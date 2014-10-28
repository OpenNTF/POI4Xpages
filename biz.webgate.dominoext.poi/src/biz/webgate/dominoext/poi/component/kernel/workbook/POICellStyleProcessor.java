package biz.webgate.dominoext.poi.component.kernel.workbook;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import biz.webgate.dominoext.poi.component.data.ss.cell.PoiCellStyle;

public enum POICellStyleProcessor {
	INSTANCE;

	private HashMap<String, Short> m_StyleConstantValues;
	private HashMap<String, Byte> m_StyleByteConstantValues;

	public CellStyle buildStyle(Sheet sheet, PoiCellStyle pCellStyle) {
		checkStyleConstantValues();
		CellStyle style = sheet.getWorkbook().createCellStyle();

		// Create a new font and alter it.
		Font font = sheet.getWorkbook().createFont();

		if (pCellStyle.getAlignment() != null)
			style.setAlignment(m_StyleConstantValues.get(pCellStyle.getAlignment()));

		if (pCellStyle.getBorderBottom() != null)
			style.setBorderBottom(m_StyleConstantValues.get(pCellStyle.getBorderBottom()));

		if (pCellStyle.getBorderLeft() != null)
			style.setBorderLeft(m_StyleConstantValues.get(pCellStyle.getBorderLeft()));

		if (pCellStyle.getBorderRight() != null)
			style.setBorderRight(m_StyleConstantValues.get(pCellStyle.getBorderRight()));

		if (pCellStyle.getBorderTop() != null)
			style.setBorderTop(m_StyleConstantValues.get(pCellStyle.getBorderTop()));

		if (pCellStyle.getDataFormat() != null) {
			DataFormat format = sheet.getWorkbook().createDataFormat();
			style.setDataFormat(format.getFormat(pCellStyle.getDataFormat()));
		}

		if (pCellStyle.getFillPattern() != null)
			style.setFillPattern(m_StyleConstantValues.get(pCellStyle.getFillPattern()));

		if (pCellStyle.getFontBoldweight() != null)
			font.setBoldweight(m_StyleConstantValues.get(pCellStyle.getFontBoldweight()));

		if (pCellStyle.getFontHeightInPoints() != 0)
			font.setFontHeightInPoints(pCellStyle.getFontHeightInPoints());

		if (pCellStyle.getFontName() != null)
			font.setFontName(pCellStyle.getFontName());

		if (pCellStyle.isFontItalic())
			font.setItalic(pCellStyle.isFontItalic());

		if (pCellStyle.isFontStrikeout())
			font.setStrikeout(pCellStyle.isFontStrikeout());

		if (pCellStyle.getFontUnderline() != null)
			font.setUnderline(m_StyleByteConstantValues.get(pCellStyle.getFontUnderline()));

		if (pCellStyle.getFontTypeOffset() != null)
			font.setTypeOffset(m_StyleConstantValues.get(pCellStyle.getFontTypeOffset()));

		// Set Font
		style.setFont(font);

		if (pCellStyle.isHidden())
			style.setHidden(pCellStyle.isHidden());

		if (pCellStyle.getIndention() != null)
			style.setIndention(m_StyleConstantValues.get(pCellStyle.getIndention()));

		if (pCellStyle.isLocked())
			style.setLocked(pCellStyle.isLocked());

		if (pCellStyle.getRotation() != 0)
			style.setRotation(pCellStyle.getRotation());

		if (pCellStyle.getVerticalAlignment() != null)
			style.setVerticalAlignment(m_StyleConstantValues.get(pCellStyle.getVerticalAlignment()));

		if (pCellStyle.isWrapText())
			style.setWrapText(pCellStyle.isWrapText());

		if (sheet.getWorkbook() instanceof XSSFWorkbook || sheet.getWorkbook() instanceof SXSSFWorkbook) {
			processXSSColor((XSSFCellStyle) style, sheet, pCellStyle, font);
		} else {
			processHSFFColor(style, sheet, pCellStyle, font);
		}
		return style;
	}

