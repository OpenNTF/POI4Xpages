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
package biz.webgate.dominoext.poi.util;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.logging.Logger;

import biz.webgate.dominoext.poi.library.Activator;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public abstract class AbstractPOIPowerActionExtended<T, I> {
	private boolean m_Error = false;
	private Exception m_LastException;

	public T run(final I inputObj, final T poiObj, final HashMap<String, String> hsProperties) {
		m_Error = false;
		m_LastException = null;
		final Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		try {
			final T wb = AccessController.doPrivileged((PrivilegedExceptionAction<T>) () -> {
				logCurrent.info("Start execution (inkl change of classLoader");
				T wb1 = setClassLoader(inputObj, poiObj, hsProperties);
				logCurrent.info("Finished execution. Result object is: " + wb1);
				return wb1;
			});
			return wb;
		} catch (PrivilegedActionException e) {
			m_Error = true;
			m_LastException = e;
			e.printStackTrace();
		}
		return null;
	}

	private T setClassLoader(I inputObj, T objPoi, HashMap<String, String> hsCurrent) {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		Thread currentThread = Thread.currentThread();
		ClassLoader currentCl = currentThread.getContextClassLoader();
		logCurrent.info("Save classloader -> " + currentCl);
		T objRC = null;
		try {
			currentThread.setContextClassLoader(Activator.class.getClassLoader());
			logCurrent.info("Execute innerCode -> doItPriv");
			objRC = doItPriv(inputObj, objPoi, hsCurrent);
			logCurrent.info("Exeute done!");
		} catch (Exception e) {
			m_Error = true;
			m_LastException = e;
			e.printStackTrace();
		} finally {
			currentThread.setContextClassLoader(currentCl);
		}
		return objRC;
	}

	protected abstract T doItPriv(I inputObj, T objPoi, HashMap<String, String> hsCurrent) throws Exception;

	public boolean hasError() {
		return m_Error;
	}

	public void setError(boolean error) {
		m_Error = error;
	}

	public Exception getLastException() {
		return m_LastException;
	}

	public void setLastException(Exception lastException) {
		m_LastException = lastException;
	}
}