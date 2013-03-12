package biz.webgate.dominoext.poi.component.data.ss.cell;

import javax.faces.context.FacesContext;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class PoiCellStyle extends ValueBindingObjectImpl{
	
	
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
	private Short m_FontBoldweight = 0;
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
	

	public String getAlignment() {
		return m_Alignment;
	}
	public void setAlignment(String alignment) {
		m_Alignment = alignment;
	}
		
	public String getBorderBottom() {
		return m_BorderBottom;
	}
	public void setBorderBottom(String borderBottom) {
		m_BorderBottom = borderBottom;
	}
	public String getBorderTop() {
		return m_BorderTop;
	}
	public void setBorderTop(String borderTop) {
		m_BorderTop = borderTop;
	}
	public String getBorderRight() {
		return m_BorderRight;
	}
	public void setBorderRight(String borderRight) {
		m_BorderRight = borderRight;
	}
	public String getBorderLeft() {
		return m_BorderLeft;
	}
	public void setBorderLeft(String borderLeft) {
		m_BorderLeft = borderLeft;
	}
	public String getBottomBorderColor() {
		return m_BottomBorderColor;
	}
	public void setBottomBorderColor(String bottomBorderColor) {
		m_BottomBorderColor = bottomBorderColor;
	}
	public String getDataFormat() {
		return m_DataFormat;
	}
	public void setDataFormat(String dataFormat) {
		m_DataFormat = dataFormat;
	}
	public String getFillBackgroundColor() {
		return m_FillBackgroundColor;
	}
	public void setFillBackgroundColor(String fillBackgroundColor) {
		m_FillBackgroundColor = fillBackgroundColor;
	}
	public String getFillForegroundColor() {
		return m_FillForegroundColor;
	}
	public void setFillForegroundColor(String fillForegroundColor) {
		m_FillForegroundColor = fillForegroundColor;
	}
	public String getFillPattern() {
		return m_FillPattern;
	}
	public void setFillPattern(String fillPattern) {
		m_FillPattern = fillPattern;
	}

	public short getFontBoldweight() {
		return m_FontBoldweight;
	}
	public void setFontBoldweight(short fontBoldweight) {
		m_FontBoldweight = fontBoldweight;
	}
	public String getFontColor() {
		return m_FontColor;
	}
	public void setFontColor(String fontColor) {
		m_FontColor = fontColor;
	}
	public short getFontHeightInPoints() {
		return m_FontHeightInPoints;
	}
	public void setFontHeightInPoints(short fontHeightInPoints) {
		m_FontHeightInPoints = fontHeightInPoints;
	}
	public String getFontName() {
		return m_FontName;
	}
	public void setFontName(String fontName) {
		m_FontName = fontName;
	}
	public boolean isFontItalic() {
		return m_FontItalic;
	}
	public void setFontItalic(boolean fontItalic) {
		this.m_FontItalic = fontItalic;
	}
	public boolean isFontStrikeout() {
		return m_FontStrikeout;
	}
	public void setFontStrikeout(boolean fontStrikeout) {
		this.m_FontStrikeout = fontStrikeout;
	}
	public String getFontUnderline() {
		return m_FontUnderline;
	}
	public void setFontUnderline(String fontUnderline) {
		this.m_FontUnderline = fontUnderline;
	}
	
	public String getFontTypeOffset() {
		return m_FontTypeOffset;
	}
	public void setFontTypeOffset(String fontTypeOffset) {
		m_FontTypeOffset = fontTypeOffset;
	}

	public boolean isHidden() {
		return m_Hidden;
	}
	public void setHidden(boolean hidden) {
		m_Hidden = hidden;
	}
	public String getIndention() {
		return m_Indention;
	}
	public void setIndention(String indention) {
		m_Indention = indention;
	}
	public String getLeftBorderColor() {
		return m_LeftBorderColor;
	}
	public void setLeftBorderColor(String leftBorderColor) {
		m_LeftBorderColor = leftBorderColor;
	}
	public boolean isLocked() {
		return m_Locked;
	}
	public void setLocked(boolean locked) {
		m_Locked = locked;
	}
	public String getRightBorderColor() {
		return m_RightBorderColor;
	}
	public void setRightBorderColor(String rightBorderColor) {
		m_RightBorderColor = rightBorderColor;
	}
	public short getRotation() {
		return m_Rotation;
	}
	public void setRotation(short rotation) {
		m_Rotation = rotation;
	}
	public String getTopBorderColor() {
		return m_TopBorderColor;
	}
	public void setTopBorderColor(String topBorderColor) {
		m_TopBorderColor = topBorderColor;
	}
	public String getVerticalAlignment() {
		return m_VerticalAlignment;
	}
	public void setVerticalAlignment(String verticalAlignment) {
		m_VerticalAlignment = verticalAlignment;
	}
	public boolean isWrapText() {
		return m_WrapText;
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
		m_FillPattern = (String)state[10];
		m_FontBoldweight = (Short) state[11];
		m_FontColor = (String) state[12] ;
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
