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

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class RowDefinition extends ValueBindingObjectImpl {
	private Integer m_RowNumber;
	private Object m_Value;
	private Integer m_ColumnShift = 0;

	public Object getValue() {
		if (m_Value != null) {
			return m_Value;
		}
		ValueBinding vb = getValueBinding("value");
		if (vb != null) {
			return vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setValue(Object value) {
		m_Value = value;
	}

	public int getRowNumber() {
		if (m_RowNumber != null)
			return m_RowNumber;
		ValueBinding vb = getValueBinding("rowNumber");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null) {
				return intValue;
			}
		}
		return 0;

	}

	public void setRowNumber(int rowNumber) {
		m_RowNumber = rowNumber;
	}

	public int getColumnShift() {
		if (m_ColumnShift != null) {
			return m_ColumnShift;
		}
		ValueBinding vb = getValueBinding("columnShift");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null) {
				return intValue;
			}
		}
		return 0;

	}

	public void setColumnShift(int columnShift) {
		m_ColumnShift = columnShift;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = m_RowNumber;
		state[2] = m_ColumnShift;
		state[3] = m_Value;
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_RowNumber = (Integer) state[1];
		m_ColumnShift = (Integer) state[2];
		m_Value = state[3];
	}

}
