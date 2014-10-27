package biz.webgate.dominoext.poi.component.data.ss.cell;

import static biz.webgate.dominoext.poi.utils.xsp.ValueBindingSupport.getBooleanValue;
import static biz.webgate.dominoext.poi.utils.xsp.ValueBindingSupport.getShortValue;
import static biz.webgate.dominoext.poi.utils.xsp.ValueBindingSupport.getStringValue;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.CellStyle;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class PoiCellStyle extends ValueBindingObjectImpl {

	private String m_Alignment;
	private String m_BorderBottom;
	private String m_BorderTop;
	private String m_BorderRight;
	private String m_BorderLeft;
	private String m_BottomBorderColor;
	private String m_DataFormat;
	private String m_FillBackgroundColor;
	private String m_FillForegroundColor;
	private String m_FillPattern;
	private String m_FontBoldweight;
	private String m_FontColor;
	private Short m_FontHeightInPoints = 0;
	private String m_FontName;
	private Boolean m_FontItalic = false;
	private Boolean m_FontStrikeout = false;
	private String m_FontUnderline;
	private String m_FontTypeOffset;
	private Boolean m_Hidden = false;
	private String m_Indention;
	private String m_LeftBorderColor;
	private Boolean m_Locked = false;
	private String m_RightBorderColor;
	private Short m_Rotation = 0;
	private String m_TopBorderColor;
	private String m_VerticalAlignment;
	private Boolean m_WrapText = false;
	/*
	 * A initialized CellStyle object, only to make the generationProcess much
	 * faster and do NOT save it via save/restore state
	 */
	private CellStyle m_CellStyle;

	public CellStyle getCellStyle() {
		return m_CellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		m_CellStyle = cellStyle;
	}

	public String getAlignment() {
		return getStringValue(m_Alignment, "aligment", this, null, getFacesContext());
	}

	public void setAlignment(String alignment) {
		m_Alignment = alignment;
	}

	public String getBorderBottom() {
		return getStringValue(m_BorderBottom, "borderBottom", this, null, getFacesContext());
	}

	public void setBorderBottom(String borderBottom) {
		m_BorderBottom = borderBottom;
	}

	public String getBorderTop() {
		return getStringValue(m_BorderTop, "borderTop", this, null, getFacesContext());
	}

	public void setBorderTop(String borderTop) {
		m_BorderTop = borderTop;
	}

	public String getBorderRight() {
		return getStringValue(m_BorderRight, "borderRight", this, null, getFacesContext());
	}

	public void setBorderRight(String borderRight) {
		m_BorderRight = borderRight;
	}

	public String getBorderLeft() {
		return getStringValue(m_BorderLeft, "borderLeft", this, null, getFacesContext());
	}

	public void setBorderLeft(String borderLeft) {
		m_BorderLeft = borderLeft;
	}

	public String getBottomBorderColor() {
		return getStringValue(m_BottomBorderColor, "bottomBorderColor", this, null, getFacesContext());
	}

	public void setBottomBorderColor(String bottomBorderColor) {
		m_BottomBorderColor = bottomBorderColor;
	}

	public String getDataFormat() {
		return getStringValue(m_DataFormat, "dataFormat", this, null, getFacesContext());
	}

	public void setDataFormat(String dataFormat) {
		m_DataFormat = dataFormat;
	}

	public String getFillBackgroundColor() {
		return getStringValue(m_FillBackgroundColor, "fillBackgroundColor", this, null, getFacesContext());
	}

	public void setFillBackgroundColor(String fillBackgroundColor) {
		m_FillBackgroundColor = fillBackgroundColor;
	}

	public String getFillForegroundColor() {
		return getStringValue(m_FillForegroundColor, "fillForegroundColor", this, null, getFacesContext());
	}

	public void setFillForegroundColor(String fillForegroundColor) {
		m_FillForegroundColor = fillForegroundColor;
	}

	public String getFillPattern() {
		return getStringValue(m_FillPattern, "fillPattern", this, null, getFacesContext());
	}

	public void setFillPattern(String fillPattern) {
		m_FillPattern = fillPattern;
	}

	public String getFontBoldweight() {
		return getStringValue(m_FontBoldweight, "fontBoldweight", this, null, getFacesContext());
	}

	public void setFontBoldweight(String fontBoldweight) {
		m_FontBoldweight = fontBoldweight;
	}

	public String getFontColor() {
		return getStringValue(m_FontColor, "fontColor", this, null, getFacesContext());
	}

	public void setFontColor(String fontColor) {
		m_FontColor = fontColor;
	}

	public short getFontHeightInPoints() {
		return getShortValue(m_FontHeightInPoints, "fontHeightInPoints", this, null, getFacesContext());
	}

	public void setFontHeightInPoints(short fontHeightInPoints) {
		m_FontHeightInPoints = fontHeightInPoints;
	}

	public String getFontName() {
		return getStringValue(m_FontName, "fontName", this, null, getFacesContext());
	}

	public void setFontName(String fontName) {
		m_FontName = fontName;
	}

	public boolean isFontItalic() {
		return getBooleanValue(m_FontItalic, "fontItalic", this, null, getFacesContext());
	}

	public void setFontItalic(boolean fontItalic) {
		this.m_FontItalic = fontItalic;
	}

	public boolean isFontStrikeout() {
		return getBooleanValue(m_FontStrikeout, "fontStrikeout", this, null, getFacesContext());
	}

	public void setFontStrikeout(boolean fontStrikeout) {
		this.m_FontStrikeout = fontStrikeout;
	}

	public String getFontUnderline() {
		return getStringValue(m_FontUnderline, "fontUnderline", this, null, getFacesContext());
	}

	public void setFontUnderline(String fontUnderline) {
		this.m_FontUnderline = fontUnderline;
	}

	public String getFontTypeOffset() {
		return getStringValue(m_FontTypeOffset, "fontTypeOffset", this, null, getFacesContext());
	}

	public void setFontTypeOffset(String fontTypeOffset) {
		m_FontTypeOffset = fontTypeOffset;
	}

	public boolean isHidden() {
		return getBooleanValue(m_Hidden, "hidden", this, null, getFacesContext());
	}

	public void setHidden(boolean hidden) {
		m_Hidden = hidden;
	}

	public String getIndention() {
		return getStringValue(m_Indention, "indention", this, null, getFacesContext());
	}

	public void setIndention(String indention) {
		m_Indention = indention;
	}

	public String getLeftBorderColor() {
		return getStringValue(m_LeftBorderColor, "leftBorderColor", this, null, getFacesContext());
	}

	public void setLeftBorderColor(String leftBorderColor) {
		m_LeftBorderColor = leftBorderColor;
	}

	public boolean isLocked() {
		return getBooleanValue(m_Locked, "locked", this, null, getFacesContext());
	}

	public void setLocked(boolean locked) {
		m_Locked = locked;
	}

	public String getRightBorderColor() {
		return getStringValue(m_RightBorderColor, "rightBorderColor", this, null, getFacesContext());
	}

	public void setRightBorderColor(String rightBorderColor) {
		m_RightBorderColor = rightBorderColor;
	}

	public short getRotation() {
		return getShortValue(m_Rotation, "rotation", this, null, getFacesContext());
	}

	public void setRotation(short rotation) {
		m_Rotation = rotation;
	}

	public String getTopBorderColor() {
		return getStringValue(m_TopBorderColor, "topBorderColor", this, null, getFacesContext());
	}

	public void setTopBorderColor(String topBorderColor) {
		m_TopBorderColor = topBorderColor;
	}

	public String getVerticalAlignment() {
		return getStringValue(m_VerticalAlignment, "verticalAlignment", this, null, getFacesContext());
	}

	public void setVerticalAlignment(String verticalAlignment) {
		m_VerticalAlignment = verticalAlignment;
	}

	public boolean isWrapText() {
		return getBooleanValue(m_WrapText, "wrapText", this, null, getFacesContext());
	}

	public void setWrapText(boolean wrapText) {
		m_WrapText = wrapText;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[28];
		state[0] = super.saveState(context);
		state[1] = m_Alignment;
		state[2] = m_BorderBottom;
		state[3] = m_BorderTop;
		state[4] = m_BorderRight;
		state[5] = m_BorderLeft;
		state[6] = m_BottomBorderColor;
		state[7] = m_DataFormat;
		state[8] = m_FillBackgroundColor;
		state[9] = m_FillForegroundColor;
		state[10] = m_FillPattern;
		state[11] = m_FontBoldweight;
		state[12] = m_FontColor;
		state[13] = m_FontHeightInPoints;
		state[14] = m_FontItalic;
		state[15] = m_FontName;
		state[16] = m_FontStrikeout;
		state[17] = m_FontUnderline;
		state[18] = m_FontTypeOffset;
		state[19] = m_Hidden;
		state[20] = m_Indention;
		state[21] = m_LeftBorderColor;
		state[22] = m_Locked;
		state[23] = m_RightBorderColor;
		state[24] = m_Rotation;
		state[25] = m_TopBorderColor;
		state[26] = m_VerticalAlignment;
		state[27] = m_WrapText;
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_Alignment = (String) state[1];
		m_BorderBottom = (String) state[2];
		m_BorderTop = (String) state[3];
		m_BorderRight = (String) state[4];
		m_BorderLeft = (String) state[5];
		m_BottomBorderColor = (String) state[6];
		m_DataFormat = (String) state[7];
		m_FillBackgroundColor = (String) state[8];
		m_FillForegroundColor = (String) state[9];
		m_FillPattern = (String) state[10];
		m_FontBoldweight = (String) state[11];
		m_FontColor = (String) state[12];
		m_FontHeightInPoints = (Short) state[13];
		m_FontItalic = (Boolean) state[14];
		m_FontName = (String) state[15];
		m_FontStrikeout = (Boolean) state[16];
		m_FontUnderline = (String) state[17];
		m_FontTypeOffset = (String) state[18];
		m_Hidden = (Boolean) state[19];
		m_Indention = (String) state[20];
		m_LeftBorderColor = (String) state[21];
		m_Locked = (Boolean) state[22];
		m_RightBorderColor = (String) state[23];
		m_Rotation = (Short) state[24];
		m_TopBorderColor = (String) state[25];
		m_VerticalAlignment = (String) state[26];
		m_WrapText = (Boolean) state[27];
	}

}
