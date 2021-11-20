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

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import biz.webgate.dominoext.poi.util.DatabaseProvider;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public abstract class AbstractTemplateSource extends ValueBindingObjectImpl {

	private String m_databaseName;

	public AbstractTemplateSource() {
		super();
	}

	public String getDatabaseName() {
		if (m_databaseName != null) {
			return m_databaseName;
		}
		ValueBinding vb = getValueBinding("databaseName");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setDatabaseName(String databaseName) {
		m_databaseName = databaseName;
	}

	public Database getSourceDatabase(boolean asSigner) {
		return DatabaseProvider.INSTANCE.getDatabase(getDatabaseName(), asSigner);
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_databaseName = (String) state[1];

	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[2];
		state[0] = super.saveState(context);
		state[1] = m_databaseName;
		return state;

	}

}