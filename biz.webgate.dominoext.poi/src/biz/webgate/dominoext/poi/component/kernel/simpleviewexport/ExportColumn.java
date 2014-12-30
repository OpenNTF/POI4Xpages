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

public class ExportColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_ColumnName;
	private int m_Position;
	private int m_TimeDateFormat;
	
	public String getColumnName() {
		return m_ColumnName;
	}
	public void setColumnName(String columnName) {
		m_ColumnName = columnName;
	}
	public int getPosition() {
		return m_Position;
	}
	public void setPosition(int position) {
		m_Position = position;
	}
	public int getTimeDateFormat() {
		return m_TimeDateFormat;
	}
	public void setTimeDateFormat(int timeDateFormat) {
		m_TimeDateFormat = timeDateFormat;
	}
}
