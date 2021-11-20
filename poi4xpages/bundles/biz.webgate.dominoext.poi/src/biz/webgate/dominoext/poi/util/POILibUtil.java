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
package biz.webgate.dominoext.poi.util;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.ibm.designer.runtime.Version;
import com.ibm.domino.xsp.module.nsf.NotesContext;

import biz.webgate.dominoext.poi.Activator;
import lotus.domino.Database;
import lotus.domino.Session;

@SuppressWarnings("deprecation")
public class POILibUtil {

	private static Boolean _9;
	public static final boolean isXPages9() {
		if (_9 == null) {
			Version v = Version.CurrentRuntimeVersion;
			_9 = (v.getMajor() == 9);
		}
		return _9;
	}



	public static String getPOILibUtilVersion() {
		try {
			String s = AccessController
					.doPrivileged((PrivilegedAction<String>) () -> {
						Object o = Activator.getContext().getBundle()
								.getHeaders().get("Bundle-Version"); // $NON-NLS-1$
						if (o != null) {
							return o.toString();
						}
						return null;
					});
			if (s != null) {
				return s;
			}
		} catch (SecurityException ex) {
		}
		return "";
	}

	public static Database getCurrentDatabase() {
		NotesContext nc = NotesContext.getCurrentUnchecked();
		return nc != null ? nc.getCurrentDatabase() : null;
	}

	public static Session getCurrentSession() {
		NotesContext nc = NotesContext.getCurrentUnchecked();
		return nc != null ? nc.getCurrentSession() : null;
	}

	/**
	 * Return the signer session.
	 */
	public static Session getCurrentSessionAsSigner() {
		NotesContext nc = NotesContext.getCurrentUnchecked();
		return nc != null ? nc.getSessionAsSigner() : null;
	}

	public static XWPFDocument getDocument(InputStream inDocument) {
		XWPFDocument dxReturn = null;
		try {
			dxReturn = new XWPFDocument(inDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dxReturn;
	}

	public static Workbook getWorkbook(InputStream inExcel) {
		Workbook wbRC = null;
		try {
			wbRC = WorkbookFactory.create(inExcel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wbRC;
	}
}
