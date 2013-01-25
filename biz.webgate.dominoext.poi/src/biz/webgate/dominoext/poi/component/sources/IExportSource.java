package biz.webgate.dominoext.poi.component.sources;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.data.IDefinition;

public interface IExportSource {

	public Object getValue(IDefinition idCurrent);

	public int accessNextRow();

	public int accessSource() throws POIException;

	public int closeSource();
}
