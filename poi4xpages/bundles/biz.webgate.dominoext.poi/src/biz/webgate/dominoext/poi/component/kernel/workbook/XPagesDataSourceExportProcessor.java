package biz.webgate.dominoext.poi.component.kernel.workbook;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.model.DataModel;

import org.apache.poi.ss.usermodel.Sheet;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIDataEx;
import com.ibm.xsp.model.AbstractDataSource;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.model.ModelDataSource;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.model.domino.ViewNavigatorEx;
import com.ibm.xsp.model.domino.viewnavigator.NOIViewNavigatorEx9;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;

import biz.webgate.dominoext.poi.component.data.ss.Data2ColumnExporter;
import biz.webgate.dominoext.poi.component.data.ss.Data2RowExporter;
import biz.webgate.dominoext.poi.component.data.ss.cell.ColumnDefinition;
import biz.webgate.dominoext.poi.component.data.ss.cell.RowDefinition;
import biz.webgate.dominoext.poi.component.kernel.WorkbookProcessor;
import biz.webgate.dominoext.poi.util.RequestVarsHandler;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;

public class XPagesDataSourceExportProcessor implements IDataSourceExportProcessor {

	private static XPagesDataSourceExportProcessor m_Processor;

	private XPagesDataSourceExportProcessor() {

	}

	public static XPagesDataSourceExportProcessor getInstances() {
		if (m_Processor == null) {
			m_Processor = new XPagesDataSourceExportProcessor();
		}
		return m_Processor;
	}

	@Override
	public void processExportRow(Data2RowExporter lstExport, Sheet shProcess, FacesContext context, String strVar, String strIndex) throws POIException {

		DataSource ds = lstExport.getPageDataSource();

		if (ds != null) {
			try {
				TabularDataModel tdm = getTDM(ds, context);
				int nRow = lstExport.getStartRow();
				int nStepSize = lstExport.getStepSize();
				int nDSRCount = tdm.getRowCount();
				List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, strVar, strIndex);
				for (int nCount = 0; nCount < nDSRCount; nCount++) {
					tdm.setRowIndex(nCount);
					if (tdm.isRowAvailable()) {
						RequestVarsHandler.INSTANCE.pushVars(context, strVar, strIndex, tdm.getRowData(), nCount);
						for (ColumnDefinition clDef : lstExport.getColumns()) {
							int nCol = clDef.getColumnNumber();
							int nMyRow = clDef.getRowShift() + nRow;
							String strTitle = clDef.getColumnTitle();
							Object objCurrent = null;
							if (strTitle != null && !"".equals(strTitle)) {
								objCurrent = getColumnValue(clDef.getColumnTitle(), tdm, context);
							} else {
								objCurrent = clDef.executeComputeValue(context);
							}
							WorkbookProcessor.INSTANCE.setCellValue(shProcess, nMyRow, nCol, objCurrent, clDef.isCellFormula(), clDef.getPoiCellStyle());
						}
						RequestVarsHandler.INSTANCE.removeVars(context, strVar, strIndex);
						nRow = nRow + nStepSize;
					} else {
						System.out.println("no Row available at " + nCount);
					}
				}
				RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);

			} catch (Exception e) {
				throw new POIException("Error in processExportRow", e);
			}
		} else {
			throw new POIException("No DataSource found", null);
		}
	}

	@Override
	public void processExportCol(Data2ColumnExporter lstExport, Sheet shProcess, FacesContext context, String strVar, String strIndex) throws POIException {
		DataSource ds = lstExport.getPageDataSource();

		if (ds != null) {
			try {
				TabularDataModel tdm = getTDM(ds, context);
				int nCol = lstExport.getStartColumn();
				int nStepSize = lstExport.getStepSize();
				int nDSMRRowCount = tdm.getRowCount();
				List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, strVar, strIndex);
				for (int nCount = 0; nCount < nDSMRRowCount; nCount++) {
					tdm.setRowIndex(nCount);
					if (tdm.isRowAvailable()) {
						RequestVarsHandler.INSTANCE.pushVars(context, strVar, strIndex, tdm.getRowData(), nCount);
						for (RowDefinition rdDef : lstExport.getRows()) {
							int nMyCol = rdDef.getColumnShift() + nCol;
							int nRow = rdDef.getRowNumber();
							String strTitle = rdDef.getColumnTitle();
							Object objCurrent = null;
							if (strTitle != null && !"".equals(strTitle)) {
								objCurrent = getColumnValue(rdDef.getColumnTitle(), tdm, context);
							} else {
								objCurrent = rdDef.executeComputeValue(context);
							}
							WorkbookProcessor.INSTANCE.setCellValue(shProcess, nRow, nMyCol, objCurrent, rdDef.isCellFormula(), rdDef.getPoiCellStyle());
						}
						RequestVarsHandler.INSTANCE.removeVars(context, strVar, strIndex);
						nCol = nCol + nStepSize;
					}
				}
				RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);
			} catch (Exception e) {
				throw new POIException("Error in processExportCol", e);
			}
		} else {
			throw new POIException("No DataSource found", null);
		}
	}

	private TabularDataModel getTDM(DataSource dsCurrent, FacesContext context) {
		try {
			if (dsCurrent instanceof ModelDataSource) {
				ModelDataSource mds = (ModelDataSource) dsCurrent;
				AbstractDataSource ads = (AbstractDataSource) mds;
				ads.load(context);
				if (ads.getBeanId() == null) {

				}
				DataModel tdm = mds.getDataModel();
				if (tdm instanceof DominoViewDataModel) {
					DominoViewDataModel tdmv = (DominoViewDataModel) tdm;
					// tdmv.setDataControl(new DummyDataIterator());
					UIDataEx uidEX = new UIDataEx();
					tdmv.setDataControl(uidEX);
					tdmv.getRowCount();
					ViewNavigatorEx nav = tdmv.getDominoViewDataContainer().getNavigator();
					if (nav instanceof NOIViewNavigatorEx9) {
						((com.ibm.xsp.model.domino.viewnavigator.NOIViewNavigatorEx9) nav).calculateExactCount(tdmv.getView());
					} else {
						((com.ibm.xsp.model.domino.viewnavigator.NOIViewNavigatorEx) nav).calculateExactCount(tdmv.getView());
					}
					uidEX.setRows(tdmv.getRowCount());
					return tdmv;
				}
				if (tdm instanceof TabularDataModel) {
					TabularDataModel tds = (TabularDataModel) tdm;
					return tds;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Object getColumnValue(String strColName, TabularDataModel tdm, FacesContext context) {
		if (StringUtil.isNotEmpty(strColName)) {
			// Read from a rowData object
			Object rowData = tdm.getRowData();
			if (rowData instanceof ViewRowData) {
				ViewRowData vr = (ViewRowData) rowData;
				return vr.getColumnValue(strColName);
			}
			// Use the JSF property resolver
			PropertyResolver pr = context.getApplication().getPropertyResolver();
			return pr.getValue(rowData, strColName);
		}

		// Ok no value found
		return null;

	}

}