	private void processHSFFColor(CellStyle style, Sheet sheet, PoiCellStyle pCellStyle, Font font) {
		if (pCellStyle.getBottomBorderColor() != null)
			style.setBottomBorderColor(IndexedColors.valueOf(pCellStyle.getBottomBorderColor()).getIndex());

		if (pCellStyle.getFillBackgroundColor() != null)
			style.setFillBackgroundColor(IndexedColors.valueOf(pCellStyle.getFillBackgroundColor()).getIndex());

		if (pCellStyle.getFillForegroundColor() != null)
			style.setFillForegroundColor(IndexedColors.valueOf(pCellStyle.getFillForegroundColor()).getIndex());
		if (pCellStyle.getFontColor() != null)
			font.setColor(IndexedColors.valueOf(pCellStyle.getFontColor()).getIndex());

		if (pCellStyle.getTopBorderColor() != null)
			style.setTopBorderColor(IndexedColors.valueOf(pCellStyle.getTopBorderColor()).getIndex());

		if (pCellStyle.getRightBorderColor() != null)
			style.setRightBorderColor(IndexedColors.valueOf(pCellStyle.getRightBorderColor()).getIndex());

		if (pCellStyle.getLeftBorderColor() != null)
			style.setLeftBorderColor(IndexedColors.valueOf(pCellStyle.getLeftBorderColor()).getIndex());

	}

	private void processXSSColor(XSSFCellStyle style, Sheet sheet, PoiCellStyle pCellStyle, Font font) {
		if (pCellStyle.getBottomBorderColor() != null)
			style.setBottomBorderColor(IndexedColors.valueOf(pCellStyle.getBottomBorderColor()).getIndex());

		if (pCellStyle.getFillBackgroundColor() != null)
			style.setFillBackgroundColor(IndexedColors.valueOf(pCellStyle.getFillBackgroundColor()).getIndex());

		if (pCellStyle.getFillForegroundColor() != null)
			style.setFillForegroundColor(IndexedColors.valueOf(pCellStyle.getFillForegroundColor()).getIndex());
		if (pCellStyle.getFontColor() != null)
			font.setColor(IndexedColors.valueOf(pCellStyle.getFontColor()).getIndex());

		if (pCellStyle.getTopBorderColor() != null)
			style.setTopBorderColor(IndexedColors.valueOf(pCellStyle.getTopBorderColor()).getIndex());

		if (pCellStyle.getRightBorderColor() != null)
			style.setRightBorderColor(IndexedColors.valueOf(pCellStyle.getRightBorderColor()).getIndex());

		if (pCellStyle.getLeftBorderColor() != null)
			style.setLeftBorderColor(IndexedColors.valueOf(pCellStyle.getLeftBorderColor()).getIndex());

	}

