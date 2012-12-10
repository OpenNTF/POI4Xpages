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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.IListDataExporter;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellBookmark;
import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;
import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.AbstractDataSource;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.model.ModelDataSource;
import com.ibm.xsp.model.TabularDataModel;

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
				InputStream is = itsCurrent.getFileStream();
				Workbook wbCurrent = WorkbookFactory.create(is);
				// ;
				itsCurrent.cleanUP();
				// Processing all Spreadsheets to the File
				for (Spreadsheet spCurrent : lstSP) {
					processSpreadSheet(spCurrent, wbCurrent, context);
				}

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
				httpResponse.setContentType("text/plain");
				PrintWriter pw = httpResponse.getWriter();
				pw.println("POI 4 XPages -> Error during Workbook Generation");
				pw.println("nr:             " + nTemplateAccess);
				pw.println("Class: " + itsCurrent.getClass());
				pw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processSpreadSheet(Spreadsheet spCurrent, Workbook wbCurrent,
			FacesContext context) {
		// Checking for Replacement Values

		String strName = spCurrent.getName();
		Sheet shProcess = wbCurrent.getSheet(strName);
		if (shProcess == null && !spCurrent.isCreate()) {
			// There is not a sheet to fillout, lets leave
			return;
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
			}
		}
		// Now doing the DataSources
		if (spCurrent.getExportDefinitions() != null) {
			for (IListDataExporter lstExport : spCurrent.getExportDefinitions()) {
				if (lstExport instanceof Data2ColumnExporter) {
					processExportCol((Data2ColumnExporter) lstExport,
							shProcess, context);
				}
				if (lstExport instanceof Data2RowExporter) {
					processExportRow((Data2RowExporter) lstExport, shProcess,
							context);

				}
			}
		}
	}

	private void processExportRow(Data2RowExporter lstExport, Sheet shProcess,
			FacesContext context) {
		DataSource dsCurrent = lstExport.getDataSource();
		TabularDataModel tdm = getTDM(dsCurrent, context);
		if (tdm == null) {
			System.out.println("NIX TABULARDATAMODEL");
			return;
		}
		int nRow = lstExport.getStartRow();
		int nStepSize = lstExport.getStepSize();
		for (int nCount = 0; nCount < tdm.getRowCount(); nCount++) {
			tdm.setRowIndex(nCount);
			if (tdm.isRowAvailable()) {
				for (ColumnDefinition clDef : lstExport.getColumns()) {
					int nCol = clDef.getColumnNumber();
					int nMyRow = clDef.getRowShift() + nRow;
					Object objCurrent = clDef.getValue();
					Row row = shProcess.getRow(nMyRow);
					if (row == null) {
						row = shProcess.createRow(nMyRow);
					}
					Cell cl = row.getCell(nCol);
					if (cl == null) {
						cl = row.createCell(nCol);
					}
					if (objCurrent instanceof Double) {
						cl.setCellValue(((Double) objCurrent).doubleValue());
					} else {
						cl.setCellValue("" + objCurrent);
					}
				}
				nRow = nRow + nStepSize;
			}
		}
	}

	private TabularDataModel getTDM(DataSource dsCurrent, FacesContext context) {
		try {
			if (dsCurrent instanceof ModelDataSource) {
				ModelDataSource mds = (ModelDataSource) dsCurrent;
				AbstractDataSource ads = (AbstractDataSource) mds;
				ads.load(context);
				System.out.println(ads.getBeanId());
				if (ads.getBeanId() == null) {

				}
				DataModel tdm = mds.getDataModel();
				if (tdm instanceof TabularDataModel) {
					TabularDataModel tds = (TabularDataModel) tdm;
					return tds;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void processExportCol(Data2ColumnExporter lstExport, Sheet shProcess, FacesContext context) {
		DataSource dsCurrent = lstExport.getDataSource();
		TabularDataModel tdm = getTDM(dsCurrent, context);
		if (tdm == null) {
			System.out.println("NIX TABULARDATAMODEL");
			return;
		}
		int nCol = lstExport.getStartColumn();
		int nStepSize = lstExport.getStepSize();
		for (int nCount = 0; nCount < tdm.getRowCount(); nCount++) {
			tdm.setRowIndex(nCount);
			if (tdm.isRowAvailable()) {
				for (RowDefinition rdDef : lstExport.getRows()) {
					int nMyCol = rdDef.getColumnShift() + nCol;
					int nRow = rdDef.getRowNumber();
					Object objCurrent = rdDef.getValue();
					Row row = shProcess.getRow(nRow);
					if (row == null) {
						row = shProcess.createRow(nRow);
					}
					Cell cl = row.getCell(nMyCol);
					if (cl == null) {
						cl = row.createCell(nMyCol);
					}
					if (objCurrent instanceof Double) {
						cl.setCellValue(((Double) objCurrent).doubleValue());
					} else {
						cl.setCellValue("" + objCurrent);
					}
				}
				nCol = nCol + nStepSize;
			}
		}
	}

	public void findAndReplaceAll(Sheet sheet, String find, Object replace) {
		if (replace == null) {
			replace = "";
		}
		int iLastRow = sheet.getLastRowNum();
		for (int i1 = 0; i1 < iLastRow; i1++) {
			Row currentRow = sheet.getRow(i1);
			int iLastCell = currentRow.getLastCellNum();
			for (int i = 0; i < iLastCell; i++) {
				Cell currentCell = currentRow.getCell(i);
				if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
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
