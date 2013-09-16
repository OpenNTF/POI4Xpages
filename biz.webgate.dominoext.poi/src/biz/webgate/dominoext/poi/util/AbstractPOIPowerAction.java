package biz.webgate.dominoext.poi.util;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.logging.Logger;

import biz.webgate.dominoext.poi.library.Activator;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public abstract class AbstractPOIPowerAction<T> {

	public T run(final T poiObj, final HashMap<String, String> hsProperties) {
		final Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		try {
			final T wb = AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
				public T run() throws Exception {
					logCurrent.info("Start execution (inkl change of classLoader");
					T wb = setClassLoader(poiObj, hsProperties);
					logCurrent.info("Finished execution. Result object is: " + wb);
					return wb;
				}
			});
			return wb;
		} catch (PrivilegedActionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private T setClassLoader(T objPoi, HashMap<String, String> hsCurrent) {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		Thread currentThread = Thread.currentThread();
		ClassLoader currentCl = currentThread.getContextClassLoader();
		logCurrent.info("Save classloader -> " + currentCl);
		T objRC = null;
		try {
			currentThread.setContextClassLoader(Activator.class.getClassLoader());
			logCurrent.info("Execute innerCode -> doItPriv");
			objRC = doItPriv(objPoi, hsCurrent);
			logCurrent.info("Exeute done!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			currentThread.setContextClassLoader(currentCl);
		}
		return objRC;
	}

	protected abstract T doItPriv(T objPoi, HashMap<String, String> hsCurrent);
}