package biz.webgate.dominoext.poi.component.kernel.document;

import javax.faces.context.FacesContext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;


public interface IDataSourceExportProcessor {
	XWPFTable processExportTable(DocumentTable lstExport, XWPFDocument dxDocument, FacesContext context, String strVar, String strIndex) throws POIException;
}