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
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.ibm.xsp.webapp.XspHttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UIDocument;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.document.table.cell.DocCellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;
import biz.webgate.dominoext.poi.component.kernel.document.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.util.MethodExecutor;
import biz.webgate.dominoext.poi.pdf.IPDFService;
import biz.webgate.dominoext.poi.pdf.PDFConversionService;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public enum DocumentProcessor {
	INSTANCE;

	/**
	 * gets the Instance of the DocumentProcessor.
	 *
	 * @deprecated -> Use DocumentProcessor.INSTANCE instead
	 *
	 * @return Instance of the DocumentProcessor
	 */
	@Deprecated
	public static DocumentProcessor getInstance() {
		return DocumentProcessor.INSTANCE;
	}

	public XWPFDocument getDocument(InputStream inDocument) {

		try {
			Logger log = LoggerFactory.getLogger(getClass().getCanonicalName());
			log.info("inDocument -> " + inDocument);
			XWPFDocument dxReturn = new XWPFDocument(inDocument);
			return dxReturn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int processBookmarks2Document(XWPFDocument dxProcess, List<IDocumentBookmark> arrBookmarks) {
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

	private void processBookmarks2Table(List<IDocumentBookmark> arrBookmarks, XWPFTable tabCurrent) {
		for (XWPFTableRow tabRow : tabCurrent.getRows()) {
			for (XWPFTableCell tabCell : tabRow.getTableCells()) {
				for (XWPFParagraph paraCurrent : tabCell.getParagraphs()) {
					processBookmarks2Paragraph(arrBookmarks, paraCurrent);
				}
			}
		}
	}

	private void processBookmarks2Paragraph(List<IDocumentBookmark> arrBookmarks, XWPFParagraph paraCurrent) {
		for (XWPFRun runCurrent : paraCurrent.getRuns()) {
			processBookmarks2Run(runCurrent, arrBookmarks);
		}
	}

	public int processBookmarks2Run(XWPFRun runCurrent, List<IDocumentBookmark> arrBookmarks) {
		String strText = runCurrent.getText(0);
		if (strText != null) {
			boolean found = false;
			for (IDocumentBookmark bmCurrent : arrBookmarks) {
				String strValue = bmCurrent.getValue();
				strValue = strValue == null ? "" : strValue;
				if (bmCurrent.getName() != null && strText.contains("<<" + bmCurrent.getName() + ">>")) {
					found = true;
					strText = strText.replace("<<" + bmCurrent.getName() + ">>", strValue);
				}
			}
			if (found) {
				String lines[] = strText.split("\\r?\\n");
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					if (i == 0) {
						runCurrent.setText(line, 0);
					} else {
						runCurrent.setText(line);
					}
					if (i + 1 < lines.length) {
						runCurrent.addBreak(BreakType.TEXT_WRAPPING);
					}
				}
			}
		}
		return 1;
	}

	public void generateNewFile(ITemplateSource itsCurrent, HttpServletResponse httpResponse, FacesContext context, UIDocument uiDoc, boolean noDownload, MethodBinding preDownload)
			throws IOException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		List<IDocumentBookmark> bookmarks = uiDoc.getBookmarks();
		List<DocumentTable> tables = uiDoc.getTables();
		String strFileName = uiDoc.getDownloadFileName();
		try {
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				XWPFDocument dxDocument = processDocument(itsCurrent, bookmarks, tables, context, uiDoc);
				if (uiDoc.getPdfOutput()) {

					logCurrent.info("Build PDF");
					ByteArrayOutputStream bosDoc = new ByteArrayOutputStream();
					dxDocument.write(bosDoc);
					bosDoc.flush();
					logCurrent.info("Size of worddocument ByteArrayOutputStream: " + bosDoc.size());
					ByteArrayInputStream is = new ByteArrayInputStream(bosDoc.toByteArray());
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IPDFService isCurrent = PDFConversionService.getInstance().getPDFService();

					isCurrent.buildPDF(is, bos);
					is.close();
					bos.flush();

					MethodExecutor.INSTANCE.execute(preDownload, uiDoc, context, bos);
					if (!noDownload) {
						logCurrent.info("Size of pdf ByteArrayOutputStream: " + bos.size());
						httpResponse.setContentType("application/octet-stream");
						httpResponse.addHeader("Content-disposition", "inline; filename=\"" + strFileName + "\"");
						OutputStream os = httpResponse.getOutputStream();
						bos.writeTo(os);
						bos.close();
						os.close();
					} else {
						OutputStream os = httpResponse.getOutputStream();
						os.close();
					}
				} else {
					logCurrent.info("Build docx");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					dxDocument.write(bos);
					MethodExecutor.INSTANCE.execute(preDownload, uiDoc, context, bos);
					if (!noDownload) {
						httpResponse.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
						httpResponse.addHeader("Content-disposition", "inline; filename=\"" + strFileName + "\"");
						OutputStream os = httpResponse.getOutputStream();
						bos.writeTo(os);
						bos.close();
						os.close();
					} else {
						OutputStream os = httpResponse.getOutputStream();
						os.close();
					}
				}
			} else {
				ErrorPageBuilder.getInstance().processError(httpResponse, "TemplateAccess Problem NR: " + nTemplateAccess, null);
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "Error during Documentgeneration", e);
		}
	}

	public XWPFDocument processDocument(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks, List<DocumentTable> tables, FacesContext context, UIDocument uiDocument) throws POIException {

		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		InputStream is = itsCurrent.getFileStream();
		XWPFDocument dxDocument = getDocument(is);
		itsCurrent.cleanUP();
		if (bookmarks != null && bookmarks.size() > 0) {
			processBookmarks2Document(dxDocument, bookmarks);
		}
		logCurrent.finest("Start export Tables");
		if (tables != null && tables.size() > 0) {
			for (DocumentTable tblExport : tables) {
				XWPFTable dxTable = EmbeddedDataSourceExportProcessor.INSTANCE.processExportTable(tblExport, dxDocument, context, tblExport.getVar(), tblExport.getIndex());
				logCurrent.finest("exportTable created");
				// logCurrent.finer("Start Processing Cells");
				if (tblExport.getDocCellValues() != null && tblExport.getDocCellValues().size() > 0) {
					for (ICellValue iCV : tblExport.getDocCellValues()) {
						if (iCV instanceof DocCellValue) {
							DocCellValue cv = (DocCellValue) iCV;
							if (cv.getRowNumber() <= tblExport.getMaxRow()) {
								EmbeddedDataSourceExportProcessor.INSTANCE.setDocCellValue(dxTable, cv.getRowNumber(), cv.getColumnNumber(), cv.getValue(), tblExport.getMaxRow(), false);
							} else {
								logCurrent.finer("MaxValue < CellValue.getRowNumber()");
							}
						}
					}

				}
			}

		}

		if (uiDocument != null) {
			uiDocument.postGenerationProcess(context, dxDocument);
		}
		return dxDocument;
	}

	public void processCall(FacesContext context, UIDocument uiDocument, boolean noDownload, MethodBinding preDownload) {
		HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		if (httpResponse instanceof XspHttpServletResponse) {
			XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
			r.setCommitted(true);
			httpResponse = r.getDelegate();
		}

		ITemplateSource itsCurrent = uiDocument.getTemplateSource();
		if (itsCurrent == null) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "No Templatesource found!", null);
			logCurrent.severe("No Template found!");
			return;
		}
		logCurrent.info("Start processing UIDocument generation");
		try {
			DocumentProcessor.INSTANCE.generateNewFile(itsCurrent, httpResponse, context, uiDocument, noDownload, preDownload);
		} catch (Exception e) {
			try {
				logCurrent.log(Level.SEVERE, "Error in UIGeneration", e);
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

}
