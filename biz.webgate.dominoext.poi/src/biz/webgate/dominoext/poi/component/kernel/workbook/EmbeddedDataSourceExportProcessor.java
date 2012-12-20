package biz.webgate.dominoext.poi.component.kernel.workbook;

import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;

import org.apache.poi.ss.usermodel.Sheet;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.ViewRowData;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;
import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;
import biz.webgate.dominoext.poi.component.sources.IExportSource;

public class EmbeddedDataSourceExportProcessor implements
		IDataSourceExportProcessor {

	private static EmbeddedDataSourceExportProcessor m_Processor;

	private EmbeddedDataSourceExportProcessor() {
	}

	public static EmbeddedDataSourceExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new EmbeddedDataSourceExportProcessor();
		}
		return m_Processor;
	}

	public void processExportRow(Data2RowExporter lstExport, Sheet shProcess,
			FacesContext context) throws POIException {
		try {
			IExportSource is = lstExport.getDataSource();
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: "
						+ is.getClass() + " Error: " + nAccess);
			}
			int nRow = lstExport.getStartRow();
			int nStepSize = lstExport.getStepSize();

			int nCount = 0;
			while (is.accessNextRow() == 1) {
				nCount++;
				// TODO: Variablen setzen
				for (ColumnDefinition clDef : lstExport.getColumns()) {
					int nCol = clDef.getColumnNumber();
					int nMyRow = clDef.getRowShift() + nRow;
					Object objCurrent = is.getValue(clDef);
					WorkbookProcessor.setCellValue(shProcess,
							nMyRow, nCol, objCurrent);
				}
				nRow = nRow + nStepSize;
			}
			is.closeSource();
		} catch (Exception e) {
			throw new POIException("Error in processExportRow", e);
		}
	}

	public void processExportCol(Data2ColumnExporter lstExport,
			Sheet shProcess, FacesContext context) throws POIException {
		IExportSource is = lstExport.getDataSource();
		int nAccess = is.accessSource();
		if (nAccess < 1) {
			throw new POIException("Error accessing Source: " + is.getClass()
					+ " Error: " + nAccess);
		}
		try {
			int nCol = lstExport.getStartColumn();
			int nStepSize = lstExport.getStepSize();
			int nCount = 0;
			while (is.accessNextRow() == 1) {
				nCount++;
				for (RowDefinition rdDef : lstExport.getRows()) {
					int nMyCol = rdDef.getColumnShift() + nCol;
					int nRow = rdDef.getRowNumber();
					Object objCurrent = is.getValue(rdDef);
					WorkbookProcessor.setCellValue(shProcess,
							nRow, nMyCol, objCurrent);
				}
				nCol = nCol + nStepSize;
			}
			is.closeSource();
		} catch (Exception e) {
			throw new POIException("Error in processExportCol", e);
		}
	}

}
