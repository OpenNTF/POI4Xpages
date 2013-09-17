/*
 * © Copyright WebGate Consulting AG, 2013
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
package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import java.io.Serializable;
import java.util.HashMap;

public class ExportDataRow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_UNID;
	private HashMap<Integer, Object> m_Values;

	public String getUNID() {
		return m_UNID;
	}

	public void setUNID(String uNID) {
		m_UNID = uNID;
	}

	public HashMap<Integer, Object> getValues() {
		return m_Values;
	}

	public void setValues(HashMap<Integer, Object> values) {
		m_Values = values;
	}

	public void addValue(int nPosition, Object obj) {
		if (m_Values == null) {
			m_Values = new HashMap<Integer, Object>();
		}
		m_Values.put(new Integer(nPosition), obj);
	}

	public Object getValue(int nPosition) {
		if (m_Values == null) {
			return null;
		}
		return m_Values.get(new Integer(nPosition));
	}
}
