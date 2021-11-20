package biz.webgate.dominoext.poi.component.kernel.workbook;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import biz.webgate.dominoext.poi.component.data.ss.cell.PoiCellStyle;

public enum POICellStyleProcessor {
	INSTANCE;

	private Map<String, Short> m_StyleConstantValues;
	private Map<String, HorizontalAlignment> m_alignmentValues;
	private Map<String, BorderStyle> m_borderValues;
	private Map<String, FillPatternType> m_fillPatternValues;
	private Map<String, VerticalAlignment> m_vertAlignmentValues;
	private Map<String, Byte> m_StyleByteConstantValues;

	public CellStyle buildStyle(Sheet sheet, PoiCellStyle pCellStyle) {
		checkStyleConstantValues();
		CellStyle style = sheet.getWorkbook().createCellStyle();

		// Create a new font and alter it.
		Font font = sheet.getWorkbook().createFont();

		if (pCellStyle.getAlignment() != null)
			style.setAlignment(m_alignmentValues.get(pCellStyle.getAlignment()));

		if (pCellStyle.getBorderBottom() != null)
			style.setBorderBottom(m_borderValues.get(pCellStyle.getBorderBottom()));

		if (pCellStyle.getBorderLeft() != null)
			style.setBorderLeft(m_borderValues.get(pCellStyle.getBorderLeft()));

		if (pCellStyle.getBorderRight() != null)
			style.setBorderRight(m_borderValues.get(pCellStyle.getBorderRight()));

		if (pCellStyle.getBorderTop() != null)
			style.setBorderTop(m_borderValues.get(pCellStyle.getBorderTop()));

		if (pCellStyle.getDataFormat() != null) {
			DataFormat format = sheet.getWorkbook().createDataFormat();
			style.setDataFormat(format.getFormat(pCellStyle.getDataFormat()));
		}

		if (pCellStyle.getFillPattern() != null)
			style.setFillPattern(m_fillPatternValues.get(pCellStyle.getFillPattern()));

		if (pCellStyle.getFontBoldweight() != null)
			if("BOLDWEIGHT_BOLD".equals(pCellStyle.getFontBoldweight())) { //$NON-NLS-1$
				font.setBold(true);
			} else {
				font.setBold(false);
			}

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
			style.setVerticalAlignment(m_vertAlignmentValues.get(pCellStyle.getVerticalAlignment()));

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
			style.setBottomBorderColor(getHSFFColor(pCellStyle.getBottomBorderColor(), sheet));

		if (pCellStyle.getFillBackgroundColor() != null)
			style.setFillBackgroundColor(getHSFFColor(pCellStyle.getFillBackgroundColor(), sheet));

		if (pCellStyle.getFillForegroundColor() != null)
			style.setFillForegroundColor(getHSFFColor(pCellStyle.getFillForegroundColor(), sheet));

		if (pCellStyle.getFontColor() != null)
			font.setColor(getHSFFColor(pCellStyle.getFontColor(), sheet));

		if (pCellStyle.getTopBorderColor() != null)
			style.setTopBorderColor(getHSFFColor(pCellStyle.getTopBorderColor(), sheet));

		if (pCellStyle.getRightBorderColor() != null)
			style.setRightBorderColor(getHSFFColor(pCellStyle.getRightBorderColor(), sheet));

		if (pCellStyle.getLeftBorderColor() != null)
			style.setLeftBorderColor(getHSFFColor(pCellStyle.getLeftBorderColor(), sheet));

	}

	private short getHSFFColor(String color, Sheet sheet) {
		if (isIndexColor(color)) {
			return IndexedColors.valueOf(color).getIndex();
		}
		HSSFPalette palette = ((HSSFWorkbook) sheet.getWorkbook()).getCustomPalette();
		Color clr = Color.decode(color);
		HSSFColor myColor = palette.findSimilarColor(clr.getRed(), clr.getGreen(), clr.getBlue());
		if (myColor == null) {
			myColor = palette.addColor((byte) clr.getRed(), (byte) clr.getGreen(), (byte) clr.getBlue());
		}
		return myColor.getIndex();
	}

	private boolean isIndexColor(String colorName) {
		for (IndexedColors color : IndexedColors.values()) {
			if (color.name().equals(colorName)) {
				return true;
			}
		}
		return false;
	}

