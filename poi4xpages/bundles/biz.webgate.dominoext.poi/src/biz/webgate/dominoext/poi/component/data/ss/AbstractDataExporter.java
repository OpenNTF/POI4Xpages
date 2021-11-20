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

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.util.FacesUtil;

import biz.webgate.dominoext.poi.component.sources.IExportSource;

public abstract class AbstractDataExporter extends ValueBindingObjectImpl {

	private String m_Var;
	private String m_Index;
	private Integer m_StepSize;
	private IExportSource m_DataSource;
	private String m_DataSourceVar;

	public AbstractDataExporter() {
		super();
	}

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
		return 1;
	}

	public void setStepSize(int stepSize) {
		m_StepSize = stepSize;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[6];
		state[0] = super.saveState(context);
		state[1] = m_StepSize;
		state[2] = FacesUtil.objectToSerializable(getFacesContext(),
				m_DataSource);
		state[3] = m_DataSourceVar;
		state[4] = m_Var;
		state[5] = m_Index;
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_StepSize = (Integer) state[1];
		m_DataSource = (IExportSource) FacesUtil.objectFromSerializable(
				getFacesContext(), getComponent(), state[2]);
		m_DataSourceVar = (String) state[3];
		m_Var = (String) state[4];
		m_Index = (String)state[5];

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

	public String getDataSourceVar() {
		if (m_DataSourceVar != null) {
			return m_DataSourceVar;
		}
		ValueBinding vb = getValueBinding("dataSourceVar");
		if (vb != null) {
			String strValue = (String) vb.getValue(getFacesContext());
			if (strValue != null)
				return strValue;
		}
		return null;

	}

	public void setDataSourceVar(String dataSourceVar) {
		m_DataSourceVar = dataSourceVar;
	}

	public DataSource getPageDataSource() {
		String strName = getDataSourceVar();
		System.out.println(strName);
		if (StringUtil.isNotEmpty(strName)) {

			UIViewRoot vrCurrent = getFacesContext().getViewRoot();
			if (vrCurrent instanceof UIViewRootEx) {
				for (DataSource dsCurrent : ((UIViewRootEx) vrCurrent)
						.getData()) {
					if (strName.equals(dsCurrent.getVar())) {
						return dsCurrent;
					}
				}
			}
		}
		System.out.println("Datasource name:" + m_DataSourceVar);
		return null;

	}

}