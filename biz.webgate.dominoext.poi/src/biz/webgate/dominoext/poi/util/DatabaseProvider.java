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

import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Session;

import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

import com.ibm.commons.util.StringUtil;

public enum DatabaseProvider {
	INSTANCE;

	public Database getDatabase(String strDB) {
		Session sesSigner = POILibUtil.getCurrentSessionAsSigner();
		if (sesSigner == null) {
			sesSigner = POILibUtil.getCurrentSession();
		}
		Database ndbAccess = null;
		try {
			if (StringUtil.isEmpty(strDB)) {
				ndbAccess = sesSigner.getCurrentDatabase();
				if (ndbAccess == null) {
					System.out.println("sesSigner DB war nix?");
					ndbAccess = POILibUtil.getCurrentDatabase();
				}
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

	public void handleRecylce(Database ndbRecylce) {
		try {
			Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
			//Database accessed with sesSigner.getCurrentDB() should not be recycled.
			if (POILibUtil.getCurrentSessionAsSigner() != null && ndbRecylce.equals(POILibUtil.getCurrentSessionAsSigner().getCurrentDatabase())) {
				logCurrent.info("No recycle -> sesSigner.currentDB");
				return;
			}
			//Dabases accsesd with ses.getCurrnentDB() should not be reccled.
			if (ndbRecylce.equals(POILibUtil.getCurrentDatabase())) {
				logCurrent.info("No recycle -> ses.currentDB");
				return;
			}
			
			logCurrent.info("Recycle -> DB is not currentDB");
			ndbRecylce.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
