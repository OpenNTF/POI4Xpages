/**
 * Copyright (c) 2012-2021 WebGate Consulting AG and others
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.webgate.dominoext.poi.component.containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.image.IBookmarkImage;
import biz.webgate.dominoext.poi.component.kernel.DocumentProcessor;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.component.FacesAjaxComponent;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

public class UIDocument extends UIComponentBase implements FacesAjaxComponent {

	public static final String COMPONENT_TYPE = "biz.webgate.dominoext.poi.Document"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "biz.webgate.dominoext.poi.Document"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "biz.webgate.dominoext.poi.Document"; //$NON-NLS-1$

	private List<IDocumentBookmark> m_Bookmarks;
	private List<DocumentTable> m_Tables;
	private List<IBookmarkImage> m_BookmarksImages;
	private String m_pathInfo;
	private String m_DownloadFileName;
	private ITemplateSource m_TemplateSource;
	private MethodBinding m_PostGenerationProcess;
	private Boolean m_PdfOutput;

	public UIDocument() {

		super();
		setRendererType(RENDERER_TYPE);
	}

	public List<IDocumentBookmark> getBookmarks() {
		return m_Bookmarks;
	}

	public void setBookmarks(List<IDocumentBookmark> bookmarks) {
		m_Bookmarks = bookmarks;
	}

	public void addBookmark(IDocumentBookmark myBookmark) {
		if (m_Bookmarks == null) {
			m_Bookmarks = new ArrayList<IDocumentBookmark>();
		}
		m_Bookmarks.add(myBookmark);
	}

	public List<IBookmarkImage> getBookmarksImages() {
		return m_BookmarksImages;
	}

	public void setBookmarksImages(List<IBookmarkImage> bookmarksImages) {
		m_BookmarksImages = bookmarksImages;
	}

	public void addBookmarkImage(IBookmarkImage myBookmarkImage) {
		if (m_Bookmarks == null) {
			m_Bookmarks = new ArrayList<IDocumentBookmark>();
		}
		m_BookmarksImages.add(myBookmarkImage);
	}
	
	public List<DocumentTable> getTables() {
		return m_Tables;
	}

	public void setTables(List<DocumentTable> tables) {
		m_Tables = tables;
	}

	public void addTables(DocumentTable myTable) {
		if (m_Tables == null) {
			m_Tables = new ArrayList<DocumentTable>();
		}
		m_Tables.add(myTable);
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

	public boolean getPdfOutput() {
		if (null != m_PdfOutput) {
			return m_PdfOutput;
		}
		ValueBinding _vb = getValueBinding("pdfOutput"); //$NON-NLS-1$
		if (_vb != null) {
			return (Boolean) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return false;
		}
	}

	public void setPdfOutput(boolean pdfOutput) {
		m_PdfOutput = pdfOutput;
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
		DocumentProcessor.INSTANCE.processCall(context, this, false, null);
	}

	public ByteArrayOutputStream processRequestAndGetStream() throws IOException {
		ByteArrayOutputStream bos = null;
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		ITemplateSource itsCurrent = getTemplateSource();
		if (itsCurrent == null) {
			logCurrent.severe("No Template found!");
			return bos;
		}
		logCurrent.info("Start processing UIDocument generation");
		try {
			bos = DocumentProcessor.INSTANCE.generateNewByteArrayOutputStream(itsCurrent, getBookmarks(), getTables(), getBookmarksImages(), getFacesContext(), this);
		} catch (Exception e) {
			logCurrent.log(Level.SEVERE, "Error in UIGeneration", e);
		}
		return bos;
	}

	// SAVING AND RESTORING
	@Override
	public Object saveState(FacesContext context) {
		try {
			Object[] state = new Object[9];
			state[0] = super.saveState(context);
			state[1] = m_DownloadFileName;
			state[2] = m_pathInfo;
			state[3] = FacesUtil.objectToSerializable(context, m_TemplateSource);
			state[4] = StateHolderUtil.saveList(context, m_Bookmarks);
			state[5] = StateHolderUtil.saveMethodBinding(context, m_PostGenerationProcess);
			state[6] = m_PdfOutput;
			state[7] = StateHolderUtil.saveList(context, m_Tables);
			state[8] = StateHolderUtil.saveList(context, m_BookmarksImages);
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
		m_Bookmarks = StateHolderUtil.restoreList(context, this, state[4]);
		m_PostGenerationProcess = StateHolderUtil.restoreMethodBinding(context, this, state[5]);
		m_PdfOutput = (Boolean) state[6];
		m_Tables = StateHolderUtil.restoreList(context, this, state[7]);
		m_BookmarksImages = StateHolderUtil.restoreList(context, this, state[8]);

	}

	public MethodBinding getPostGenerationProcess() {
		return m_PostGenerationProcess;
	}

	public void setPostGenerationProcess(MethodBinding postGenerationProcess) {
		m_PostGenerationProcess = postGenerationProcess;
	}

	public boolean postGenerationProcess(FacesContext context, XWPFDocument document) {
		if (m_PostGenerationProcess != null) {
			Object[] params = null;
			if (m_PostGenerationProcess instanceof MethodBindingEx) {
				params = new Object[] { document };
				((MethodBindingEx) m_PostGenerationProcess).setComponent(this);
				((MethodBindingEx) m_PostGenerationProcess).setParamNames(s_postGenParamNames);
			}
			/*
			 * if (FacesUtil.isCancelled(m_PostGenerationProcess.invoke(context,
			 * params))) { return false; }
			*/
			doPostGenerationProcessPrivileged(context, params);
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

	
	private static final String[] s_postGenParamNames = { "xwpfdocument" }; // $NON-NLS-1$
}
