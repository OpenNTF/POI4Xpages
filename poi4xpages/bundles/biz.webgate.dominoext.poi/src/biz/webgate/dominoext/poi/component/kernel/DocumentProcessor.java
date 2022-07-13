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
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import com.ibm.xsp.webapp.XspHttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UIDocument;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.document.table.cell.DocCellValue;
import biz.webgate.dominoext.poi.component.data.image.BookmarkImage;
import biz.webgate.dominoext.poi.component.data.image.IBookmarkImage;
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

	public int processBookmarks2Document(XWPFDocument dxProcess, List<IDocumentBookmark> arrBookmarks) throws XmlException {
		// First Prozessing all paragraphs.
		for (XWPFParagraph paraCurrent : dxProcess.getParagraphs()) {
			processBookmarks2Paragraph(arrBookmarks, paraCurrent);
		}
		
		// TextBoxes
		for (XWPFParagraph paraCurrent : dxProcess.getParagraphs()) {
			processBookmarks2TextBox(arrBookmarks, paraCurrent);
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
	
	public int processBookmarks2Image(XWPFDocument dxProcess, List<IBookmarkImage> arrBookmarksImages) throws XmlException {
		// First Prozessing all paragraphs.
		for (XWPFParagraph paraCurrent : dxProcess.getParagraphs()) {
			processBookmarksImages2Paragraph(arrBookmarksImages, paraCurrent);
		}
		
		// TextBoxes
		for (XWPFParagraph paraCurrent : dxProcess.getParagraphs()) {
			processBookmarksImages2TextBox(arrBookmarksImages, paraCurrent);
		}

		for (XWPFTable tabCurrent : dxProcess.getTables()) {
			processBookmarksImages2Table(arrBookmarksImages, tabCurrent);
		}
		for (XWPFHeader headCurrent : dxProcess.getHeaderList()) {
			for (XWPFParagraph paraCurrent : headCurrent.getParagraphs()) {
				processBookmarksImages2Paragraph(arrBookmarksImages, paraCurrent);
			}
			for (XWPFTable tabCurrent : headCurrent.getTables()) {
				processBookmarksImages2Table(arrBookmarksImages, tabCurrent);
			}
		}
		for (XWPFFooter footCurrent : dxProcess.getFooterList()) {
			for (XWPFParagraph paraCurrent : footCurrent.getParagraphs()) {
				processBookmarksImages2Paragraph(arrBookmarksImages, paraCurrent);
			}
			for (XWPFTable tabCurrent : footCurrent.getTables()) {
				processBookmarksImages2Table(arrBookmarksImages, tabCurrent);
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
	
	private void processBookmarksImages2Table(List<IBookmarkImage> arrBookmarks, XWPFTable tabCurrent) {
		for (XWPFTableRow tabRow : tabCurrent.getRows()) {
			for (XWPFTableCell tabCell : tabRow.getTableCells()) {
				for (XWPFParagraph paraCurrent : tabCell.getParagraphs()) {
					processBookmarksImages2Paragraph(arrBookmarks, paraCurrent);
				}
			}
		}
	}

	private void processBookmarks2Paragraph(List<IDocumentBookmark> arrBookmarks, XWPFParagraph paraCurrent) {
		for (XWPFRun runCurrent : paraCurrent.getRuns()) {
			processBookmarks2Run(runCurrent, arrBookmarks);
		}
	}
	
	private void processBookmarksImages2Paragraph(List<IBookmarkImage> arrBookmarks, XWPFParagraph paraCurrent) {
		for (XWPFRun runCurrent : paraCurrent.getRuns()) {
			processBookmarksImages2Run(runCurrent, arrBookmarks);
		}
	}
	
	private void processBookmarks2TextBox(List<IDocumentBookmark> arrBookmarks, XWPFParagraph paraCurrent) throws XmlException {		
		XmlCursor cursor = paraCurrent.getCTP().newCursor();
		cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//*/w:txbxContent/w:p/w:r");

		List<XmlObject> ctrsintxtbx = new ArrayList<XmlObject>();

		while(cursor.hasNextSelection()) {
			cursor.toNextSelection();
			XmlObject obj = cursor.getObject();
			ctrsintxtbx.add(obj);
		}
		for (XmlObject obj : ctrsintxtbx) {
			CTR ctr = CTR.Factory.parse(obj.xmlText());
			XWPFRun runCurrent = new XWPFRun(ctr, (IRunBody)paraCurrent);			
			processBookmarks2Run(runCurrent, arrBookmarks);
			obj.set(runCurrent.getCTR());
		}
	}
	
	private void processBookmarksImages2TextBox(List<IBookmarkImage> arrBookmarks, XWPFParagraph paraCurrent) throws XmlException {		
		XmlCursor cursor = paraCurrent.getCTP().newCursor();
		cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//*/w:txbxContent/w:p/w:r");

		List<XmlObject> ctrsintxtbx = new ArrayList<XmlObject>();

		while(cursor.hasNextSelection()) {
			cursor.toNextSelection();
			XmlObject obj = cursor.getObject();
			ctrsintxtbx.add(obj);
		}
		for (XmlObject obj : ctrsintxtbx) {
			CTR ctr = (CTR) CTR.Factory.parse(obj.xmlText());
			XWPFRun runCurrent = new XWPFRun(ctr, (IRunBody)paraCurrent);			
			processBookmarksImages2Run(runCurrent, arrBookmarks);
			obj.set(runCurrent.getCTR());
		}
	}

	 private void addPicture(final String fileName, final String typeFichier, final int extension, final int id, int width, int height, final XWPFRun run) {		 
		 InputStream fileInputStream = null;

	     try {
	    	if (BookmarkImage.FILETYPE_FILE.equals(typeFichier))
	    		fileInputStream = new FileInputStream(fileName);
	    	else if (BookmarkImage.FILETYPE_URL.equals(typeFichier))
	    		fileInputStream = new URL(fileName).openStream();
	    	
			final String blipId = run.getDocument().addPictureData(fileInputStream, extension);
			
			final int EMU = 9525;
			width *= EMU;
			height *= EMU;
			// String blipId =
			// getAllPictures().get(id).getPackageRelationship().getId();

			final CTInline inline = run.getCTR().addNewDrawing().addNewInline();

			final String picXml = ""
					+ "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
					+ "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
					+ "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
					+ "         <pic:nvPicPr>" + "            <pic:cNvPr id=\""
					+ id
					+ "\" name=\"Generated\"/>"
					+ "            <pic:cNvPicPr/>"
					+ "         </pic:nvPicPr>"
					+ "         <pic:blipFill>"
					+ "            <a:blip r:embed=\""
					+ blipId
					+ "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
					+ "            <a:stretch>"
					+ "               <a:fillRect/>"
					+ "            </a:stretch>"
					+ "         </pic:blipFill>"
					+ "         <pic:spPr>"
					+ "            <a:xfrm>"
					+ "               <a:off x=\"0\" y=\"0\"/>"
					+ "               <a:ext cx=\""
					+ width
					+ "\" cy=\""
					+ height
					+ "\"/>"
					+ "            </a:xfrm>"
					+ "            <a:prstGeom prst=\"rect\">"
					+ "               <a:avLst/>"
					+ "            </a:prstGeom>"
					+ "         </pic:spPr>"
					+ "      </pic:pic>"
					+ "   </a:graphicData>" + "</a:graphic>";

			// CTGraphicalObjectData graphicData =
			// inline.addNewGraphic().addNewGraphicData();
			XmlToken xmlToken = null;
			xmlToken = XmlToken.Factory.parse(picXml);
			inline.set(xmlToken);
			// graphicData.set(xmlToken);

			inline.setDistT(0);
			inline.setDistB(0);
			inline.setDistL(0);
			inline.setDistR(0);

			final CTPositiveSize2D extent = inline.addNewExtent();
			extent.setCx(width);
			extent.setCy(height);

			final CTNonVisualDrawingProps docPr = inline.addNewDocPr();
			docPr.setId(id);
			docPr.setName("Picture " + id);
			docPr.setDescr("Generated");
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// close streams
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (final IOException ioEx) {
					// can be ignored
				}
			}
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
				for (int i=0; i< lines.length; i++) {
					String line = lines[i];
					if (i == 0) {
						runCurrent.setText(line, 0);
					} else {
						runCurrent.setText(line);
					}
					if (i+1 < lines.length) {
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
				XWPFDocument dxDocument = processDocument(itsCurrent, bookmarks, tables, null, context, uiDoc);
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
	
	public XWPFDocument processDocument(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks, List<DocumentTable> tables, List<IBookmarkImage> bookmarksImages, FacesContext context, UIDocument uiDocument) throws POIException, XmlException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		InputStream is = itsCurrent.getFileStream();
		XWPFDocument dxDocument = getDocument(is);
		itsCurrent.cleanUP();
		if (bookmarks != null && bookmarks.size() > 0) {
			processBookmarks2Document(dxDocument, bookmarks);
		}
		if (bookmarksImages != null && bookmarksImages.size() > 0) {
			processBookmarks2Image(dxDocument, bookmarksImages);
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

	public ByteArrayOutputStream generateNewByteArrayOutputStream(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks, List<DocumentTable> tables, List<IBookmarkImage> bookmarksImages, FacesContext context, UIDocument uiDoc) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		try {
			int nTemplateAccess = itsCurrent.accessTemplate();
			if (nTemplateAccess == 1) {
				logCurrent.info("Build docx");
				XWPFDocument dxDocument = processDocument(itsCurrent, bookmarks, tables, bookmarksImages, context, uiDoc);
				
				dxDocument.write(bos);
				bos.flush();
				logCurrent.info("Size of docx ByteArrayOutputStream: " + bos.size());

				if (uiDoc.getPdfOutput()) {
					logCurrent.info("Build PDF");
					ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
					bos.close();
					
					IPDFService isCurrent = PDFConversionService.getInstance().getPDFService();
					bos = new ByteArrayOutputStream();
					isCurrent.buildPDF(is, bos);
					is.close();

					bos.flush();
					logCurrent.info("Size of pdf ByteArrayOutputStream: " + bos.size());
				}
			} else {
				throw new Exception("TemplateAccess Problem NR: " + nTemplateAccess, null);
			}
		} catch (Exception e) {
			throw new Exception("Error during Documentgeneration", e);
		}
		return bos;
	}
		
	public int processBookmarksImages2Run(XWPFRun runCurrent, List<IBookmarkImage> arrBookmarks) {
		String strText = runCurrent.getText(0);
		if (strText != null) {
			boolean found = false;
			for (IBookmarkImage bmImageCurrent : arrBookmarks) {
				if (bmImageCurrent.getName() != null && strText.contains("<<" + bmImageCurrent.getName() + ">>")) {
					found = true;
					strText = strText.replace("<<" + bmImageCurrent.getName() + ">>", "");
					addPicture(bmImageCurrent.getFileName(), bmImageCurrent.getFileType(), bmImageCurrent.getExtension(), bmImageCurrent.getId(), bmImageCurrent.getWidth(), bmImageCurrent.getHeight(), runCurrent);
				}
			}
			if (found) {				
				String lines[] = strText.split("\\r?\\n");
				for (int i=0; i< lines.length; i++) {
					String line = lines[i];
					if (i == 0) {
						runCurrent.setText(line, 0);
					} else {
						runCurrent.setText(line);
					}
					if (i+1 < lines.length) {
						runCurrent.addBreak(BreakType.TEXT_WRAPPING);
					}
				}
			}
		}
		return 1;
	}

	
}
