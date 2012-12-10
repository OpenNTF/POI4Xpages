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

public class CellBookmark extends ValueBindingObjectImpl implements ICellValue {

	private Object m_Value;
	private String m_Name;

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

	public String getName() {
		if (m_Name != null) {
			return m_Name;
		}
		ValueBinding vb = getValueBinding("name");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setName(String name) {
		m_Name = name;
	}

	public void setValue(Object value) {
		m_Value = value;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = m_Name;
		state[2] = m_Value;
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_Name = (String)state[1];
		m_Value = state[2];
	}
}
