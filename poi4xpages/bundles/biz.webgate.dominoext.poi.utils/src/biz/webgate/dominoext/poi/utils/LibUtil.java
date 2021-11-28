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
package biz.webgate.dominoext.poi.utils;

import com.ibm.domino.xsp.module.nsf.NotesContext;

import lotus.domino.Database;
import lotus.domino.Session;

public class LibUtil {

	public static String getPOILibUtilVersion() {
		return Activator.getPoiVersion();
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

}
