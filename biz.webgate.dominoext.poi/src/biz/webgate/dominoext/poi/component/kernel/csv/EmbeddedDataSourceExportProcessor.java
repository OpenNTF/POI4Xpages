package biz.webgate.dominoext.poi.component.kernel.csv;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.component.sources.IExportSource;

public class EmbeddedDataSourceExportProcessor implements IDataSourceProcessor {

	private static EmbeddedDataSourceExportProcessor m_Processor;
	private EmbeddedDataSourceExportProcessor() {
		
	}
	public static EmbeddedDataSourceExportProcessor getInstance() {
		if (m_Processor == null){
			m_Processor = new EmbeddedDataSourceExportProcessor();
		}
		return m_Processor;
	}
	
	public void process(List<CSVColumn> lstColumns, UICSV csvDef,
			CSVPrinter csvPrinter, FacesContext context) throws POIException {
		try {
			IExportSource is = csvDef.getDataSource();
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: "
						+ is.getClass() + " Error: " + nAccess);
			}
			int nCount = 0;
			while (is.accessNextRow() == 1) {
				nCount++;
				// TODO: Variablen setzen
				for (CSVColumn cl : lstColumns) {
					Object objCurrent = is.getValue(cl);
					csvPrinter.print(objCurrent);
				}
				csvPrinter.println();
			}
			is.closeSource();
		} catch (Exception e) {
			throw new POIException("Error in processExportRow", e);
		}


	}

}
