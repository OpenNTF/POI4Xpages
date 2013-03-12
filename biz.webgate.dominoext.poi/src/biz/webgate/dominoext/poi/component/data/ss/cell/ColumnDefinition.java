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
package biz.webgate.dominoext.poi.component.data.ss.cell;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import biz.webgate.dominoext.poi.component.data.AbstractDefinition;
import biz.webgate.dominoext.poi.component.data.IDefinition;

import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

public class ColumnDefinition extends AbstractDefinition implements IDefinition {
	private Integer m_ColumnNumber;
	private String m_ColumnTitle;
	private Integer m_RowShift = 0;
	private Boolean m_CellFormula = false;
	private PoiCellStyle m_PoiCellStyle;
	
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
		
	public boolean isCellFormula() {
		return m_CellFormula;
	}

	public void setCellFormula(boolean cellFormula) {
		m_CellFormula = cellFormula;
	}
	
	public PoiCellStyle getPoiCellStyle() {
		return m_PoiCellStyle;
	}

	public void setPoiCellStyle(PoiCellStyle poiCellStyle) {
		m_PoiCellStyle = poiCellStyle;
	}

	public PoiCellStyle getCellStyle() {
		return getPoiCellStyle();
	}

	public void setCellStyle(PoiCellStyle poiCellStyle) {
		setPoiCellStyle(poiCellStyle);
	}
	
	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[7];
		state[0] = super.saveState(context);
		state[1] = m_ColumnNumber;
		state[2] = m_RowShift;
		state[3] = m_ColumnTitle;
		state[4] = StateHolderUtil
				.saveMethodBinding(context, getComputeValue());
		state[5] = m_CellFormula;
		state[6] = FacesUtil.objectToSerializable(context, m_PoiCellStyle);
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
		m_CellFormula = (Boolean) state[5];
		m_PoiCellStyle = (PoiCellStyle) FacesUtil.objectFromSerializable(context, state[6]);
	}

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

}
