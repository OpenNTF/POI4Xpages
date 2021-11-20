/**
 * Copyright (c) 2012-2021 WebGate Consulting AG and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.webgate.dominoext.poi.component.data.ss;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.util.StateHolderUtil;

import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;

public class Data2ColumnExporter extends AbstractDataExporter implements
		IListDataExporter {

	List<RowDefinition> m_Rows;
	Integer m_StartColumn;

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
			m_Rows = new ArrayList<>();
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
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = m_StartColumn;
		state[2] = StateHolderUtil.saveList(getFacesContext(), m_Rows);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_StartColumn = (Integer) state[1];
		m_Rows = StateHolderUtil.restoreList(getFacesContext(), getComponent(),
				state[2]);

	}

}
