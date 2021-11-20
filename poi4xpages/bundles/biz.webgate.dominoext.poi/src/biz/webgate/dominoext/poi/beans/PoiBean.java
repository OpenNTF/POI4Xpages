package biz.webgate.dominoext.poi.beans;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import biz.webgate.dominoext.poi.component.data.AbstractTemplateSource;
import biz.webgate.dominoext.poi.component.data.AttachmentTemplateSource;
import biz.webgate.dominoext.poi.component.data.ITemplateSource;
import biz.webgate.dominoext.poi.component.data.ResourceTemplateSource;
import biz.webgate.dominoext.poi.component.data.document.DocumentBookmark;
import biz.webgate.dominoext.poi.component.data.document.IDocumentBookmark;
import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.IListDataExporter;
import biz.webgate.dominoext.poi.component.data.ss.Spreadsheet;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellBookmark;
import biz.webgate.dominoext.poi.component.data.ss.cell.CellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;
import biz.webgate.dominoext.poi.component.data.ss.cell.ICellValue;
import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;
import biz.webgate.dominoext.poi.component.kernel.DocumentProcessor;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;
import biz.webgate.dominoext.poi.component.sources.DominoView;
import biz.webgate.dominoext.poi.component.sources.IExportSource;
import biz.webgate.dominoext.poi.component.sources.ListObjectDataSource;
import biz.webgate.dominoext.poi.util.POILibUtil;
import biz.webgate.dominoext.poi.util.action.DocX2HTMLAction;
import biz.webgate.dominoext.poi.util.action.DocX2PDFAction;

public class PoiBean {

	private static final DocX2HTMLAction DOC_X2HTML_ACTION = new DocX2HTMLAction();
	private static final DocX2PDFAction DOC_X2PDF_ACTION = new DocX2PDFAction();
	public static final String BEAN_NAME = "poiBean"; //$NON-NLS-1$

	public static PoiBean get(FacesContext context) {
		return (PoiBean) context.getApplication().getVariableResolver().resolveVariable(context, BEAN_NAME);
	}

	public static PoiBean get() {
		return get(FacesContext.getCurrentInstance());
	}

	public String getLibVersion() {
		return POILibUtil.getPOILibUtilVersion();
	}

	public XWPFDocument processDocument(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks) {
		return processDocument(itsCurrent, bookmarks, null);
	}

	public XWPFDocument processDocument(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks, List<DocumentTable> tables) {
		final ITemplateSource itsCurrentFin = itsCurrent;
		final List<IDocumentBookmark> bookmarksFin = bookmarks;
		final List<DocumentTable> tablesFin = tables;
		if (itsCurrent.accessTemplate() == 1) {
			return AccessController.doPrivileged((PrivilegedAction<XWPFDocument>) () -> {
				try {
					return DocumentProcessor.INSTANCE.processDocument(itsCurrentFin, bookmarksFin, tablesFin, FacesContext.getCurrentInstance(), null);
				} catch (Exception e) {
					throw new FacesException("poiBean. processDocument", e);
				}
			});
		}
		return null;
	}

	public ByteArrayOutputStream processDocument2Stream(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks) {
		return processDocument2Stream(itsCurrent, bookmarks, null);
	}

