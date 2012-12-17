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

package biz.webgate.dominoext.poi.component.sources;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.faces.el.ValueBinding;

import biz.webgate.dominoext.poi.component.data.IDefinition;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class ListObjectDataSource extends ValueBindingObjectImpl implements
		IExportSource {

	private List<?> m_Values;
	private Iterator<?> m_tempIt;
	private Object m_tempObj;

	public List<?> getValues() {
		if (m_Values != null) {
			return m_Values;
		}
		ValueBinding vb = getValueBinding("values");
		if (vb != null) {
			return (List<?>) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setValues(List<?> values) {
		m_Values = values;
	}

	public Object getValue(IDefinition idCurrent) {
		try {
			Method mt = m_tempObj.getClass().getMethod(
					"get" + idCurrent.getColumnTitle(), Object.class);
			return mt.invoke(m_tempObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int accessNextRow() {
		m_tempObj = m_tempIt.next();
		return m_tempIt.hasNext() ? 1 : 0;
	}

	public int accessSource() {
		if (m_Values != null) {
			m_tempIt = m_Values.iterator();
		}
		return m_Values != null ? 1 : 0;
	}

	public int closeSource() {
		m_tempIt = null;
		m_tempObj = null;
		return 1;
	}

}
