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
package biz.webgate.dominoext.poi.component.data;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import biz.webgate.dominoext.poi.util.DatabaseProvider;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.RichTextItem;
import lotus.domino.View;

public class AttachmentTemplateSource extends AbstractTemplateSource implements ITemplateSource {

	String m_viewName;
	String m_keyName;
	String m_fieldName;

	private DataTempStore m_tempDataStore;

	protected static class DataTempStore {
		public Database m_Database;
		public View m_View;
		public Document m_Document;
		public RichTextItem m_Item;
		public EmbeddedObject m_EmbObject;
	}

	public String getViewName() {
		if (m_viewName != null) {
			return m_viewName;
		}
		ValueBinding vb = getValueBinding("viewName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setViewName(String viewName) {
		m_viewName = viewName;
	}

	public String getKeyName() {
		if (m_keyName != null) {
			return m_keyName;
		}
		ValueBinding vb = getValueBinding("keyName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setKeyName(String keyName) {
		m_keyName = keyName;
	}

	public String getFieldName() {
		if (m_fieldName != null) {
			return m_fieldName;
		}
		ValueBinding vb = getValueBinding("fieldName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setFieldName(String _fieldName) {
		m_fieldName = _fieldName;
	}

	@Override
	public InputStream getFileStream() {
		Logger log = LoggerFactory.getLogger(getClass().getCanonicalName());
		try {
			if (m_tempDataStore != null) {
				log.info("EmbeddedObject: " + m_tempDataStore.m_EmbObject);
				return m_tempDataStore.m_EmbObject.getInputStream();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int accessTemplate() {
		Logger log = LoggerFactory.getLogger(getClass().getCanonicalName());
		m_tempDataStore = null;
		String strView = getViewName();
		String strKey = getKeyName();
		String strFieldName = getFieldName();

		log.info("View: " + strView);
		log.info("Key: " + strKey);
		log.info("FieldName: " + strFieldName);
		if (strView == null || strKey == null || strFieldName == null) {
			return -1;
		}
		try {
			Database ndbAccess = getSourceDatabase(false);
			if (ndbAccess == null) {
				return -2;
			}
			View viwLUP = ndbAccess.getView(strView);
			if (viwLUP == null) {
				DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
				return -3;
			}
			Document docCurrent = viwLUP.getDocumentByKey(getKeyName(), true);
			if (docCurrent == null) {
				viwLUP.recycle();
				DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
				return -4;
			}
			if (!docCurrent.hasItem(strFieldName)) {
				docCurrent.recycle();
				viwLUP.recycle();
				DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
				return -5;
			}
			Item itmBody = docCurrent.getFirstItem(strFieldName);
			if (itmBody instanceof RichTextItem) {
				RichTextItem rtiBody = (RichTextItem) itmBody;
				if (rtiBody.getEmbeddedObjects().size() > 0) {
					EmbeddedObject eoCurrent = (EmbeddedObject) rtiBody.getEmbeddedObjects().elementAt(0);
					m_tempDataStore = new DataTempStore();
					m_tempDataStore.m_Database = ndbAccess;
					m_tempDataStore.m_Document = docCurrent;
					m_tempDataStore.m_Item = rtiBody;
					m_tempDataStore.m_View = viwLUP;
					m_tempDataStore.m_EmbObject = eoCurrent;
					return 1;
				} else {
					itmBody.recycle();
					docCurrent.recycle();
					viwLUP.recycle();
					DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);

					return -6;
				}
			} else {
				itmBody.recycle();
				docCurrent.recycle();
				viwLUP.recycle();
				DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);

				return -7;
			}
		} catch (Exception e) {
			return -10;
		}
	}

	@Override
	public void cleanUP() {
		try {
			if (m_tempDataStore != null) {
				m_tempDataStore.m_EmbObject.recycle();
				m_tempDataStore.m_Item.recycle();
				m_tempDataStore.m_Document.recycle();
				m_tempDataStore.m_View.recycle();
				DatabaseProvider.INSTANCE.handleRecylce(m_tempDataStore.m_Database);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		m_tempDataStore = null;

	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_fieldName = (String) state[1];
		m_keyName = (String) state[2];
		m_viewName = (String) state[3];

	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[4];
		state[0] = super.saveState(context);
		state[1] = m_fieldName;
		state[2] = m_keyName;
		state[3] = m_viewName;
		return state;

	}

}
