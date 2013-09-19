/*
 * © Copyright WebGate Consulting AG, 2013
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
package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import lotus.domino.DateTime;
import lotus.domino.ViewColumn;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

public class WorkbooklExportProcessor implements IExportProcessor {

	private static WorkbooklExportProcessor m_Processor;

	private WorkbooklExportProcessor() {

	}

	public static WorkbooklExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new WorkbooklExportProcessor();
		}
		return m_Processor;
	}

	public void process2HTTP(ExportModel expModel, UISimpleViewExport uis, HttpServletResponse hsr, DateTimeHelper dth) {
		try {
			String strFileName = uis.getDownloadFileName();

			Workbook wbCurrent = null;
			if (strFileName.toLowerCase().endsWith(".xlsx")) {
				wbCurrent = new XSSFWorkbook();
			} else {
				wbCurrent = new HSSFWorkbook();
			}
			HashMap<String, CellStyle> hsCS = new HashMap<String, CellStyle>();
			CreationHelper cr = wbCurrent.getCreationHelper();
			CellStyle csDate = wbCurrent.createCellStyle();
			csDate.setDataFormat(cr.createDataFormat().getFormat(dth.getDFDate().toPattern()));

			CellStyle csDateTime = wbCurrent.createCellStyle();
			csDateTime.setDataFormat(cr.createDataFormat().getFormat(dth.getDFDateTime().toPattern()));

			CellStyle csTime = wbCurrent.createCellStyle();
			csTime.setDataFormat(cr.createDataFormat().getFormat(dth.getDFTime().toPattern()));

			hsCS.put("DATE", csDate);
			hsCS.put("TIME", csTime);
			hsCS.put("DATETIME", csDateTime);

			Sheet sh = wbCurrent.createSheet("SVE Export");
			int nRowCount = 0;

			// BUILDING HEADER
			if (uis.isIncludeHeader()) {
				Row rw = sh.createRow(nRowCount);
				int nCol = 0;
				for (ExportColumn expColumn : expModel.getColumns()) {
					rw.createCell(nCol).setCellValue(expColumn.getColumnName());
					nCol++;
				}
				nRowCount++;
			}
			// Processing Values
			for (ExportDataRow expRow : expModel.getRows()) {
				Row rw = sh.createRow(nRowCount);
				int nCol = 0;
				for (ExportColumn expColumn : expModel.getColumns()) {
					Cell clCurrent = rw.createCell(nCol);
					setCellValue(expRow.getValue(expColumn.getPosition()), clCurrent, expColumn, hsCS);
					nCol++;
				}
				nRowCount++;
			}
			for (int nCol = 0; nCol < expModel.getColumns().size(); nCol++) {
				sh.autoSizeColumn(nCol);
			}
			if (strFileName.toLowerCase().endsWith(".xlsx")) {
				hsr.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			} else if (strFileName.toLowerCase().endsWith("xls")) {
				hsr.setContentType("application/vnd.ms-excel");

			} else {
				hsr.setContentType("application/octet-stream");
			}
			hsr.addHeader("Content-disposition", "inline; filename=\"" + strFileName + "\"");
			OutputStream os = hsr.getOutputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wbCurrent.write(bos);
			bos.writeTo(os);
			os.close();
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(hsr, "Error during SVE-Generation (Workbook Export)", e);
		}
	}

	private void setCellValue(Object objValue, Cell cellCurrent, ExportColumn expCol, HashMap<String, CellStyle> hsStyle) {
		if (objValue instanceof Double) {
			cellCurrent.setCellValue((Double) objValue);
			return;
		}
		if (objValue instanceof Integer) {
			cellCurrent.setCellValue((Integer) objValue);
			return;
		}
		if (objValue instanceof Date) {
			cellCurrent.setCellValue((Date) objValue);
			switch (expCol.getTimeDateFormat()) {
			case ViewColumn.FMT_DATE:
				cellCurrent.setCellStyle(hsStyle.get("DATE"));
				break;
			case ViewColumn.FMT_TIME:
				cellCurrent.setCellStyle(hsStyle.get("TIME"));
				break;
			default:
				cellCurrent.setCellStyle(hsStyle.get("DATETIME"));
				break;
			}
			return;
		}
		if (objValue instanceof DateTime) {
			try {
				cellCurrent.setCellValue(((DateTime) objValue).toJavaDate());
				switch (expCol.getTimeDateFormat()) {
				case ViewColumn.FMT_DATE:
					cellCurrent.setCellStyle(hsStyle.get("DATE"));
					break;
				case ViewColumn.FMT_TIME:
					cellCurrent.setCellStyle(hsStyle.get("TIME"));
					break;
				default:
					cellCurrent.setCellStyle(hsStyle.get("DATETIME"));
					break;
				}
			} catch (Exception e) {
				cellCurrent.setCellValue("" + objValue);
				e.printStackTrace();
			}
			return;
		}
		if (objValue instanceof Vector<?>) {
			Vector<?> vec = (Vector<?>) objValue;
			StringBuilder sb = new StringBuilder();
			for (Iterator<?> it = vec.iterator(); it.hasNext();) {
				sb.append("" + it.next());
				if (it.hasNext()) {
					sb.append(";");
				}
			}
			cellCurrent.setCellValue(sb.toString());
			return;
		}

		cellCurrent.setCellValue("" + objValue);

	}

}
