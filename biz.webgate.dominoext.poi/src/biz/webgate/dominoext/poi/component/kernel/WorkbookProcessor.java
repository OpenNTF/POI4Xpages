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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import biz.webgate.dominoext.poi.utils.exceptions.POIException;
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
import biz.webgate.dominoext.poi.component.kernel.workbook.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.workbook.XPagesDataSourceExportProcessor;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.commons.util.StringUtil;

public class WorkbookProcessor {

	private static HashMap<String, Short> m_StyleConstantValues;
	private static HashMap<String, Byte> m_StyleByteConstantValues;

	private static WorkbookProcessor m_Processor;

	private WorkbookProcessor() {

	}

	public static WorkbookProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new WorkbookProcessor();
		}
		return m_Processor;
	}

	public static void getStyleConstantValues() {
		if (m_StyleConstantValues == null) {
			m_StyleConstantValues = new HashMap<String, Short>();
			m_StyleConstantValues.put("ALIGN_CENTER", CellStyle.ALIGN_CENTER);
			m_StyleConstantValues.put("ALIGN_CENTER_SELECTION",
					CellStyle.ALIGN_CENTER_SELECTION);
			m_StyleConstantValues.put("ALIGN_FILL", CellStyle.ALIGN_FILL);
			m_StyleConstantValues.put("ALIGN_GENERAL", CellStyle.ALIGN_GENERAL);
			m_StyleConstantValues.put("ALIGN_JUSTIFY", CellStyle.ALIGN_JUSTIFY);
			m_StyleConstantValues.put("ALIGN_LEFT", CellStyle.ALIGN_LEFT);
			m_StyleConstantValues.put("ALIGN_RIGHT", CellStyle.ALIGN_RIGHT);

			m_StyleConstantValues.put("BORDER_DASH_DOT",
					CellStyle.BORDER_DASH_DOT);
			m_StyleConstantValues.put("BORDER_DASH_DOT_DOT",
					CellStyle.BORDER_DASH_DOT_DOT);
			m_StyleConstantValues.put("BORDER_DASHED", CellStyle.BORDER_DASHED);
			m_StyleConstantValues.put("BORDER_DOTTED", CellStyle.BORDER_DOTTED);
			m_StyleConstantValues.put("BORDER_DOUBLE", CellStyle.BORDER_DOUBLE);
			m_StyleConstantValues.put("BORDER_HAIR", CellStyle.BORDER_HAIR);
			m_StyleConstantValues.put("BORDER_MEDIUM", CellStyle.BORDER_MEDIUM);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASH_DOT",
					CellStyle.BORDER_MEDIUM_DASH_DOT);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASH_DOT_DOT",
					CellStyle.BORDER_MEDIUM_DASH_DOT_DOT);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASHED",
					CellStyle.BORDER_MEDIUM_DASHED);
			m_StyleConstantValues.put("BORDER_NONE", CellStyle.BORDER_NONE);
			m_StyleConstantValues.put("BORDER_SLANTED_DASH_DOT",
					CellStyle.BORDER_SLANTED_DASH_DOT);
			m_StyleConstantValues.put("BORDER_THICK", CellStyle.BORDER_THICK);
			m_StyleConstantValues.put("BORDER_THIN", CellStyle.BORDER_THIN);

			m_StyleConstantValues.put("ALT_BARS", CellStyle.ALT_BARS);
			m_StyleConstantValues.put("BIG_SPOTS", CellStyle.BIG_SPOTS);
			m_StyleConstantValues.put("BRICKS", CellStyle.BRICKS);
			m_StyleConstantValues.put("DIAMONDS", CellStyle.DIAMONDS);
			m_StyleConstantValues.put("FINE_DOTS", CellStyle.FINE_DOTS);
			m_StyleConstantValues.put("LEAST_DOTS", CellStyle.LEAST_DOTS);
			m_StyleConstantValues.put("LESS_DOTS", CellStyle.LESS_DOTS);
			m_StyleConstantValues.put("NO_FILL", CellStyle.NO_FILL);
			m_StyleConstantValues.put("SOLID_FOREGROUND",
					CellStyle.SOLID_FOREGROUND);
			m_StyleConstantValues.put("SPARSE_DOTS", CellStyle.SPARSE_DOTS);
			m_StyleConstantValues.put("SQUARES", CellStyle.SQUARES);
			m_StyleConstantValues.put("THICK_BACKWARD_DIAG",
					CellStyle.THICK_BACKWARD_DIAG);
			m_StyleConstantValues.put("THICK_FORWARD_DIAG",
					CellStyle.THICK_FORWARD_DIAG);
			m_StyleConstantValues.put("THICK_HORZ_BANDS",
					CellStyle.THICK_HORZ_BANDS);
			m_StyleConstantValues.put("THICK_VERT_BANDS",
					CellStyle.THICK_VERT_BANDS);
			m_StyleConstantValues.put("THIN_BACKWARD_DIAG",
					CellStyle.THIN_BACKWARD_DIAG);
			m_StyleConstantValues.put("THIN_FORWARD_DIAG",
					CellStyle.THIN_FORWARD_DIAG);
			m_StyleConstantValues.put("THIN_HORZ_BANDS",
					CellStyle.THIN_HORZ_BANDS);
			m_StyleConstantValues.put("THIN_VERT_BANDS",
					CellStyle.THIN_VERT_BANDS);

			m_StyleConstantValues.put("VERTICAL_BOTTOM",
					CellStyle.VERTICAL_BOTTOM);
			m_StyleConstantValues.put("VERTICAL_CENTER",
					CellStyle.VERTICAL_CENTER);
			m_StyleConstantValues.put("VERTICAL_JUSTIFY",
					CellStyle.VERTICAL_JUSTIFY);
			m_StyleConstantValues.put("VERTICAL_TOP", CellStyle.VERTICAL_TOP);

			m_StyleConstantValues.put("SS_NONE", Font.SS_NONE);
			m_StyleConstantValues.put("SS_SUPER", Font.SS_SUPER);
			m_StyleConstantValues.put("SS_SUB", Font.SS_SUB);

			m_StyleConstantValues.put("BOLDWEIGHT_BOLD", Font.BOLDWEIGHT_BOLD);
			m_StyleConstantValues.put("BOLDWEIGHT_NORMAL",
					Font.BOLDWEIGHT_NORMAL);

		}

		if (m_StyleByteConstantValues == null) {
			m_StyleByteConstantValues = new HashMap<String, Byte>();
			m_StyleByteConstantValues.put("U_NONE", Font.U_NONE);
			m_StyleByteConstantValues.put("U_SINGLE", Font.U_SINGLE);
			m_StyleByteConstantValues.put("U_DOUBLE", Font.U_DOUBLE);
			m_StyleByteConstantValues.put("U_SINGLE_ACCOUNTING",
					Font.U_SINGLE_ACCOUNTING);
			m_StyleByteConstantValues.put("U_DOUBLE_ACCOUNTING",
					Font.U_DOUBLE_ACCOUNTING);
		}
	}

	public void generateNewFile(ITemplateSource itsCurrent,
			List<Spreadsheet> lstSP, String strFileName,
			HttpServletResponse httpResponse, FacesContext context,
			UIWorkbook uiWB) {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());

		try {
			logCurrent.finer("First getting the File");
			// First getting the File
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				Workbook wbCurrent = processWorkbook(itsCurrent, lstSP,
						context, uiWB);

				logCurrent.finer("Push the Result to the HttpServlet");
				// Push the Result to the HttpServlet
				if (strFileName.toLowerCase().endsWith(".xlsx")) {
					httpResponse
							.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				} else if (strFileName.toLowerCase().endsWith("xls")) {
					httpResponse.setContentType("application/vnd.ms-excel");

				} else {
					httpResponse.setContentType("application/octet-stream");
				}
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
			List<Spreadsheet> lstSP, FacesContext context, UIWorkbook uiWB)
			throws IOException, InvalidFormatException, POIException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());

		InputStream is = itsCurrent.getFileStream();
		// Workbook wbCurrent = WorkbookFactory.create(is);
		// ;
		Workbook wbCurrent1 = WorkbookFactory.create(is);
		Workbook wbCurrent = wbCurrent1;
		is.close();
		if (wbCurrent1 instanceof XSSFWorkbook) {
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

	private void processSpreadSheet(Spreadsheet spCurrent, Workbook wbCurrent,
			FacesContext context) throws POIException {
		// Checking for Replacement Values
		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());

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
						findAndReplaceAll(shProcess,
								"<<" + cb.getName() + ">>", cb.getValue());
					}
				}
				if (iCV instanceof CellValue) {
					CellValue cv = (CellValue) iCV;
					setCellValue(shProcess, cv.getRowNumber(),
							cv.getColumnNumber(), cv.getValue(),
							cv.isCellFormula(), cv.getPoiCellStyle());
				}
			}
		}

		logCurrent.finer("Proccess ExportDefinition");
		if (spCurrent.getExportDefinitions() != null) {
			for (IListDataExporter lstExport : spCurrent.getExportDefinitions()) {
				if (lstExport instanceof Data2ColumnExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor.getInstance()
								.processExportCol(
										(Data2ColumnExporter) lstExport,
										shProcess,
										context,
										((Data2ColumnExporter) lstExport)
												.getVar(),
										((Data2ColumnExporter) lstExport)
												.getIndex());
					} else {
						XPagesDataSourceExportProcessor.getInstances()
								.processExportCol(
										(Data2ColumnExporter) lstExport,
										shProcess,
										context,
										((Data2ColumnExporter) lstExport)
												.getVar(),
										((Data2ColumnExporter) lstExport)
												.getIndex());
					}
				} else if (lstExport instanceof Data2RowExporter) {
					if (lstExport.getDataSource() != null) {
						EmbeddedDataSourceExportProcessor
								.getInstance()
								.processExportRow(
										(Data2RowExporter) lstExport,
										shProcess,
										context,
										((Data2RowExporter) lstExport).getVar(),
										((Data2RowExporter) lstExport)
												.getIndex());
					} else {
						XPagesDataSourceExportProcessor
								.getInstances()
								.processExportRow(
										(Data2RowExporter) lstExport,
										shProcess,
										context,
										((Data2RowExporter) lstExport).getVar(),
										((Data2RowExporter) lstExport)
												.getIndex());
					}

				}
			}
		}
	}

	public static void setCellValue(Sheet shProcess, int nRow, int nCol,
			Object objValue, boolean isFormula, PoiCellStyle pCellStyle) {
		// Logger logCurrent =
		// LoggerFactory.getLogger(WorkbookProcessor.class.getCanonicalName());

		try {
			Row rw = shProcess.getRow(nRow);
			if (rw == null) {
				// logCurrent.finest("Create Row");
				rw = shProcess.createRow(nRow);
			}
			// Cell c = rw.getCell(nCol);
			// if (c == null) {
			// logCurrent.finest("Create Cell");
			Cell c = rw.createCell(nCol);
			// }
			if (isFormula) {
				c.setCellFormula((String) objValue);
			} else {
				if (objValue instanceof Double) {
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
			}
			// *** STYLE CONFIG Since V 1.1.7 ***

			if (pCellStyle != null) {
				getStyleConstantValues();
				CellStyle style = shProcess.getWorkbook().createCellStyle();

				if (pCellStyle.getAlignment() != null)
					style.setAlignment(m_StyleConstantValues.get(pCellStyle
							.getAlignment()));

				if (pCellStyle.getBorderBottom() != null)
					style.setBorderBottom(m_StyleConstantValues.get(pCellStyle
							.getBorderBottom()));

				if (pCellStyle.getBorderLeft() != null)
					style.setBorderLeft(m_StyleConstantValues.get(pCellStyle
							.getBorderLeft()));

				if (pCellStyle.getBorderRight() != null)
					style.setBorderRight(m_StyleConstantValues.get(pCellStyle
							.getBorderRight()));

				if (pCellStyle.getBorderTop() != null)
					style.setBorderTop(m_StyleConstantValues.get(pCellStyle
							.getBorderTop()));

				if (pCellStyle.getBottomBorderColor() != null)
					style.setBottomBorderColor(IndexedColors.valueOf(
							pCellStyle.getBottomBorderColor()).getIndex());

				if (pCellStyle.getDataFormat() != null) {
					DataFormat format = shProcess.getWorkbook()
							.createDataFormat();
					style.setDataFormat(format.getFormat(pCellStyle
							.getDataFormat()));
				}

				if (pCellStyle.getFillBackgroundColor() != null)
					style.setFillBackgroundColor(IndexedColors.valueOf(
							pCellStyle.getFillBackgroundColor()).getIndex());

				if (pCellStyle.getFillForegroundColor() != null)
					style.setFillForegroundColor(IndexedColors.valueOf(
							pCellStyle.getFillForegroundColor()).getIndex());

				if (pCellStyle.getFillPattern() != null)
					style.setFillPattern(m_StyleConstantValues.get(pCellStyle
							.getFillPattern()));

				// Create a new font and alter it.
				Font font = shProcess.getWorkbook().createFont();

				if (pCellStyle.getFontBoldweight() != null)
					font.setBoldweight(m_StyleConstantValues.get(pCellStyle
							.getFontBoldweight()));

				if (pCellStyle.getFontColor() != null)
					font.setColor(IndexedColors.valueOf(
							pCellStyle.getFontColor()).getIndex());

				if (pCellStyle.getFontHeightInPoints() != 0)
					font.setFontHeightInPoints(pCellStyle
							.getFontHeightInPoints());

				if (pCellStyle.getFontName() != null)
					font.setFontName(pCellStyle.getFontName());

				if (pCellStyle.isFontItalic())
					font.setItalic(pCellStyle.isFontItalic());

				if (pCellStyle.isFontStrikeout())
					font.setStrikeout(pCellStyle.isFontStrikeout());

				if (pCellStyle.getFontUnderline() != null)
					font.setUnderline(m_StyleByteConstantValues.get(pCellStyle
							.getFontUnderline()));

				if (pCellStyle.getFontTypeOffset() != null)
					font.setTypeOffset(m_StyleConstantValues.get(pCellStyle
							.getFontTypeOffset()));

				// Set Font
				style.setFont(font);

				if (pCellStyle.isHidden())
					style.setHidden(pCellStyle.isHidden());

				if (pCellStyle.getIndention() != null)
					style.setIndention(m_StyleConstantValues.get(pCellStyle
							.getIndention()));

				if (pCellStyle.getLeftBorderColor() != null)
					style.setLeftBorderColor(IndexedColors.valueOf(
							pCellStyle.getLeftBorderColor()).getIndex());

				if (pCellStyle.isLocked())
					style.setLocked(pCellStyle.isLocked());

				if (pCellStyle.getRightBorderColor() != null)
					style.setRightBorderColor(IndexedColors.valueOf(
							pCellStyle.getRightBorderColor()).getIndex());

				if (pCellStyle.getRotation() != 0)
					style.setRotation(pCellStyle.getRotation());

				if (pCellStyle.getTopBorderColor() != null)
					style.setTopBorderColor(IndexedColors.valueOf(
							pCellStyle.getTopBorderColor()).getIndex());

				if (pCellStyle.getVerticalAlignment() != null)
					style.setVerticalAlignment(m_StyleConstantValues
							.get(pCellStyle.getVerticalAlignment()));

				if (pCellStyle.isWrapText())
					style.setWrapText(pCellStyle.isWrapText());

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
