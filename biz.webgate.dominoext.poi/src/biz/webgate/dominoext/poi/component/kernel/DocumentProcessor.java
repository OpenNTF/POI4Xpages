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
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import biz.webgate.dominoext.poi.component.containers.UIDocument;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.util.ErrorPageBuilder;

public class DocumentProcessor {

	private static DocumentProcessor m_Processor;

	private DocumentProcessor() {

	}

	public static DocumentProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new DocumentProcessor();
		}
		return m_Processor;

	}

	public XWPFDocument getDocument(InputStream inDocument) {

		try {
			XWPFDocument dxReturn = new XWPFDocument(inDocument);
			return dxReturn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int processBookmarks2Document(XWPFDocument dxProcess,
			List<IDocumentBookmark> arrBookmarks) {
		// First Prozessing all paragraphs.
		for (XWPFParagraph paraCurrent : dxProcess.getParagraphs()) {
			processBookmarks2Paragraph(arrBookmarks, paraCurrent);
		}

		for (XWPFTable tabCurrent : dxProcess.getTables()) {
			processBookmarks2Table(arrBookmarks, tabCurrent);
		}
		for (XWPFHeader headCurrent : dxProcess.getHeaderList()) {
			for (XWPFParagraph paraCurrent : headCurrent.getParagraphs()) {
				processBookmarks2Paragraph(arrBookmarks, paraCurrent);
			}
			for (XWPFTable tabCurrent : headCurrent.getTables()) {
				processBookmarks2Table(arrBookmarks, tabCurrent);
			}
		}
		for (XWPFFooter footCurrent : dxProcess.getFooterList()) {
			for (XWPFParagraph paraCurrent : footCurrent.getParagraphs()) {
				processBookmarks2Paragraph(arrBookmarks, paraCurrent);
			}
			for (XWPFTable tabCurrent : footCurrent.getTables()) {
				processBookmarks2Table(arrBookmarks, tabCurrent);
			}
		}

		return 1;
	}

	private void processBookmarks2Table(List<IDocumentBookmark> arrBookmarks,
			XWPFTable tabCurrent) {
		for (XWPFTableRow tabRow : tabCurrent.getRows()) {
			for (XWPFTableCell tabCell : tabRow.getTableCells()) {
				for (XWPFParagraph paraCurrent : tabCell.getParagraphs()) {
					processBookmarks2Paragraph(arrBookmarks, paraCurrent);
				}
			}
		}
	}

	private void processBookmarks2Paragraph(
			List<IDocumentBookmark> arrBookmarks, XWPFParagraph paraCurrent) {
		for (XWPFRun runCurrent : paraCurrent.getRuns()) {
			processBookmarks2Run(runCurrent, arrBookmarks);
		}
	}

	public int processBookmarks2Run(XWPFRun runCurrent,
			List<IDocumentBookmark> arrBookmarks) {
		String strText = runCurrent.getText(0);
		if (strText != null) {
			for (IDocumentBookmark bmCurrent : arrBookmarks) {
				String strValue = bmCurrent.getValue();
				strValue = strValue == null ? "" : strValue;
				if (bmCurrent.getName() != null) {
					strText = strText.replaceAll("<<" + bmCurrent.getName()
							+ ">>", strValue);
				}
			}
		}
		runCurrent.setText(strText, 0);
		return 1;
	}

	public void generateNewFile(ITemplateSource itsCurrent,
			List<IDocumentBookmark> bookmarks,
			HttpServletResponse httpResponse, String strFileName,
			FacesContext context, UIDocument uiDoc) throws IOException {
		try {
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				XWPFDocument dxDocument = processDocument(itsCurrent,
						bookmarks, context, uiDoc);
				httpResponse.setContentType("application/octet-stream");
				httpResponse.addHeader("Content-disposition",
						"inline; filename=\"" + strFileName + "\"");
				OutputStream os = httpResponse.getOutputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				dxDocument.write(bos);
				bos.writeTo(os);
				os.close();
			} else {
				ErrorPageBuilder.getInstance().processError(httpResponse,
						"TemplateAccess Problem NR: " + nTemplateAccess, null);
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse,
					"Error during Documentgeneration", e);
		}
	}

	public XWPFDocument processDocument(ITemplateSource itsCurrent,
			List<IDocumentBookmark> bookmarks, FacesContext context,
			UIDocument uiDocument) {
		InputStream is = itsCurrent.getFileStream();
		XWPFDocument dxDocument = getDocument(is);
		itsCurrent.cleanUP();
		if (bookmarks != null && bookmarks.size() > 0) {
			processBookmarks2Document(dxDocument, bookmarks);
		}
		if (uiDocument != null) {
			uiDocument.postGenerationProcess(context, dxDocument);
		}
		return dxDocument;
	}
}