	private void processXSSColor(XSSFCellStyle style, Sheet sheet, PoiCellStyle pCellStyle, Font font) {

		if (pCellStyle.getBottomBorderColor() != null) {
			String color = pCellStyle.getBottomBorderColor();
			if (isIndexColor(color)) {
				style.setBottomBorderColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setBottomBorderColor(getXSSFColor(color));
			}
		}
		if (pCellStyle.getFillBackgroundColor() != null) {
			String color = pCellStyle.getFillBackgroundColor();
			if (isIndexColor(color)) {
				style.setFillBackgroundColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setFillBackgroundColor(getXSSFColor(color));
			}
		}

		if (pCellStyle.getFillForegroundColor() != null) {
			String color = pCellStyle.getFillForegroundColor();
			if (isIndexColor(color)) {
				style.setFillForegroundColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setFillForegroundColor(getXSSFColor(color));
			}
		}

		if (pCellStyle.getFontColor() != null) {
			String color = pCellStyle.getFontColor();
			if (isIndexColor(color)) {
				font.setColor(IndexedColors.valueOf(color).getIndex());
			} else {
				((XSSFFont) font).setColor(getXSSFColor(color));
			}
		}

		if (pCellStyle.getTopBorderColor() != null) {
			String color = pCellStyle.getTopBorderColor();
			if (isIndexColor(color)) {
				style.setTopBorderColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setTopBorderColor(getXSSFColor(color));
			}
		}

		if (pCellStyle.getRightBorderColor() != null) {
			String color = pCellStyle.getRightBorderColor();
			if (isIndexColor(color)) {
				style.setRightBorderColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setRightBorderColor(getXSSFColor(color));
			}
		}

		if (pCellStyle.getLeftBorderColor() != null) {
			String color = pCellStyle.getLeftBorderColor();
			if (isIndexColor(color)) {
				style.setLeftBorderColor(IndexedColors.valueOf(color).getIndex());
			} else {
				style.setLeftBorderColor(getXSSFColor(color));
			}
		}

	}

	private XSSFColor getXSSFColor(String color) {
		Color clr = Color.decode(color);
		XSSFColor myColor = new XSSFColor(clr, new DefaultIndexedColorMap());
		return myColor;
	}

