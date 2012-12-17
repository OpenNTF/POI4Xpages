package biz.webgate.dominoext.poi.component.kernel.csv;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;

public interface IDataSourceProcessor {

	public void process(List<CSVColumn> lstColumns, UICSV csvDef,
			CSVPrinter csvPrinter, FacesContext context) throws POIException;
}
