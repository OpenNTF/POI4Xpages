package biz.webgate.dominoext.poi.util;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class ValueBindingSupport {

	public static String getVBString(String strValueName,
			ValueBindingObjectImpl vbobj, FacesContext context) {
		ValueBinding vb = vbobj.getValueBinding(strValueName);
		if (vb != null) {
			return (String) vb.getValue(context);
		}
		return null;
	}

	public static Integer getVBInt(String strValueName,
			ValueBindingObjectImpl vbobj, FacesContext context) {
		ValueBinding vb = vbobj.getValueBinding(strValueName);
		if (vb != null) {
			return (Integer) vb.getValue(context);
		}
		return null;
	}

}
