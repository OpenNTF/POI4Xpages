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

import com.ibm.xsp.util.StateHolderUtil;

public class Data2RowExporter extends AbstractDataExporter implements
		IListDataExporter {
	private Integer m_StartRow;
	private List<ColumnDefinition> m_Columns;

	public int getStartRow() {
		if (m_StartRow != null) {
			return m_StartRow;
		}
		ValueBinding vb = getValueBinding("startRow");
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

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[5];
		state[0] = super.saveState(context);
		state[1] = m_StartRow;
		state[2] = StateHolderUtil.saveList(context, m_Columns);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_StartRow = (Integer) state[1];
		m_Columns = StateHolderUtil.restoreList(context, getComponent(),
				state[2]);

	}
}
