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

import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

public class Data2RowExporter extends ValueBindingObjectImpl implements
		IListDataExporter {
	private DataSource m_DataSource;
	private String m_Var;
	private String m_Index;
	private Integer m_StartRow;
	private Integer m_StepSize;
	private List<ColumnDefinition> m_Columns;

	public int getStepSize() {
		if (m_StepSize != null){
			return m_StepSize;
		}
		ValueBinding  vb = getValueBinding("stepSize");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null)
				return intValue;
		}
		return 0;
	}

	public void setStepSize(Integer stepSize) {
		m_StepSize = stepSize;
	}

	
	public int getStartRow() {
		if (m_StartRow != null) {
			return m_StartRow;
		}
		ValueBinding  vb = getValueBinding("startRow");
		if (vb != null) {
			Integer intValue = (Integer) vb.getValue(getFacesContext());
			if (intValue != null)
				return intValue;
		}
		return 0;

	}

	public void setStartRow(int startRow) {
		m_StartRow = startRow;
	}

	public List<ColumnDefinition> getColumns() {
		return m_Columns;
	}

	public void setColumns(List<ColumnDefinition> columns) {
		m_Columns = columns;
	}

	public void addColumn(ColumnDefinition cdCurrent) {
		if (m_Columns == null) {
			m_Columns = new ArrayList<ColumnDefinition>();
		}
		m_Columns.add(cdCurrent);
	}

	public void setDataSource(DataSource dataSource) {
		m_DataSource = dataSource;
	}

	public DataSource getDataSource() {
		return m_DataSource;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[4];
		state[0] = super.saveState(context);
		state[1] = m_StartRow;
		state[2] = m_StepSize;
		state[3] = StateHolderUtil.saveList(context, m_Columns);
		state[4] = FacesUtil.objectToSerializable(context, m_DataSource);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_StartRow = (Integer) state[1];
		m_StepSize = (Integer) state[2];
		m_Columns = StateHolderUtil.restoreList(context, getComponent(),
				state[3]);
		m_DataSource = (DataSource) FacesUtil.objectFromSerializable(context,
				state[4]);

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
