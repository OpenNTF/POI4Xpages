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
package biz.webgate.dominoext.poi.component.data.ss;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.StateHolderUtil;

public class Spreadsheet extends ValueBindingObjectImpl {

	private String m_Name;
	private Boolean m_Create;
	private List<IListDataExporter> m_ExportDefinition;
	private List<ICellValue> m_CellValues;

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

	public boolean isCreate() {
		if (m_Create != null) {
			return m_Create;
		}
		ValueBinding vb = getValueBinding("create");
		if (vb != null) {
			Boolean blValue = (Boolean) vb.getValue(getFacesContext());
			if (blValue != null) {
				return blValue;
			}
		}
		return false;
	}

	public void setCreate(boolean create) {
		m_Create = create;
	}

	public List<IListDataExporter> getExportDefinitions() {
		return m_ExportDefinition;
	}

	public void setExportDefinitions(List<IListDataExporter> exportDefinition) {
		m_ExportDefinition = exportDefinition;
	}

	public void addExportDefinition(IListDataExporter ilCurrent) {
		if (m_ExportDefinition == null) {
			m_ExportDefinition = new ArrayList<IListDataExporter>();
		}
		m_ExportDefinition.add(ilCurrent);
	}

	public List<ICellValue> getCellValues() {
		return m_CellValues;
	}

	public void setCellValues(List<ICellValue> cellValues) {
		m_CellValues = cellValues;
	}

	public void addCellValues(ICellValue icvCurrent) {
		if (m_CellValues == null) {
			m_CellValues = new ArrayList<ICellValue>();
		}
		m_CellValues.add(icvCurrent);
	}

	@Override
	public Object saveState(FacesContext context) {
		try {

			Object[] state = new Object[3];
			state[0] = super.saveState(context);
			state[1] = m_Create;
			state[2] = m_Name;
			state[3] = StateHolderUtil.saveList(context, m_ExportDefinition);
			state[4] = StateHolderUtil.saveList(context, m_CellValues);
			return state;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_Create = (Boolean) state[1];
		m_Name = (String) state[2];
		m_ExportDefinition = StateHolderUtil.restoreList(context,
				getComponent(), state[3]);
		m_CellValues = StateHolderUtil.restoreList(context, getComponent(),
				state[4]);
	}
}
