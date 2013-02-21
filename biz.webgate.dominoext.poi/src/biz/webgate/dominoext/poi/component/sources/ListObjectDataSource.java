/*
 * © Copyright WebGate Consulting AG, 2012
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

package biz.webgate.dominoext.poi.component.sources;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.component.data.IDefinition;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.util.StateHolderUtil;

public class ListObjectDataSource extends ValueBindingObjectImpl implements
		IExportSource {

	private MethodBinding m_BuildValues;

	public MethodBinding getBuildValues() {
		return m_BuildValues;
	}

	public void setBuildValues(MethodBinding buildValues) {
		m_BuildValues = buildValues;
	}

	private List<?> m_Values;
	private List<?> m_ValuesProcess;
	private Iterator<?> m_tempIt;
	private Object m_tempObj;

	public List<?> getValues() throws POIException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());
		if (m_BuildValues != null) {
			logCurrent.info("Exectue BuildValues");
			Object objCurrent = m_BuildValues.invoke(getFacesContext(), null);
			logCurrent.info(objCurrent.getClass().getCanonicalName());
			if (objCurrent instanceof List<?>) {
				return (List<?>) objCurrent;
			} else {
				throw new POIException(
						"buildValues must return a java.util.List Object");
			}
		}
		if (m_Values != null) {
			return m_Values;
		}
		ValueBinding vb = getValueBinding("values");
		if (vb != null) {
			return (List<?>) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setValues(List<?> values) {
		m_Values = values;
	}

	public Object getValue(IDefinition idCurrent, String strVarName,
			String strIndName, int nIndex, FacesContext context) {
		String strVarNameUse = strVarName == null || "".equals(strVarName) ? "exportRow"
				: strVarName;
		String strIndNameUse = strIndName == null || "".equals(strIndName) ? "indexRow"
				: strIndName;

		Logger logCurrent = LoggerFactory.getLogger(this.getClass()
				.getCanonicalName());
		String strCT = idCurrent.getColumnTitle();

		if (strCT != null && !"".equals(strCT)) {
			try {
				Method mt = m_tempObj.getClass().getMethod(
						"get" + idCurrent.getColumnTitle());
				logCurrent.info("ColumnTitle = " + idCurrent.getColumnTitle());
				logCurrent.info("MT =" + mt);
				Object objRC = mt.invoke(m_tempObj);
				logCurrent.info("Result =" + objRC);
				return objRC;
			} catch (Exception e) {
				logCurrent.log(Level.SEVERE, "Error on getValue()", e);
			}
		} else {
			try {
				return idCurrent.executeComputeValue(context, m_tempObj,
						nIndex, strVarNameUse, strIndNameUse);
			} catch (Exception e) {
				logCurrent.log(Level.SEVERE, "Error on getValue()", e);
			}
		}
		return null;
	}

	public int accessNextRow() {
		int nResult = m_tempIt.hasNext() ? 1 : 0;
		if (nResult == 1) {
			m_tempObj = m_tempIt.next();
		}
		return nResult;
	}

	public int accessSource() throws POIException {
		m_ValuesProcess = getValues();
		if (m_ValuesProcess != null) {
			m_tempIt = m_ValuesProcess.iterator();
		}
		return m_ValuesProcess != null ? 1 : 0;
	}

	public int closeSource() {
		m_tempIt = null;
		m_tempObj = null;
		return 1;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = StateHolderUtil.saveList(context, m_Values);
		state[2] = StateHolderUtil.saveMethodBinding(context, m_BuildValues);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_Values = StateHolderUtil.restoreList(context, getComponent(),
				state[1]);
		m_BuildValues = StateHolderUtil.restoreMethodBinding(context,
				getComponent(), state[2]);
	}

}
