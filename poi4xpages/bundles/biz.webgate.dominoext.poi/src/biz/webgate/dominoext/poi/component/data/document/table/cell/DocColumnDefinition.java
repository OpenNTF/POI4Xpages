/*
 * � Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi.component.data.document.table.cell;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.util.StateHolderUtil;

import biz.webgate.dominoext.poi.component.data.AbstractDefinition;
import biz.webgate.dominoext.poi.component.data.IDefinition;

public class DocColumnDefinition extends AbstractDefinition implements IDefinition {
	private Integer m_ColumnNumber;
	private String m_ColumnTitle;
	private String m_ColumnHeader;
	private Integer m_RowShift = 0;

	// private PoiCellStyle m_PoiCellStyle;

	public int getColumnNumber() {
		if (m_ColumnNumber != null) {
			return m_ColumnNumber;
		}
		ValueBinding vb = getValueBinding("columnNumber");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null) {
				return intValue;
			}
		}
		return 0;

	}

	public void setColumnNumber(int columnNumber) {
		m_ColumnNumber = columnNumber;
	}

	public int getRowShift() {
		if (m_RowShift != null) {
			return m_RowShift;
		}
		ValueBinding vb = getValueBinding("rowShift");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null) {
				return intValue;
			}
		}
		return 0;

	}

	public void setRowShift(int rowShift) {
		m_RowShift = rowShift;
	}


	@Override
	public String getColumnTitle() {
		if (m_ColumnTitle != null) {
			return m_ColumnTitle;
		}
		ValueBinding vb = getValueBinding("columnTitle");
		if (vb != null) {
			String strValue = (String) vb.getValue(getFacesContext());
			if (strValue != null) {
				return strValue;
			}
		}
		return null;
	}

	public void setColumnTitle(String columnTitle) {
		m_ColumnTitle = columnTitle;
	}

	public String getColumnHeader() {
		if (m_ColumnHeader != null) {
			return m_ColumnHeader;
		}
		ValueBinding vb = getValueBinding("columnHeader");
		if (vb != null) {
			String strValue = (String) vb.getValue(getFacesContext());
			if (strValue != null) {
				return strValue;
			}
		}
		return null;
	}

	public void setColumnHeader(String columnHeader) {
		m_ColumnHeader = columnHeader;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[6];
		state[0] = super.saveState(context);
		state[1] = m_ColumnNumber;
		state[2] = m_RowShift;
		state[3] = m_ColumnTitle;
		state[4] = StateHolderUtil
				.saveMethodBinding(context, getComputeValue());
		state[5] = m_ColumnHeader;
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_ColumnNumber = (Integer) state[1];
		m_RowShift = (Integer) state[2];
		m_ColumnTitle = (String) state[3];
		setComputeValue(StateHolderUtil.restoreMethodBinding(context,
				getComponent(), state[4]));
		m_ColumnHeader = (String) state[5];
	}


}
