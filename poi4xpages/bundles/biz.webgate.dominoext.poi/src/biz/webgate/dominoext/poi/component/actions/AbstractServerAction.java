/**
 * Copyright (c) 2012-2021 WebGate Consulting AG and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.webgate.dominoext.poi.component.actions;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import com.ibm.xsp.binding.MethodBindingEx;
import com.ibm.xsp.util.StateHolderUtil;

public abstract class AbstractServerAction extends MethodBindingEx {

	private boolean noDownload;
	private MethodBinding preDownload;

	public AbstractServerAction() {
		super();
	}

	public boolean isNoDownload() {
		return noDownload;
	}

	public void setNoDownload(boolean noDownload) {
		this.noDownload = noDownload;
	}

	public MethodBinding getPreDownload() {
		return preDownload;
	}

	public void setPreDownload(MethodBinding download) {
		this.preDownload = download;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = noDownload;
		state[2] = StateHolderUtil.saveMethodBinding(context, preDownload);
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
		noDownload = (Boolean) values[1];
		preDownload = StateHolderUtil.restoreMethodBinding(context, getComponent(), values[2]);
	}
}