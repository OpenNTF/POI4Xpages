/*
 * © Copyright WebGate Consulting AG, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.IListDataExporter;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellBookmark;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;
import biz.webgate.dominoext.poi.component.kernel.workbook.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.workbook.XPagesDataSourceExportProcessor;
import biz.webgate.dominoext.poi.util.ErrorPageBuilder;

import com.ibm.commons.util.StringUtil;

public class WorkbookProcessor {

	private static WorkbookProcessor m_Processor;

	private WorkbookProcessor() {

	}

	public static WorkbookProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new WorkbookProcessor();
		}
		return m_Processor;
	}

	public void generateNewFile(ITemplateSource itsCurrent,
			List<Spreadsheet> lstSP, String strFileName,
			HttpServletResponse httpResponse, FacesContext context) {

		try {
			// First getting the File
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				Workbook wbCurrent = processWorkbook(itsCurrent, lstSP, context);

				// Push the Result to the HttpServlet
				httpResponse.setContentType("application/octet-stream");
				httpResponse.addHeader("Content-disposition",
						"inline; filename=\"" + strFileName + "\"");
				OutputStream os = httpResponse.getOutputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				wbCurrent.write(bos);
				bos.writeTo(os);
				os.close();
			} else {
				ErrorPageBuilder.getInstance().processError(httpResponse,
						"TemplateAccess Problem NR: " + nTemplateAccess, null);
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse,
					"Error during Workbookgeneration", e);
		}

	}

	public Workbook processWorkbook(ITemplateSource itsCurrent,
			List<Spreadsheet> lstSP, FacesContext context) throws IOException,
			InvalidFormatException, POIException {
		InputStream is = itsCurrent.getFileStream();
		Workbook wbCurrent = WorkbookFactory.create(is);
		// ;
		itsCurrent.cleanUP();
		// Processing all Spreadsheets to the File
		for (Spreadsheet spCurrent : lstSP) {
			processSpreadSheet(spCurrent, wbCurrent, context);
		}
		return wbCurrent;
	}

	private void processSpreadSheet(Spreadsheet spCurrent, Workbook wbCurrent,
			FacesContext context) throws POIException {
		// Checking for Replacement Values

		String strName = spCurrent.getName();
		Sheet shProcess = wbCurrent.getSheet(strName);
		if (shProcess == null && !spCurrent.isCreate()) {
			throw new POIException("No Sheet found with name " + strName);
		}
		if (shProcess == null) {
			shProcess = wbCurrent.createSheet(strName);
		}
		if (spCurrent.getCellValues() != null) {
			for (ICellValue iCV : spCurrent.getCellValues()) {
				if (iCV instanceof CellBookmark) {
					CellBookmark cb = (CellBookmark) iCV;
					if (StringUtil.isNotEmpty(cb.getName())) {
						findAndReplaceAll(shProcess,
								"<<" + cb.getName() + ">>", cb.getValue());
					}
				}
				if (iCV instanceof CellValue) {
					CellValue cv = (CellValue) iCV;
					setCellValue(shProcess, cv.getRowNumber(),
							cv.getColumnNumber(), cv.getValue());
				}
			}
		}
		if (spCurrent.getExportDefinitions() != null) {
			for (IListDataExporter lstExport : spCurrent.getExportDefinitions()) {
				if (lstExport instanceof Data2ColumnExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor.getInstance()
								.processExportCol(
										(Data2ColumnExporter) lstExport,
										shProcess, context);
					} else {
						XPagesDataSourceExportProcessor.getInstances()
								.processExportCol(
										(Data2ColumnExporter) lstExport,
										shProcess, context);
					}
				}
				if (lstExport instanceof Data2RowExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor.getInstance()
								.processExportRow((Data2RowExporter) lstExport,
										shProcess, context);
					} else {
						XPagesDataSourceExportProcessor.getInstances()
								.processExportRow((Data2RowExporter) lstExport,
										shProcess, context);
					}

				}
			}
		}
	}

	public static void setCellValue(Sheet shProcess, int nRow, int nCol,
			Object objValue) {
		try {
			Row rw = shProcess.getRow(nRow);
			if (rw == null) {
				rw = shProcess.createRow(nRow);
			}
			Cell c = rw.getCell(nCol);
			if (c == null) {
				c = rw.createCell(nCol);
			}
			if (objValue instanceof Double) {
				c.setCellValue((Double) objValue);
			} else {
				if (objValue instanceof Date) {
					c.setCellValue((Date) objValue);
				} else {
					c.setCellValue("" + objValue);
				}
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
					if (currentCell != null
							&& currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						if (currentCell.getStringCellValue().contains(find)) {
							currentCell.setCellValue(currentCell
									.getStringCellValue().replace(find,
											"" + replace));
						}
					}
				}
			}
		}
	}
}
