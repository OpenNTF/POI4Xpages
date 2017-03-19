/*
 * � Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi.component.actions;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.util.FacesUtil;

import biz.webgate.dominoext.poi.component.containers.UIDocument;

public class DocumentGenerationServerAction extends AbstractServerAction {

	private String m_DocumentId;

	@SuppressWarnings("rawtypes")
	@Override
	public Class getType(FacesContext arg0) throws MethodNotFoundException {
		return String.class;
	}

	@Override
	public Object invoke(FacesContext context, Object[] arg1)
			throws EvaluationException, MethodNotFoundException {
		String strID = getDocumentId();
		UIComponent uiC = FacesUtil.getComponentFor(context.getViewRoot(),
				strID);
		if (uiC instanceof UIDocument) {
			UIDocument uiD = (UIDocument) uiC;
			try {
				uiD.processAjaxRequest(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getDocumentId() {
		if (null != m_DocumentId) {
			return m_DocumentId;
		}
		ValueBinding _vb = getValueBinding("documentId"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext
					.getCurrentInstance());
		} else {
			return null;
		}

	}

	public void setDocumentId(String documentId) {
		m_DocumentId = documentId;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[2];
		state[0] = super.saveState(context);
		state[1] = m_DocumentId;
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.
	 * FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] values = (Object[]) value;
		super.restoreState(context, values[0]);
		m_DocumentId = (String) values[1];
	}

}
