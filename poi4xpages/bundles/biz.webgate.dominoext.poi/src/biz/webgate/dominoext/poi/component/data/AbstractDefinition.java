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
package biz.webgate.dominoext.poi.component.data;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.util.DataPublisher;
import com.ibm.xsp.util.TypedUtil;

import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public class AbstractDefinition extends ValueBindingObjectImpl {

	private MethodBinding m_ComputeValue;

	public MethodBinding getComputeValue() {
		return m_ComputeValue;
	}

	public void setComputeValue(MethodBinding computeValue) {
		m_ComputeValue = computeValue;
	}

	public Object executeComputeValue(FacesContext context) throws POIException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		try {
			logCurrent.info("ExecuteComputeValue started.");
			Object[] arrObject = new Object[0];
			if (m_ComputeValue != null) {
				if (m_ComputeValue instanceof MethodBindingEx) {
					((MethodBindingEx) m_ComputeValue).setComponent(getComponent());
				}
				Object objRC = m_ComputeValue.invoke(context, arrObject);
				return objRC;
			}
		} catch (Exception e) {
			throw new POIException("Error during computeValue:" + e.getMessage(), e);
		}
		return null;
	}

	public void pushVars(FacesContext context, String varobject, String varIndex, Object object, int index) {

		Map<String, Object> localMap = TypedUtil.getRequestMap(context.getExternalContext());
		localMap.put(varobject, object);
		localMap.put(varIndex, Integer.valueOf(index));
	}

	public void removeVars(FacesContext context, String varobject, String varIndex) {
		Map<String, Object> localMap = TypedUtil.getRequestMap(context.getExternalContext());
		localMap.remove(varobject);
		localMap.remove(varIndex);
	}

	protected List<DataPublisher.ShadowedObject> publishControlData(FacesContext paramFacesContext, String varobject, String varIndex) {
		DataPublisher localDataPublisher = ((FacesContextEx) paramFacesContext).getDataPublisher();
		List<DataPublisher.ShadowedObject> localList = null;
		localList = localDataPublisher.pushShadowObjects(localList, new String[] { varobject, varIndex });
		return localList;
	}

	protected void revokeControlData(List<DataPublisher.ShadowedObject> paramList, FacesContext paramFacesContext) {
		DataPublisher localDataPublisher = ((FacesContextEx) paramFacesContext).getDataPublisher();
		localDataPublisher.popObjects(paramList);
	}

}
