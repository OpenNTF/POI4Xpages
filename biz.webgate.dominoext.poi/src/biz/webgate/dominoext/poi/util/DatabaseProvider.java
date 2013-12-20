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
package biz.webgate.dominoext.poi.util;

import lotus.domino.Database;
import lotus.domino.Session;

public enum DatabaseProvider {
	INSTANCE;

	public Database getDatabase(String strDB) {
		Session sesSigner = POILibUtil.getCurrentSessionAsSigner();
		if (sesSigner == null) {
			sesSigner = POILibUtil.getCurrentSession();
		}
		Database ndbAccess = null;
		try {
			if (strDB == null) {
				ndbAccess = sesSigner.getCurrentDatabase();
			} else {
				if (strDB.contains("!!")) {
					String[] arrDB = strDB.split("!!");
					ndbAccess = sesSigner.getDatabase(arrDB[0], arrDB[1]);
				} else {
					ndbAccess = sesSigner.getDatabase(sesSigner.getCurrentDatabase().getServer(), strDB);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ndbAccess;

	}
}
