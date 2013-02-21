package biz.webgate.dominoext.poi.component.data;

import javax.faces.context.FacesContext;

import biz.webgate.dominoext.poi.utils.exceptions.POIException;

public interface IDefinition {

	public String getColumnTitle();
	public Object executeComputeValue(FacesContext context, Object objAction,
			int count, String varObject, String varIndex) throws POIException;
}
