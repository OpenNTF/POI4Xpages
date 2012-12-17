package biz.webgate.dominoext.poi.component.kernel.csv;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.model.DataModel;

import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIDataEx;
import com.ibm.xsp.model.AbstractDataSource;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.model.ModelDataSource;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.ViewRowData;
import com.ibm.xsp.model.domino.DominoViewDataModel;

public class XPagesDataSourceExportProcessor implements IDataSourceProcessor {
	private static XPagesDataSourceExportProcessor m_Processor;

	private XPagesDataSourceExportProcessor() {

	}

	public static XPagesDataSourceExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new XPagesDataSourceExportProcessor();
		}
		return m_Processor;
	}

	public void process(List<CSVColumn> lstColumns, UICSV csvDef,
			CSVPrinter csvPrinter, FacesContext context) throws POIException {
		DataSource ds = csvDef.getPageDataSource();

		if (ds != null) {
			try {
				TabularDataModel tdm = getTDM(ds, context);
				for (int nCount = 0; nCount < tdm.getRowCount(); nCount++) {
					nCount++;
					for (CSVColumn cl : lstColumns) {
						Object objCurrent = getColumnValue(cl.getColumnTitle(),
								tdm, context);
						csvPrinter.print(objCurrent);
					}
					csvPrinter.println();
				}
			} catch (Exception e) {
				throw new POIException("Error in process", e);
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
					tdmv.setDataControl(new UIDataEx());
					tdmv.getRowCount();
					System.out.println(tdmv.getRowCount());
					((com.ibm.xsp.model.domino.viewnavigator.NOIViewNavigatorEx) tdmv
							.getDominoViewDataContainer().getNavigator())
							.calculateExactCount(tdmv.getView());
					System.out.println(tdmv.getRowCount());
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

	private Object getColumnValue(String strColName, TabularDataModel tdm,
			FacesContext context) {
		System.out.println("Check Value 4 " + strColName);
		if (StringUtil.isNotEmpty(strColName)) {
			// Read from a rowData object
			Object rowData = tdm.getRowData();
			if (rowData instanceof ViewRowData) {
				System.out.println("Bin ViewRowData");
				ViewRowData vr = (ViewRowData) rowData;
				System.out.println(vr.getColumnValue(strColName));
				return vr.getColumnValue(strColName);
			}
			System.out.println(rowData.getClass().getCanonicalName());
			// Use the JSF property resolver
			PropertyResolver pr = context.getApplication()
					.getPropertyResolver();
			return pr.getValue(rowData, strColName);
		}

		// Ok no value found
		return null;

	}

}