	public ByteArrayOutputStream processDocument2Stream(ITemplateSource itsCurrent, List<IDocumentBookmark> bookmarks, List<DocumentTable> tables) {
		final XWPFDocument doc = processDocument(itsCurrent, bookmarks, tables);
		if (doc == null) {
			throw new NullPointerException("The result of processDocument is null");
		}
		return AccessController.doPrivileged((PrivilegedAction<ByteArrayOutputStream>) () -> {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				doc.write(bos);
				return bos;
			} catch (Exception e) {
				throw new FacesException("poiBean:processDocument2Stream", e);
			}
		});
	}

	public IDocumentBookmark buildDocumentBookmark(String strName, String strValue) {
		DocumentBookmark bmRC = new DocumentBookmark();
		bmRC.setName(strName);
		bmRC.setValue(strValue);
		return bmRC;
	}

	public AbstractTemplateSource buildAttachmentTemplateSource(String strDB, String strView, String strKey, String strField) {
		AttachmentTemplateSource attRC = new AttachmentTemplateSource();
		attRC.setDatabaseName(strDB);
		attRC.setViewName(strView);
		attRC.setKeyName(strKey);
		attRC.setFieldName(strField);
		return attRC;
	}

	public ResourceTemplateSource buildResourceTemplateSource(String strDB, String strFileName) {
		ResourceTemplateSource rtsRC = new ResourceTemplateSource();
		rtsRC.setDatabaseName(strDB);
		rtsRC.setFileName(strFileName);
		return rtsRC;
	}

	public Workbook processWorkbook(ITemplateSource itsCurrent, List<Spreadsheet> lstSP) {
		if (itsCurrent.accessTemplate() == 1) {
			final ITemplateSource itsCurrentFIN = itsCurrent;
			final List<Spreadsheet> lstSPFIN = lstSP;
			return AccessController.doPrivileged((PrivilegedAction<Workbook>) () -> {
				try {
					return WorkbookProcessor.INSTANCE.processWorkbook(itsCurrentFIN, lstSPFIN, FacesContext.getCurrentInstance(), null);
				} catch (Exception e) {
					throw new FacesException("poiBean:processWorkbook", e);
				}
			});
		}
		return null;
	}

	@Deprecated
	public ByteArrayOutputStream processWorkbook2Stream(ITemplateSource itsCurrent, List<Spreadsheet> lstSP, FacesContext context) {
		return processWorkbook2Stream(itsCurrent, lstSP);
	}

	public ByteArrayOutputStream processWorkbook2Stream(ITemplateSource itsCurrent, List<Spreadsheet> lstSP) {
		final Workbook wb = processWorkbook(itsCurrent, lstSP);
		if (wb == null) {
			throw new NullPointerException("the result of processWorkbook is null");
		}
		return AccessController.doPrivileged((PrivilegedAction<ByteArrayOutputStream>) () -> {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				wb.write(bos);
				return bos;
			} catch (Exception e) {
				throw new FacesException("poiBean:processWorkbook2Stream", e);
			}
		});
	}

	public CellBookmark buildCellBookmark(String strName, Object strValue) {
		CellBookmark cbRC = new CellBookmark();
		cbRC.setName(strName);
		cbRC.setValue(strValue);
		return cbRC;
	}

	public CellValue buildCellValue(int nCol, int nRow, Object objValue) {
		CellValue cbRC = new CellValue();
		cbRC.setColumnNumber(nCol);
		cbRC.setRowNumber(nRow);
		cbRC.setValue(objValue);
		return cbRC;
	}

	public DominoView buildDominoViewSource(String strDB, String strView, String strKey, String strSearch) {
		DominoView dvRC = new DominoView();
		dvRC.setDatabase(strDB);
		dvRC.setViewName(strView);
		dvRC.setKey(strKey);
		dvRC.setSearch(strSearch);
		return dvRC;
	}

	public ListObjectDataSource buildListObjectDataSource(List<?> lstValues) {
		ListObjectDataSource lodsRC = new ListObjectDataSource();
		lodsRC.setValues(lstValues);
		return lodsRC;
	}

	public ColumnDefinition buildColumnDefinition(int nCol, int nShift, String strColumnTitle) {
		ColumnDefinition cdRC = new ColumnDefinition();
		cdRC.setColumnNumber(nCol);
		cdRC.setRowShift(nShift);
		cdRC.setColumnTitle(strColumnTitle);
		return cdRC;
	}

	public RowDefinition buildRowDefinition(int nRow, int nShift, String strColumnTitle) {
		RowDefinition cdRC = new RowDefinition();
		cdRC.setRowNumber(nRow);
		cdRC.setColumnShift(nShift);
		cdRC.setColumnTitle(strColumnTitle);
		return cdRC;
	}

	public Data2ColumnExporter buildData2ColumnExporter(IExportSource dataSource, String dataSourceVar, List<RowDefinition> rows, int startColumn, int stepSize) {
		Data2ColumnExporter d2cRC = new Data2ColumnExporter();
		d2cRC.setDataSource(dataSource);
		d2cRC.setDataSourceVar(dataSourceVar);
		d2cRC.setRows(rows);
		d2cRC.setStartColumn(startColumn);
		d2cRC.setStepSize(stepSize);
		return d2cRC;
	}

	public Data2RowExporter buildData2RowExporter(IExportSource dataSource, String dataSourceVar, List<ColumnDefinition> cols, int startRow, int stepSize) {
		Data2RowExporter d2cRC = new Data2RowExporter();
		d2cRC.setDataSource(dataSource);
		d2cRC.setDataSourceVar(dataSourceVar);
		d2cRC.setColumns(cols);
		d2cRC.setStartRow(startRow);
		d2cRC.setStepSize(stepSize);
		return d2cRC;
	}

	public Spreadsheet buildSpreadSheet(String strName, boolean blCreate, List<ICellValue> cellValues, List<IListDataExporter> exportDefinition) {
		Spreadsheet spRC = new Spreadsheet();
		spRC.setName(strName);
		spRC.setCreate(blCreate);
		spRC.setCellValues(cellValues);
		spRC.setExportDefinitions(exportDefinition);
		return spRC;
	}

	public OutputStream buildHTMLFromDocX(InputStream is) {
		return DOC_X2HTML_ACTION.run(is, null, null);
	}
	public OutputStream buildHTMLFromDocX(InputStream is, OutputStream out) {
		return DOC_X2HTML_ACTION.run(is, out, null);
	}

	public OutputStream buildPDFFromDocX(InputStream is) {
		return DOC_X2PDF_ACTION.run(is, null, null);
	}
	public OutputStream buildPDFFromDocX(InputStream is, OutputStream out) {
		return DOC_X2PDF_ACTION.run(is, out, null);
	}

}
