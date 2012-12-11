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

import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;
import biz.webgate.dominoext.poi.component.sources.IExportSource;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

public class Data2ColumnExporter extends ValueBindingObjectImpl implements
		IListDataExporter {

	private String m_Var;
	private String m_Index;
	private List<RowDefinition> m_Rows;
	private Integer m_StartColumn;
	private Integer m_StepSize;
	private IExportSource m_DataSource;

	public IExportSource getDataSource() {
		return m_DataSource;
	}

	public void setDataSource(IExportSource dataSource) {
		m_DataSource = dataSource;
	}

	public int getStepSize() {
		if (m_StepSize != null) {
			return m_StepSize;
		}
		ValueBinding vb = getValueBinding("stepSize");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null)
				return intValue;
		}
		return 0;
	}

	public void setStepSize(int stepSize) {
		m_StepSize = stepSize;
	}

	public int getStartColumn() {
		if (m_StartColumn != null) {
			return m_StartColumn;
		}
		ValueBinding vb = getValueBinding("startColumn");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null)
				return intValue;
		}
		return 0;
	}

	public void setStartColumn(int startColumn) {
		m_StartColumn = startColumn;
	}

	public void addRow(RowDefinition rdCurrent) {
		if (m_Rows == null) {
			m_Rows = new ArrayList<RowDefinition>();
		}
		m_Rows.add(rdCurrent);
	}

	public List<RowDefinition> getRows() {
		return m_Rows;
	}

	public void setRows(List<RowDefinition> rows) {
		m_Rows = rows;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[5];
		state[0] = super.saveState(context);
		state[1] = m_StartColumn;
		state[2] = m_StepSize;
		state[3] = StateHolderUtil.saveList(context, m_Rows);
		state[4] = FacesUtil.objectToSerializable(getFacesContext(),
				m_DataSource);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_StartColumn = (Integer) state[1];
		m_StepSize = (Integer) state[2];
		m_Rows = StateHolderUtil.restoreList(context, getComponent(), state[3]);
		m_DataSource = (IExportSource) FacesUtil.objectFromSerializable(
				getFacesContext(), getComponent(), state[4]);

	}

	public String getVar() {
		return m_Var;
	}

	public void setVar(String var) {
		m_Var = var;
	}

	public String getIndex() {
		return m_Index;
	}

	public void setIndex(String index) {
		m_Index = index;
	}
}
