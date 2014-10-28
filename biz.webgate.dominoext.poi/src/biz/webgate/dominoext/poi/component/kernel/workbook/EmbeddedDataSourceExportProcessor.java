package biz.webgate.dominoext.poi.component.kernel.workbook;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Sheet;

import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;
import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;
import biz.webgate.dominoext.poi.component.sources.IExportSource;
import biz.webgate.dominoext.poi.util.RequestVarsHandler;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.xsp.util.DataPublisher.ShadowedObject;

public class EmbeddedDataSourceExportProcessor implements IDataSourceExportProcessor {

	private static EmbeddedDataSourceExportProcessor m_Processor;

	private EmbeddedDataSourceExportProcessor() {
	}

	public static EmbeddedDataSourceExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new EmbeddedDataSourceExportProcessor();
		}
		return m_Processor;
	}

	public void processExportRow(Data2RowExporter lstExport, Sheet shProcess, FacesContext context, String strVar, String strIndex) throws POIException {

		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		try {
			logCurrent.finer("Proccess Export Row");
			IExportSource is = lstExport.getDataSource();

			logCurrent.finer("Load Access Source");
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: " + is.getClass() + " Error: " + nAccess);
			}
			int nRow = lstExport.getStartRow();
			int nStepSize = lstExport.getStepSize();

			int nCount = 0;

			logCurrent.finer("Start Processing Cells");
			List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, strVar, strIndex);
			while (is.accessNextRow() == 1) {
				nCount++;
				RequestVarsHandler.INSTANCE.pushVars(context, strVar, strIndex, is.getDataRow(), nCount);
				for (ColumnDefinition clDef : lstExport.getColumns()) {
					int nCol = clDef.getColumnNumber();
					int nMyRow = clDef.getRowShift() + nRow;
					Object objCurrent = is.getValue(clDef, context);
					WorkbookProcessor.INSTANCE.setCellValue(shProcess, nMyRow, nCol, objCurrent, clDef.isCellFormula(), clDef.getPoiCellStyle());
				}
				RequestVarsHandler.INSTANCE.removeVars(context, strVar, strIndex);
				nRow = nRow + nStepSize;
			}
			RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);
			is.closeSource();
			logCurrent.finer("Proccess Export Row - DONE");
		} catch (Exception e) {
			throw new POIException("Error in processExportRow", e);
		}
	}

	public void processExportCol(Data2ColumnExporter lstExport, Sheet shProcess, FacesContext context, String strVar, String strIndex) throws POIException {
		IExportSource is = lstExport.getDataSource();
		int nAccess = is.accessSource();
		if (nAccess < 1) {
			throw new POIException("Error accessing Source: " + is.getClass() + " Error: " + nAccess);
		}
		try {
			int nCol = lstExport.getStartColumn();
			int nStepSize = lstExport.getStepSize();
			int nCount = 0;
			List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, strVar, strIndex);
			while (is.accessNextRow() == 1) {
				nCount++;
				RequestVarsHandler.INSTANCE.pushVars(context, strVar, strIndex, is.getDataRow(), nCount);
				for (RowDefinition rdDef : lstExport.getRows()) {
					int nMyCol = rdDef.getColumnShift() + nCol;
					int nRow = rdDef.getRowNumber();
					Object objCurrent = is.getValue(rdDef, context);
					WorkbookProcessor.INSTANCE.setCellValue(shProcess, nRow, nMyCol, objCurrent, rdDef.isCellFormula(), rdDef.getPoiCellStyle());
				}
				RequestVarsHandler.INSTANCE.removeVars(context, strVar, strIndex);
				nCol = nCol + nStepSize;
			}
			RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);
			is.closeSource();
		} catch (Exception e) {
			throw new POIException("Error in processExportCol", e);
		}
	}

}
