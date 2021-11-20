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
package biz.webgate.dominoext.poi.component.kernel.util;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import com.ibm.xsp.binding.MethodBindingEx;

public enum MethodExecutor {
	INSTANCE;

	public void execute(MethodBinding method, UIComponent comp, FacesContext context, Object bo) throws PrivilegedActionException {
		if (method != null) {
			Object[] params = null;
			if (method instanceof MethodBindingEx) {
				params = new Object[] { bo };
				((MethodBindingEx) method).setComponent(comp);
				((MethodBindingEx) method).setParamNames(new String[] { "result" });
			}

			executePrivileged(method, context, params);
		}
	}

	private void executePrivileged(final MethodBinding method, final FacesContext context, final Object[] params) throws PrivilegedActionException {
		AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
			method.invoke(context, params);
			return null;
		});
	}
}