	private synchronized void checkStyleConstantValues() {
		if(m_alignmentValues == null) {
			this.m_alignmentValues = new HashMap<>();
			m_alignmentValues.put("ALIGN_CENTER", HorizontalAlignment.CENTER); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_CENTER_SELECTION", HorizontalAlignment.CENTER_SELECTION); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_FILL", HorizontalAlignment.FILL); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_GENERAL", HorizontalAlignment.GENERAL); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_JUSTIFY", HorizontalAlignment.JUSTIFY); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_LEFT", HorizontalAlignment.LEFT); //$NON-NLS-1$
			m_alignmentValues.put("ALIGN_RIGHT", HorizontalAlignment.RIGHT); //$NON-NLS-1$
		}
		if(m_borderValues == null) {
			this.m_borderValues = new HashMap<>();

			m_borderValues.put("BORDER_DASH_DOT", BorderStyle.DASH_DOT); //$NON-NLS-1$
			m_borderValues.put("BORDER_DASH_DOT_DOT", BorderStyle.DASH_DOT_DOT); //$NON-NLS-1$
			m_borderValues.put("BORDER_DASHED", BorderStyle.DASHED); //$NON-NLS-1$
			m_borderValues.put("BORDER_DOTTED", BorderStyle.DOTTED); //$NON-NLS-1$
			m_borderValues.put("BORDER_DOUBLE", BorderStyle.DOUBLE); //$NON-NLS-1$
			m_borderValues.put("BORDER_HAIR", BorderStyle.HAIR); //$NON-NLS-1$
			m_borderValues.put("BORDER_MEDIUM", BorderStyle.MEDIUM); //$NON-NLS-1$
			m_borderValues.put("BORDER_MEDIUM_DASH_DOT", BorderStyle.MEDIUM_DASH_DOT); //$NON-NLS-1$
			m_borderValues.put("BORDER_MEDIUM_DASH_DOT_DOT", BorderStyle.MEDIUM_DASH_DOT_DOT); //$NON-NLS-1$
			m_borderValues.put("BORDER_MEDIUM_DASHED", BorderStyle.MEDIUM_DASHED); //$NON-NLS-1$
			m_borderValues.put("BORDER_NONE", BorderStyle.NONE); //$NON-NLS-1$
			m_borderValues.put("BORDER_SLANTED_DASH_DOT", BorderStyle.SLANTED_DASH_DOT); //$NON-NLS-1$
			m_borderValues.put("BORDER_THICK", BorderStyle.THICK); //$NON-NLS-1$
			m_borderValues.put("BORDER_THIN", BorderStyle.THIN); //$NON-NLS-1$
		}
		if(m_fillPatternValues == null) {
			this.m_fillPatternValues = new HashMap<>();
			m_fillPatternValues.put("ALT_BARS", FillPatternType.ALT_BARS); //$NON-NLS-1$
			m_fillPatternValues.put("BIG_SPOTS", FillPatternType.BIG_SPOTS); //$NON-NLS-1$
			m_fillPatternValues.put("BRICKS", FillPatternType.BRICKS); //$NON-NLS-1$
			m_fillPatternValues.put("DIAMONDS", FillPatternType.DIAMONDS); //$NON-NLS-1$
			m_fillPatternValues.put("FINE_DOTS", FillPatternType.FINE_DOTS); //$NON-NLS-1$
			m_fillPatternValues.put("LEAST_DOTS", FillPatternType.LEAST_DOTS); //$NON-NLS-1$
			m_fillPatternValues.put("LESS_DOTS", FillPatternType.LESS_DOTS); //$NON-NLS-1$
			m_fillPatternValues.put("NO_FILL", FillPatternType.NO_FILL); //$NON-NLS-1$
			m_fillPatternValues.put("SOLID_FOREGROUND", FillPatternType.SOLID_FOREGROUND); //$NON-NLS-1$
			m_fillPatternValues.put("SPARSE_DOTS", FillPatternType.SPARSE_DOTS); //$NON-NLS-1$
			m_fillPatternValues.put("SQUARES", FillPatternType.SQUARES); //$NON-NLS-1$

			m_fillPatternValues.put("THICK_BACKWARD_DIAG", FillPatternType.THICK_BACKWARD_DIAG); //$NON-NLS-1$
			m_fillPatternValues.put("THICK_FORWARD_DIAG", FillPatternType.THICK_FORWARD_DIAG); //$NON-NLS-1$
			m_fillPatternValues.put("THICK_HORZ_BANDS", FillPatternType.THICK_HORZ_BANDS); //$NON-NLS-1$
			m_fillPatternValues.put("THICK_VERT_BANDS", FillPatternType.THICK_VERT_BANDS); //$NON-NLS-1$
			m_fillPatternValues.put("THIN_BACKWARD_DIAG", FillPatternType.THIN_BACKWARD_DIAG); //$NON-NLS-1$
			m_fillPatternValues.put("THIN_FORWARD_DIAG", FillPatternType.THIN_FORWARD_DIAG); //$NON-NLS-1$
			m_fillPatternValues.put("THIN_HORZ_BANDS", FillPatternType.THIN_HORZ_BANDS); //$NON-NLS-1$
			m_fillPatternValues.put("THIN_VERT_BANDS", FillPatternType.THIN_VERT_BANDS); //$NON-NLS-1$
		}
		if(m_vertAlignmentValues == null) {
			m_vertAlignmentValues = new HashMap<>();

			m_vertAlignmentValues.put("VERTICAL_BOTTOM", VerticalAlignment.BOTTOM); //$NON-NLS-1$
			m_vertAlignmentValues.put("VERTICAL_CENTER", VerticalAlignment.CENTER); //$NON-NLS-1$
			m_vertAlignmentValues.put("VERTICAL_JUSTIFY", VerticalAlignment.JUSTIFY); //$NON-NLS-1$
			m_vertAlignmentValues.put("VERTICAL_TOP", VerticalAlignment.TOP); //$NON-NLS-1$
		}
		if (m_StyleConstantValues == null) {
			m_StyleConstantValues = new HashMap<>();

			m_StyleConstantValues.put("SS_NONE", Font.SS_NONE); //$NON-NLS-1$
			m_StyleConstantValues.put("SS_SUPER", Font.SS_SUPER); //$NON-NLS-1$
			m_StyleConstantValues.put("SS_SUB", Font.SS_SUB); //$NON-NLS-1$

		}

		if (m_StyleByteConstantValues == null) {
			m_StyleByteConstantValues = new HashMap<>();
			m_StyleByteConstantValues.put("U_NONE", Font.U_NONE); //$NON-NLS-1$
			m_StyleByteConstantValues.put("U_SINGLE", Font.U_SINGLE); //$NON-NLS-1$
			m_StyleByteConstantValues.put("U_DOUBLE", Font.U_DOUBLE); //$NON-NLS-1$
			m_StyleByteConstantValues.put("U_SINGLE_ACCOUNTING", Font.U_SINGLE_ACCOUNTING); //$NON-NLS-1$
			m_StyleByteConstantValues.put("U_DOUBLE_ACCOUNTING", Font.U_DOUBLE_ACCOUNTING); //$NON-NLS-1$
		}
	}

}
