package biz.webgate.dominoext.poi.component.kernel.workbook;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;

import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;

public interface IDataSourceExportProcessor {
	void processExportRow(Data2RowExporter lstExport, Sheet shProcess,
			FacesContext context, String strVar, String strIndex) throws POIException;

	void processExportCol(Data2ColumnExporter lstExport, Sheet shProcess,
			FacesContext context, String strVar, String strIndex) throws POIException;
}
