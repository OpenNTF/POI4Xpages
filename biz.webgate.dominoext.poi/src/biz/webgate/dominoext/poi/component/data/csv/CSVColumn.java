package biz.webgate.dominoext.poi.component.data.csv;

import javax.faces.context.FacesContext;

import biz.webgate.dominoext.poi.component.data.AbstractDefinition;
import biz.webgate.dominoext.poi.component.data.IDefinition;
import biz.webgate.dominoext.poi.utils.xsp.ValueBindingSupport;

import com.ibm.xsp.util.StateHolderUtil;

public class CSVColumn extends AbstractDefinition implements IDefinition {

	public String m_Title;
	public String m_ColumnTitle;
	public Integer m_Position = new Integer(999999);

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

	public int getPosition() {
		if (m_Position != null) {
			return m_Position;
		}
		return ValueBindingSupport
				.getVBInt("position", this, getFacesContext());
	}

	public void setPosition(int position) {
		m_Position = position;
	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] state = new Object[5];
		state[0] = super.saveState(context);
		state[1] = m_ColumnTitle;
		state[2] = m_Position;
		state[3] = m_Title;
		state[4] = StateHolderUtil.saveMethodBinding(context,getComputeValue());
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_ColumnTitle = (String) state[1];
		m_Position = (Integer) state[2];
		m_Title = (String) state[3];
		setComputeValue(StateHolderUtil.restoreMethodBinding(context, getComponent(), state[4]));
	}

}
