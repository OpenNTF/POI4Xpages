/*
 * © Copyright WebGate Consulting AG, 2013
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
package biz.webgate.dominoext.poi.component.data;

import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class AbstractDefinition extends ValueBindingObjectImpl {

	private MethodBinding m_ComputeValue;

	public MethodBinding getComputeValue() {
		return m_ComputeValue;
	}

	public void setComputeValue(MethodBinding computeValue) {
		m_ComputeValue = computeValue;
	}

	public Object executeComputeValue(FacesContext context, Object objAction,
			int count, String varObject, String varIndex) throws POIException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());

		try {
			logCurrent.info("ExecuteComputeValue started.");
			String [] arrVars = new String[2];
			/*
			ArrayList<String> lstVars = new ArrayList<String>();
			lstVars.add(varObject);
			logCurrent.info("Var Name: " + varObject);
			lstVars.add(varIndex);
			logCurrent.info("Index Name:" + varIndex);
			ArrayList<Object> lstObject = new ArrayList<Object>();
			lstObject.add(objAction);
			lstObject.add(new Integer(count));
			*/
			arrVars[0] = varObject;
			arrVars[1] = varIndex;
			
			Object[] arrObject = new Object[2];
			arrObject[0] = objAction;
			arrObject[1] = new Integer(count);
			logCurrent.info("Var Name: " + varObject);
			logCurrent.info("Index Name:" + varIndex);
			logCurrent.info("Var Object: " + varObject);
			logCurrent.info("Index Object: " + count);
			if (m_ComputeValue != null) {
				if (m_ComputeValue instanceof MethodBindingEx) {
					((MethodBindingEx) m_ComputeValue)
							.setComponent(getComponent());
					((MethodBindingEx) m_ComputeValue).setParamNames(arrVars);
				}
				Object objRC = m_ComputeValue.invoke(context,
						arrObject);
				return objRC;
			}
		} catch (Exception e) {
			throw new POIException("Error during computeValue:" +e.getMessage(), e);
		}
		return null;
	}

}
