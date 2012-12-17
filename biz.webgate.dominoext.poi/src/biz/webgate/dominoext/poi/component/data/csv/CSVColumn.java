package biz.webgate.dominoext.poi.component.data.csv;

import biz.webgate.dominoext.poi.component.data.IDefinition;
import biz.webgate.dominoext.poi.util.ValueBindingSupport;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class CSVColumn extends ValueBindingObjectImpl implements IDefinition {

	public String m_Title;
	public String m_ColumnTitle;
	public Integer m_Position;

	public String getTitle() {
		if (m_Title != null) {
			return m_Title;
		}
		return ValueBindingSupport
				.getVBString("title", this, getFacesContext());

	}

	public void setTitle(String title) {
		m_Title = title;
	}

	public String getColumnTitle() {
		if (m_ColumnTitle != null) {
			return m_ColumnTitle;
		}
		return ValueBindingSupport.getVBString("columnTitle", this,
				getFacesContext());
	}

	public void setColumnTitle(String columnTitle) {
		m_ColumnTitle = columnTitle;
	}

	public Integer getPosition() {
		if (m_Position != null) {
			return m_Position;
		}
		return ValueBindingSupport
				.getVBInt("position", this, getFacesContext());
	}

	public void setPosition(Integer position) {
		m_Position = position;
	}

}
