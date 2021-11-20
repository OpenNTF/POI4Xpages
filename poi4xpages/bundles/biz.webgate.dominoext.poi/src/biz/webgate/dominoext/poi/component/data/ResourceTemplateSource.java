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
package biz.webgate.dominoext.poi.component.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import biz.webgate.dominoext.poi.util.DatabaseProvider;
import biz.webgate.dominoext.poi.util.POILibUtil;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DxlExporter;
import lotus.domino.Item;
import lotus.domino.MIMEEntity;
import lotus.domino.NoteCollection;
import lotus.domino.Session;
import lotus.domino.Stream;

public class ResourceTemplateSource extends AbstractTemplateSource implements ITemplateSource {

	private String m_FileName;

	private TemplateData m_Data;

	private static class TemplateData {
		public String m_Base64data;
	}

	public String getFileName() {
		if (m_FileName != null) {
			return m_FileName;
		}
		ValueBinding vb = getValueBinding("fileName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setFileName(String _fieldName) {
		m_FileName = _fieldName;
	}

	@Override
	public InputStream getFileStream() {
		if (m_Data == null) {
			return null;
		}
		try {
			Session sesCurrent = POILibUtil.getCurrentSession();
			Stream stream = sesCurrent.createStream();
			Stream streamOut = sesCurrent.createStream();
			Document docTMP = sesCurrent.getCurrentDatabase().createDocument();
			MIMEEntity mime = docTMP.createMIMEEntity("body");
			stream.writeText(m_Data.m_Base64data);
			mime.setContentFromText(stream, "binary", MIMEEntity.ENC_BASE64);
			mime.getContentAsBytes(streamOut);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			streamOut.getContents(bos);
			docTMP.recycle();
			mime.recycle();
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			return bis;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int accessTemplate() {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		String strFileName = getFileName();
		if (strFileName == null) {
			return -1;
		}
		try {
			Database ndbAccess = getSourceDatabase(true);
			if (ndbAccess == null) {
				return -2;
			}
			Session sesSigner = POILibUtil.getCurrentSessionAsSigner();
			if (sesSigner == null) {
				sesSigner = POILibUtil.getCurrentSession();
			}
			logCurrent.info("Getting NoteCollection");
			NoteCollection ncCurrent = ndbAccess.createNoteCollection(false);
			logCurrent.info("Select only MiscFormaElements");
			// ncCurrent.selectAllDesignElements(true);
			ncCurrent.setSelectMiscFormatElements(true);
			logCurrent.info("buildColleciton");
			// ncCurrent.selectAllCodeElements(true);
			ncCurrent.buildCollection();
			logCurrent.info("browseColllection");
			String strID = ncCurrent.getFirstNoteID();
			while (strID != null) {
				Document docNode = ndbAccess.getDocumentByID(strID);

				if (docNode.hasItem("$Title") && docNode.hasItem("$FileData")) {
					Item itmTitle = docNode.getFirstItem("$Title");
					if (itmTitle.containsValue(strFileName)) {
						String str64 = getBASE64StringFormFileResource(docNode, sesSigner);
						if (str64 != null) {
							m_Data = new TemplateData();
							m_Data.m_Base64data = str64;
							strID = null;
						}
					}
				}
				if (strID != null) {
					strID = ncCurrent.getNextNoteID(strID);
					if (strID != null && strID.length() == 0) {
						strID = null;
					}
				}
				docNode.recycle();
			}
			logCurrent.info("DONE");

			ncCurrent.recycle();
			DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
			if (m_Data != null) {
				return 1;
			}
			// ndbAccess.get
		} catch (Exception e) {
			e.printStackTrace();
			return -9;
		}
		return -4;
	}

	@Override
	public void cleanUP() {
		m_Data = null;
	}

	private String getBASE64StringFormFileResource(Document docCurrent, Session sesCurrent) {
		try {
			DxlExporter dxle = sesCurrent.createDxlExporter();
			dxle.setOutputDOCTYPE(false);
			String strDOM = dxle.exportDxl(docCurrent);

			DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(strDOM));
			org.w3c.dom.Document domDoc = build.parse(is);
			NodeList nlCurrent = domDoc.getElementsByTagName("filedata");
			if (nlCurrent.getLength() == 0) {
				return null;
			} else {
				Node nCheck = nlCurrent.item(0);
				return ((org.w3c.dom.Text) nCheck.getFirstChild()).getTextContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_FileName = (String) state[1];

	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[2];
		state[0] = super.saveState(context);
		state[1] = m_FileName;
		return state;

	}
}
