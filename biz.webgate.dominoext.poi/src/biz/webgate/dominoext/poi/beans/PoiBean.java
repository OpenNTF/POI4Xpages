package biz.webgate.dominoext.poi.beans;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.kernel.DocumentProcessor;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;

public class PoiBean {

	public static final String BEAN_NAME = "poiBean"; //$NON-NLS-1$

	public static PoiBean get(FacesContext context) {
		PoiBean bean = (PoiBean) context.getApplication().getVariableResolver()
				.resolveVariable(context, BEAN_NAME);
		return bean;
	}

	public static PoiBean get() {
		return get(FacesContext.getCurrentInstance());
	}

	public XWPFDocument processDocument(ITemplateSource itsCurrent,
			List<IDocumentBookmark> bookmarks) {
		if (itsCurrent.accessTemplate() == 1) {
			return DocumentProcessor.getInstance().processDocument(itsCurrent,
					bookmarks);
		}
		return null;
	}

	public ByteArrayOutputStream processDocument2Stream(
			ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks) {
		XWPFDocument doc = processDocument(itsCurrent, bookmarks);
		if (doc != null) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				doc.write(bos);
				return bos;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Workbook processWorkbook(ITemplateSource itsCurrent,
			List<Spreadsheet> lstSP) {
		if (itsCurrent.accessTemplate() == 1) {
			try {
				return WorkbookProcessor.getInstance().processWorkbook(
						itsCurrent, lstSP, FacesContext.getCurrentInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ByteArrayOutputStream processWorkbook2Stream(
			ITemplateSource itsCurrent, List<Spreadsheet> lstSP,
			FacesContext context) {
		Workbook wb = processWorkbook(itsCurrent, lstSP);
		if (wb != null) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				wb.write(bos);
				return bos;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
