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
package biz.webgate.dominoext.poi.component.containers;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.http.HttpServletResponse;

import biz.webgate.dominoext.poi.component.kernel.SimpleViewExportProcessor;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.webapp.XspHttpServletResponse;

public class UISimpleViewExport extends UIComponentBase implements FacesAjaxComponent {

	public static final String COMPONENT_TYPE = "biz.webgate.dominoext.poi.SVE"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "biz.webgate.dominoext.poi.SVE"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "biz.webgate.dominoext.poi.SVE"; //$NON-NLS-1$

	private String m_PathInfo;
	private String m_DownloadFileName;
	private String m_Database;
	private String m_View;
	private String m_Key;
	private String m_Search;
	private Boolean m_IncludeHeader;
	private String m_ExportFormat;

	public UISimpleViewExport() {

		super();
		setRendererType(RENDERER_TYPE);
	}

	public boolean isIncludeHeader() {
		if (m_IncludeHeader != null) {
			return m_IncludeHeader;
		}
		ValueBinding _vb = getValueBinding("includeHeader"); //$NON-NLS-1$
		if (_vb != null) {
			return (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
		}
		return true;
	}

	public void setIncludeHeader(boolean includeHeader) {
		m_IncludeHeader = includeHeader;
	}

	public String getPathInfo() {
		if (null != m_PathInfo) {
			return m_PathInfo;
		}
		ValueBinding _vb = getValueBinding("pathInfo"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setPathInfo(String linkName) {
		m_PathInfo = linkName;
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

	public String getDatabase() {
		if (m_Database != null) {
			return m_Database;
		}
		ValueBinding _vb = getValueBinding("database"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setDatabase(String database) {
		m_Database = database;
	}

	public String getView() {
		if (m_View != null) {
			return m_View;
		}
		ValueBinding _vb = getValueBinding("view"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setView(String view) {
		m_View = view;
	}

	public String getKey() {
		if (m_Key != null) {
			return m_Key;
		}
		ValueBinding _vb = getValueBinding("key"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setKey(String key) {
		m_Key = key;
	}

	public String getSearch() {
		if (m_Search != null) {
			return m_Search;
		}
		ValueBinding _vb = getValueBinding("search"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setSearch(String search) {
		m_Search = search;
	}

	public String getExportFormat() {
		if (m_ExportFormat != null) {
			return m_ExportFormat;
		}
		ValueBinding _vb = getValueBinding("exportFormat"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setExportFormat(String exportFormat) {
		m_ExportFormat = exportFormat;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

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

	public void processAjaxRequest(FacesContext context) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();

		// Disable the XPages response buffer as this will collide with the
		// engine one
		// We mark it as committed and use its delegate instead
		if (httpResponse instanceof XspHttpServletResponse) {
			XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
			r.setCommitted(true);
			httpResponse = r.getDelegate();
		}

		try {
			SimpleViewExportProcessor processor = SimpleViewExportProcessor.getInstance(this, httpResponse);
			if (processor != null) {
				processor.generateNewFile(this, httpResponse, context);
			}
		} catch (Exception e) {
			try {
				e.printStackTrace();
				e.printStackTrace(httpResponse.getWriter());
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e);
			} catch (Exception e2) {
				e2.printStackTrace();
				e.printStackTrace();
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e2);

			}
		}
	}

	// SAVING AND RESTORING
	@Override
	public Object saveState(FacesContext context) {
		try {
			Object[] state = new Object[9];
			state[0] = super.saveState(context);
			state[1] = m_DownloadFileName;
			state[2] = m_PathInfo;
			state[3] = m_Database;
			state[4] = m_View;
			state[5] = m_Key;
			state[6] = m_Search;
			state[7] = m_IncludeHeader;
			state[8] = m_ExportFormat;

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
		m_PathInfo = (String) state[2];
		m_Database = (String) state[3];
		m_View = (String) state[4];
		m_Key = (String) state[5];
		m_Search = (String) state[6];
		m_IncludeHeader = (Boolean) state[7];
		m_ExportFormat = (String) state[8];
	}
}
