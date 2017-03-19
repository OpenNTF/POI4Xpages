/*
 * � Copyright WebGate Consulting AG, 2012
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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import org.apache.poi.ss.usermodel.Workbook;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;

public class UIWorkbook extends UIComponentBase implements FacesAjaxComponent {

	public static final String COMPONENT_TYPE = "biz.webgate.dominoext.poi.Workbook"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "biz.webgate.dominoext.poi.Workbook"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "biz.webgate.dominoext.poi.Workbook"; //$NON-NLS-1$

	private List<Spreadsheet> m_Spreadsheets;
	private String m_pathInfo;
	private String m_DownloadFileName;
	private ITemplateSource m_TemplateSource;
	private MethodBinding m_PostGenerationProcess;
	private boolean m_UseStreamingModel = false;

	public boolean isUseStreamingModel() {
		return m_UseStreamingModel;
	}

	public void setUseStreamingModel(boolean useStreamingModel) {
		m_UseStreamingModel = useStreamingModel;
	}

	public UIWorkbook() {

		super();
		setRendererType(RENDERER_TYPE);
	}

	public List<Spreadsheet> getSpreadsheets() {
		return m_Spreadsheets;
	}

	public void setSpreadsheets(List<Spreadsheet> tables) {
		m_Spreadsheets = tables;
	}

	public void addSpreadsheet(Spreadsheet tbCurrent) {
		if (m_Spreadsheets == null) {
			m_Spreadsheets = new ArrayList<Spreadsheet>();
		}
		m_Spreadsheets.add(tbCurrent);
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

	public ITemplateSource getTemplateSource() {
		return m_TemplateSource;
	}

	public void setTemplateSource(ITemplateSource templateSource) {
		m_TemplateSource = templateSource;
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
		WorkbookProcessor.INSTANCE.processCall(context, this, false, null);
	}

	public MethodBinding getPostGenerationProcess() {
		return m_PostGenerationProcess;
	}

	public void setPostGenerationProcess(MethodBinding postGenerationProcess) {
		m_PostGenerationProcess = postGenerationProcess;
	}

	// SAVING AND RESTORING
	@Override
	public Object saveState(FacesContext context) {
		try {
			Object[] state = new Object[7];
			state[0] = super.saveState(context);
			state[1] = m_DownloadFileName;
			state[2] = m_pathInfo;
			state[3] = FacesUtil.objectToSerializable(context, m_TemplateSource);
			state[4] = StateHolderUtil.saveList(context, m_Spreadsheets);
			state[5] = StateHolderUtil.saveMethodBinding(context, m_PostGenerationProcess);
			state[6] = Boolean.valueOf(m_UseStreamingModel);
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
		m_TemplateSource = (ITemplateSource) FacesUtil.objectFromSerializable(context, this, state[3]);
		m_Spreadsheets = StateHolderUtil.restoreList(context, this, state[4]);
		m_PostGenerationProcess = StateHolderUtil.restoreMethodBinding(context, this, state[5]);
		m_UseStreamingModel = ((Boolean) state[6]).booleanValue();
	}

	public boolean postGenerationProcess(FacesContext context, Workbook wbCurrent) {
		if (m_PostGenerationProcess != null) {
			Object[] params = null;
			if (m_PostGenerationProcess instanceof MethodBindingEx) {
				params = new Object[] { wbCurrent };
				((MethodBindingEx) m_PostGenerationProcess).setComponent(this);
				((MethodBindingEx) m_PostGenerationProcess).setParamNames(s_postGenParamNames);
			}

			doPostGenerationProcessPrivileged(context, params);

			/*
			 * if (FacesUtil.isCancelled(m_PostGenerationProcess.invoke(context,
			 * params))) { return false; }
			 */
			return true;
		}
		return true;
	}

	public void doPostGenerationProcessPrivileged(final FacesContext context, final Object[] params) {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
				public Void run() throws Exception {
					m_PostGenerationProcess.invoke(context, params);
					return null;
				}
			});
		} catch (PrivilegedActionException e) {
			e.printStackTrace();
		}
	}

	private static final String[] s_postGenParamNames = { "workbook" }; // $NON-NLS-1$

}
