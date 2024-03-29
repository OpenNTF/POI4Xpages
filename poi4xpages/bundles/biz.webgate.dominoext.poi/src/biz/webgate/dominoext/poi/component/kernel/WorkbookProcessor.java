/**
 * Copyright (c) 2012-2021 WebGate Consulting AG and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.webapp.XspHttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UIWorkbook;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.IListDataExporter;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellBookmark;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.PoiCellStyle;
import biz.webgate.dominoext.poi.component.kernel.util.MethodExecutor;
import biz.webgate.dominoext.poi.component.kernel.workbook.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.workbook.POICellStyleProcessor;
import biz.webgate.dominoext.poi.component.kernel.workbook.XPagesDataSourceExportProcessor;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public enum WorkbookProcessor {
	INSTANCE;

	public static WorkbookProcessor getInstance() {
		return WorkbookProcessor.INSTANCE;
	}

	public void generateNewFile(ITemplateSource itsCurrent, HttpServletResponse httpResponse, FacesContext context, UIWorkbook uiWB, boolean noDownload, MethodBinding preDownload) {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		List<Spreadsheet> lstSP = uiWB.getSpreadsheets();
		String strFileName = uiWB.getDownloadFileName();
		try {
			logCurrent.finer("First getting the File");
			// First getting the File
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				Workbook wbCurrent = processWorkbook(itsCurrent, lstSP, context, uiWB);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				wbCurrent.write(bos);

				MethodExecutor.INSTANCE.execute(preDownload, uiWB, context, bos);
				if (!noDownload) {
					logCurrent.finer("Push the Result to the HttpServlet");
					// Push the Result to the HttpServlet
					if (strFileName.toLowerCase().endsWith(".xlsx")) {
						httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
					} else if (strFileName.toLowerCase().endsWith("xls")) {
						httpResponse.setContentType("application/vnd.ms-excel");

					} else {
						httpResponse.setContentType("application/octet-stream");
					}
					httpResponse.addHeader("Content-disposition", "inline; filename=\"" + strFileName + "\"");
					OutputStream os = httpResponse.getOutputStream();
					bos.writeTo(os);
					os.close();
				} else {
					OutputStream os = httpResponse.getOutputStream();
					os.close();
				}
				wbCurrent.close();
			} else {
				ErrorPageBuilder.getInstance().processError(httpResponse, "TemplateAccess Problem NR: " + nTemplateAccess, null);
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "Error during Workbookgeneration", e);
		}

	}

	public Workbook processWorkbook(ITemplateSource itsCurrent, List<Spreadsheet> lstSP, FacesContext context, UIWorkbook uiWB) throws IOException, InvalidFormatException, POIException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		InputStream is = itsCurrent.getFileStream();
		// Workbook wbCurrent = WorkbookFactory.create(is);
		// ;
		Workbook wbCurrent1 = WorkbookFactory.create(is);
		Workbook wbCurrent = wbCurrent1;
		is.close();
		if (wbCurrent1 instanceof XSSFWorkbook && uiWB.isUseStreamingModel()) {
			logCurrent.info("Generation SXSSFWorkbook");
			wbCurrent = new SXSSFWorkbook((XSSFWorkbook) wbCurrent1);
		}
		itsCurrent.cleanUP();
		// Processing all Spreadsheets to the File
		logCurrent.finer("Push the Result to the HttpServlet");

		for (Spreadsheet spCurrent : lstSP) {
			processSpreadSheet(spCurrent, wbCurrent, context);
		}
		if (uiWB != null) {
			logCurrent.finer("Post Generation Process");
			uiWB.postGenerationProcess(context, wbCurrent);
		}
		return wbCurrent;
	}

	private void processSpreadSheet(Spreadsheet spCurrent, Workbook wbCurrent, FacesContext context) throws POIException {
		// Checking for Replacement Values
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		logCurrent.finer("Proccess Spread Sheet");
		String strName = spCurrent.getName();
		Sheet shProcess = wbCurrent.getSheet(strName);
		if (shProcess == null && !spCurrent.isCreate()) {
			return;
		}
		if (shProcess == null) {
			shProcess = wbCurrent.createSheet(strName);
		}

		logCurrent.finer("Proccess Cell Values");
		if (spCurrent.getCellValues() != null) {
			for (ICellValue iCV : spCurrent.getCellValues()) {
				if (iCV instanceof CellBookmark) {
					CellBookmark cb = (CellBookmark) iCV;
					if (StringUtil.isNotEmpty(cb.getName())) {
						findAndReplaceAll(shProcess, "<<" + cb.getName() + ">>", cb.getValue());
					}
				}
				if (iCV instanceof CellValue) {
					CellValue cv = (CellValue) iCV;
					setCellValue(shProcess, cv.getRowNumber(), cv.getColumnNumber(), cv.getValue(), cv.isCellFormula(), cv.getPoiCellStyle());
				}
			}
		}

		logCurrent.finer("Proccess ExportDefinition");
		if (spCurrent.getExportDefinitions() != null) {
			for (IListDataExporter lstExport : spCurrent.getExportDefinitions()) {
				if (lstExport instanceof Data2ColumnExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor.getInstance().processExportCol((Data2ColumnExporter) lstExport, shProcess, context, ((Data2ColumnExporter) lstExport).getVar(),
								((Data2ColumnExporter) lstExport).getIndex());
					} else {
						XPagesDataSourceExportProcessor.getInstances().processExportCol((Data2ColumnExporter) lstExport, shProcess, context, ((Data2ColumnExporter) lstExport).getVar(),
								((Data2ColumnExporter) lstExport).getIndex());
					}
				} else if (lstExport instanceof Data2RowExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor.getInstance().processExportRow((Data2RowExporter) lstExport, shProcess, context, ((Data2RowExporter) lstExport).getVar(),
								((Data2RowExporter) lstExport).getIndex());
					} else {
						XPagesDataSourceExportProcessor.getInstances().processExportRow((Data2RowExporter) lstExport, shProcess, context, ((Data2RowExporter) lstExport).getVar(),
								((Data2RowExporter) lstExport).getIndex());
					}

				}
			}
		}
	}

	public void setCellValue(Sheet shProcess, int nRow, int nCol, Object objValue, boolean isFormula, PoiCellStyle pCellStyle) {
		try {
			Row rw = shProcess.getRow(nRow);
			if (rw == null) {
				rw = shProcess.createRow(nRow);
			}
			Cell c = rw.getCell(nCol);
			if (c == null) {
				c = rw.createCell(nCol);
			}
			if (isFormula) {
				c.setCellFormula((String) objValue);
			} else if (objValue instanceof Double) {
				c.setCellValue((Double) objValue);
			} else if (objValue instanceof Integer) {
				c.setCellValue((Integer) objValue);
			} else {
				if (objValue instanceof Date) {
					c.setCellValue((Date) objValue);
				} else {
					c.setCellValue("" + objValue);
				}
			}
			if (pCellStyle != null) {
				CellStyle style = POICellStyleProcessor.INSTANCE.buildStyle(shProcess, pCellStyle);
				c.setCellStyle(style);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findAndReplaceAll(Sheet sheet, String find, Object replace) {
		if (replace == null) {
			replace = "";
		}
		int iLastRow = sheet.getLastRowNum();
		for (int i1 = 0; i1 < iLastRow; i1++) {
			Row currentRow = sheet.getRow(i1);
			if (currentRow != null) {
				int iLastCell = currentRow.getLastCellNum();
				for (int i = 0; i < iLastCell; i++) {
					Cell currentCell = currentRow.getCell(i);
					if (currentCell != null && currentCell.getCellType() == CellType.STRING) {
						if (currentCell.getStringCellValue().contains(find)) {
							currentCell.setCellValue(currentCell.getStringCellValue().replace(find, "" + replace));
						}
					}
				}
			}
		}
	}

	public void processCall(FacesContext context, UIWorkbook uiWorkbook, boolean noDownload, MethodBinding preDownload) {
		HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();

		// Disable the XPages response buffer as this will collide with the
		// engine one
		// We mark it as committed and use its delegate instead

		if (httpResponse instanceof XspHttpServletResponse) {
			XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
			r.setCommitted(true);
			httpResponse = r.getDelegate();
		}

		ITemplateSource itsCurrent = uiWorkbook.getTemplateSource();
		if (itsCurrent == null) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "No Templatesource found!", null);
			return;
		}
		try {
			WorkbookProcessor.INSTANCE.generateNewFile(itsCurrent, httpResponse, context, uiWorkbook, noDownload, preDownload);
		} catch (Exception e) {
			try {
				e.printStackTrace();
				e.printStackTrace(httpResponse.getWriter());
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e);
			} catch (Exception e2) {
				e2.printStackTrace();
				e.printStackTrace();
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e2);
			}
		}

	}
}