	private synchronized void checkStyleConstantValues() {
		if (m_StyleConstantValues == null) {
			m_StyleConstantValues = new HashMap<String, Short>();
			m_StyleConstantValues.put("ALIGN_CENTER", CellStyle.ALIGN_CENTER);
			m_StyleConstantValues.put("ALIGN_CENTER_SELECTION", CellStyle.ALIGN_CENTER_SELECTION);
			m_StyleConstantValues.put("ALIGN_FILL", CellStyle.ALIGN_FILL);
			m_StyleConstantValues.put("ALIGN_GENERAL", CellStyle.ALIGN_GENERAL);
			m_StyleConstantValues.put("ALIGN_JUSTIFY", CellStyle.ALIGN_JUSTIFY);
			m_StyleConstantValues.put("ALIGN_LEFT", CellStyle.ALIGN_LEFT);
			m_StyleConstantValues.put("ALIGN_RIGHT", CellStyle.ALIGN_RIGHT);

			m_StyleConstantValues.put("BORDER_DASH_DOT", CellStyle.BORDER_DASH_DOT);
			m_StyleConstantValues.put("BORDER_DASH_DOT_DOT", CellStyle.BORDER_DASH_DOT_DOT);
			m_StyleConstantValues.put("BORDER_DASHED", CellStyle.BORDER_DASHED);
			m_StyleConstantValues.put("BORDER_DOTTED", CellStyle.BORDER_DOTTED);
			m_StyleConstantValues.put("BORDER_DOUBLE", CellStyle.BORDER_DOUBLE);
			m_StyleConstantValues.put("BORDER_HAIR", CellStyle.BORDER_HAIR);
			m_StyleConstantValues.put("BORDER_MEDIUM", CellStyle.BORDER_MEDIUM);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASH_DOT", CellStyle.BORDER_MEDIUM_DASH_DOT);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASH_DOT_DOT", CellStyle.BORDER_MEDIUM_DASH_DOT_DOT);
			m_StyleConstantValues.put("BORDER_MEDIUM_DASHED", CellStyle.BORDER_MEDIUM_DASHED);
			m_StyleConstantValues.put("BORDER_NONE", CellStyle.BORDER_NONE);
			m_StyleConstantValues.put("BORDER_SLANTED_DASH_DOT", CellStyle.BORDER_SLANTED_DASH_DOT);
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
			m_StyleConstantValues.put("SOLID_FOREGROUND", CellStyle.SOLID_FOREGROUND);
			m_StyleConstantValues.put("SPARSE_DOTS", CellStyle.SPARSE_DOTS);
			m_StyleConstantValues.put("SQUARES", CellStyle.SQUARES);
			m_StyleConstantValues.put("THICK_BACKWARD_DIAG", CellStyle.THICK_BACKWARD_DIAG);
			m_StyleConstantValues.put("THICK_FORWARD_DIAG", CellStyle.THICK_FORWARD_DIAG);
			m_StyleConstantValues.put("THICK_HORZ_BANDS", CellStyle.THICK_HORZ_BANDS);
			m_StyleConstantValues.put("THICK_VERT_BANDS", CellStyle.THICK_VERT_BANDS);
			m_StyleConstantValues.put("THIN_BACKWARD_DIAG", CellStyle.THIN_BACKWARD_DIAG);
			m_StyleConstantValues.put("THIN_FORWARD_DIAG", CellStyle.THIN_FORWARD_DIAG);
			m_StyleConstantValues.put("THIN_HORZ_BANDS", CellStyle.THIN_HORZ_BANDS);
			m_StyleConstantValues.put("THIN_VERT_BANDS", CellStyle.THIN_VERT_BANDS);

			m_StyleConstantValues.put("VERTICAL_BOTTOM", CellStyle.VERTICAL_BOTTOM);
			m_StyleConstantValues.put("VERTICAL_CENTER", CellStyle.VERTICAL_CENTER);
			m_StyleConstantValues.put("VERTICAL_JUSTIFY", CellStyle.VERTICAL_JUSTIFY);
			m_StyleConstantValues.put("VERTICAL_TOP", CellStyle.VERTICAL_TOP);

			m_StyleConstantValues.put("SS_NONE", Font.SS_NONE);
			m_StyleConstantValues.put("SS_SUPER", Font.SS_SUPER);
			m_StyleConstantValues.put("SS_SUB", Font.SS_SUB);

			m_StyleConstantValues.put("BOLDWEIGHT_BOLD", Font.BOLDWEIGHT_BOLD);
			m_StyleConstantValues.put("BOLDWEIGHT_NORMAL", Font.BOLDWEIGHT_NORMAL);

		}

		if (m_StyleByteConstantValues == null) {
			m_StyleByteConstantValues = new HashMap<String, Byte>();
			m_StyleByteConstantValues.put("U_NONE", Font.U_NONE);
			m_StyleByteConstantValues.put("U_SINGLE", Font.U_SINGLE);
			m_StyleByteConstantValues.put("U_DOUBLE", Font.U_DOUBLE);
			m_StyleByteConstantValues.put("U_SINGLE_ACCOUNTING", Font.U_SINGLE_ACCOUNTING);
			m_StyleByteConstantValues.put("U_DOUBLE_ACCOUNTING", Font.U_DOUBLE_ACCOUNTING);
		}
	}

}
