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
package biz.webgate.dominoext.poi.component.containers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.component.kernel.CSVProcessor;
import biz.webgate.dominoext.poi.component.sources.IExportSource;

public class UICSV extends UIComponentBase implements FacesAjaxComponent {

	public static final String COMPONENT_TYPE = "biz.webgate.dominoext.poi.CSV"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "biz.webgate.dominoext.poi.CSV"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "biz.webgate.dominoext.poi.CSV"; //$NON-NLS-1$

	private String m_pathInfo;
	private String m_DownloadFileName;
	private List<CSVColumn> m_Columns;

	private IExportSource m_DataSource;
	private String m_DataSourceVar;
	private String m_Var;
	private String m_Index;
	private Boolean m_includeHeader;

	public UICSV() {

		super();
		setRendererType(RENDERER_TYPE);
	}

	public boolean isIncludeHeader() {
		if (m_includeHeader != null) {
			return m_includeHeader;
		}
		ValueBinding _vb = getValueBinding("includeHeader"); //$NON-NLS-1$
		if (_vb != null) {
			return (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return true;
	}

	public void setIncludeHeader(boolean includeHeader) {
		m_includeHeader = includeHeader;
	}

	public String getPathInfo() {
		if (null != m_pathInfo) {
			return m_pathInfo;
		}
		ValueBinding _vb = getValueBinding("pathInfo"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setPathInfo(String linkName) {
		m_pathInfo = linkName;
	}

	public String getDownloadFileName() {
		if (null != m_DownloadFileName) {
			return m_DownloadFileName;
		}
		ValueBinding _vb = getValueBinding("downloadFileName"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setDownloadFileName(String downloadFileName) {
		m_DownloadFileName = downloadFileName;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public boolean handles(FacesContext context) {
		String reqPathInfo = context.getExternalContext().getRequestPathInfo();
		if (StringUtil.isNotEmpty(reqPathInfo)) {
			String pathInfo = getPathInfo();
			if (StringUtil.isEmpty(pathInfo)) {
				return false;
			}
			if (!pathInfo.startsWith("/")) {
				pathInfo = "/" + pathInfo;
			}
			if (!pathInfo.endsWith("/")) {
				pathInfo += "/";
			}
			if (!reqPathInfo.startsWith("/")) {
				reqPathInfo = "/" + reqPathInfo;
			}
			if (!reqPathInfo.endsWith("/")) {
				reqPathInfo += "/";
			}
			if (reqPathInfo.startsWith(pathInfo)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processAjaxRequest(FacesContext context) throws IOException {
		CSVProcessor.getInstance().processCall(context, this, false, null);
	}

	// SAVING AND RESTORING
	@Override
	public Object saveState(FacesContext context) {
		try {
			Object[] state = new Object[9];
			state[0] = super.saveState(context);
			state[1] = m_DownloadFileName;
			state[2] = m_pathInfo;
			state[3] = FacesUtil.objectToSerializable(context, m_DataSource);
			state[4] = StateHolderUtil.saveList(context, m_Columns);
			state[5] = m_DataSourceVar;
			state[6] = m_Index;
			state[7] = m_Var;
			state[8] = m_includeHeader;

			return state;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_DownloadFileName = (String) state[1];
		m_pathInfo = (String) state[2];
		m_DataSource = (IExportSource) FacesUtil.objectFromSerializable(context, this, state[3]);
		m_Columns = StateHolderUtil.restoreList(context, this, state[4]);
		m_DataSourceVar = (String) state[5];
		m_Index = (String) state[6];
		m_Var = (String) state[7];
		m_includeHeader = (Boolean) state[8];
	}

	public List<CSVColumn> getColumns() {
		return m_Columns;
	}

	public void addColumn(CSVColumn col) {
		if (m_Columns == null) {
			m_Columns = new ArrayList<>();
		}
		m_Columns.add(col);
	}

	public void setColumns(List<CSVColumn> columns) {
		m_Columns = columns;
	}

	public IExportSource getDataSource() {
		return m_DataSource;
	}

	public void setDataSource(IExportSource dataSource) {
		m_DataSource = dataSource;
	}

	public String getDataSourceVar() {
		if (m_DataSourceVar != null) {
			return m_DataSourceVar;
		}
		ValueBinding _vb = getValueBinding("dataSourceVar"); //$NON-NLS-1$
		if (_vb != null) {
			return (String) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return null;

	}

	public void setDataSourceVar(String dataSourceVar) {
		m_DataSourceVar = dataSourceVar;
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

	public DataSource getPageDataSource() {
		String strName = getDataSourceVar();
		if (StringUtil.isNotEmpty(strName)) {

			UIViewRoot vrCurrent = getFacesContext().getViewRoot();
			if (vrCurrent instanceof UIViewRootEx) {
				for (DataSource dsCurrent : ((UIViewRootEx) vrCurrent).getData()) {
					if (strName.equals(dsCurrent.getVar())) {
						return dsCurrent;
					}
				}
			}
		}
		return null;
	}

}
