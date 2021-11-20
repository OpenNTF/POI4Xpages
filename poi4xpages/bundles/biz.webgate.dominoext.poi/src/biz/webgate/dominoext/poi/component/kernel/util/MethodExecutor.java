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
