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
package extlib;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class DataAccessor {

	private boolean firstContactIDRead;
	private String firstContactID;
	
	public DataAccessor() {
	}

	
	// ===================================================================
	// Access the first document in the contact view
	// ===================================================================

	public String getFirstContactID() throws NotesException {
		if(!firstContactIDRead) {
			Database db = ExtLibUtil.getCurrentDatabase();
			View view = db.getView("AllContacts");
			Document doc = view.getFirstDocument();
			if(doc!=null) {
				firstContactID = doc.getNoteID();
			}
		}
		return firstContactID;
	}
}
