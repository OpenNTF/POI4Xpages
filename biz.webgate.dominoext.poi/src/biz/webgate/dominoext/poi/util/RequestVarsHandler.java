package biz.webgate.dominoext.poi.util;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.TypedUtil;

public enum RequestVarsHandler {
	INSTANCE;
	public void pushVars(FacesContext context, String varobject, String varIndex, Object object, int index) {

		String varNameUse = buildVarName(varobject, "exportRow");
		String indNameUse = buildVarName(varIndex, "indexRow");

		Map<String, Object> localMap = TypedUtil.getRequestMap(context.getExternalContext());
		localMap.put(varNameUse, object);
		localMap.put(indNameUse, Integer.valueOf(index));
	}

	public void removeVars(FacesContext context, String varobject, String varIndex) {
		String varNameUse = buildVarName(varobject, "exportRow");
		String indNameUse = buildVarName(varIndex, "indexRow");

		Map<String, Object> localMap = TypedUtil.getRequestMap(context.getExternalContext());
		localMap.remove(varNameUse);
		localMap.remove(indNameUse);
	}

	public List<DataPublisher.ShadowedObject> publishControlData(FacesContext paramFacesContext, String varobject, String varIndex) {
		String varNameUse = buildVarName(varobject, "exportRow");
		String indNameUse = buildVarName(varIndex, "indexRow");

		DataPublisher localDataPublisher = ((FacesContextEx) paramFacesContext).getDataPublisher();
		List<DataPublisher.ShadowedObject> localList = null;
		localList = localDataPublisher.pushShadowObjects(localList, new String[] { varNameUse, indNameUse });
		return localList;
	}

	public void revokeControlData(List<DataPublisher.ShadowedObject> paramList, FacesContext paramFacesContext) {
		DataPublisher localDataPublisher = ((FacesContextEx) paramFacesContext).getDataPublisher();
		localDataPublisher.popObjects(paramList);
	}

	private String buildVarName(String name, String defaultName) {
		return StringUtil.isEmpty(name) ? defaultName : name;
	}
}
