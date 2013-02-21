/*
 * � Copyright WebGate Consulting AG, 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package biz.webgate.dominoext.poi.utils.xsp;

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
