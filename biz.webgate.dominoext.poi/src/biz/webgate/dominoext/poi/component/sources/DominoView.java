/*
 * © Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi.component.sources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import biz.webgate.dominoext.poi.component.data.IDefinition;
import biz.webgate.dominoext.poi.util.DatabaseProvider;
import biz.webgate.dominoext.poi.utils.xsp.ValueBindingSupport;

import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class DominoView extends ValueBindingObjectImpl implements IExportSource {

	private String m_ViewName;
	private String m_Database;
	private String m_Key;
	private String m_Search;
	private int m_maxRow = 1000;

	private DataTempStore m_tempDataStore;

	protected class DataTempStore {
		public Database m_Database;
		public View m_View;
		public ViewEntry m_Entry;
		public ViewEntryCollection m_Col;
		public List<String> m_ColTitle;
		public int m_rowCount;
		public Vector<?> m_ColValues;
	}

	public int getMaxRow() {
		return m_maxRow;
	}

	public void setMaxRow(int maxRow) {
		m_maxRow = maxRow;
	}

	public int accessNextRow() {
		if (m_tempDataStore == null) {
			return -1;
		}
		try {
			if (m_tempDataStore.m_Entry == null) {
				m_tempDataStore.m_Entry = m_tempDataStore.m_Col.getFirstEntry();
			} else {
				ViewEntry ve = m_tempDataStore.m_Entry;
				m_tempDataStore.m_Entry = m_tempDataStore.m_Col.getNextEntry(ve);
				ve.recycle();
			}
			if (m_tempDataStore.m_Entry == null) {
				return 0;
			}
			m_tempDataStore.m_ColValues = m_tempDataStore.m_Entry.getColumnValues();
			m_tempDataStore.m_rowCount++;
			if (m_tempDataStore.m_rowCount > m_maxRow) {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -10;
		}
		return 1;
	}

	public int accessSource() {
		if (m_tempDataStore == null) {
			m_tempDataStore = new DataTempStore();
			String strDB = getDatabase();
			String strView = getViewName();
			String strKey = getKey();
			String strSearch = getSearch();
			if (strView == null) {
				System.out.println("???" + strView);
				return -1;
			}
			try {
				Database ndbAccess = DatabaseProvider.INSTANCE.getDatabase(strDB);
				if (ndbAccess == null) {
					return -2;
				}
				View viwLUP = ndbAccess.getView(strView);

				if (viwLUP == null) {
					ndbAccess.recycle();
					return -3;
				}
				ViewEntryCollection nvcCurrent = viwLUP.getAllEntries();
				if (strKey != null) {
					nvcCurrent = viwLUP.getAllEntriesByKey(strKey, true);
					System.out.println("Build collection with Key: " + strKey + "(" + nvcCurrent.getCount() + ")");
				} else {
					if (strSearch != null) {
						nvcCurrent.FTSearch(strSearch);

					}
				}
				m_tempDataStore.m_Database = ndbAccess;
				m_tempDataStore.m_View = viwLUP;
				m_tempDataStore.m_Col = nvcCurrent;
				m_tempDataStore.m_ColTitle = new ArrayList<String>();
				m_tempDataStore.m_rowCount = 0;
				for (Iterator<?> itNames = viwLUP.getColumnNames().iterator(); itNames.hasNext();) {
					String strCLNAME = (String) itNames.next();
					m_tempDataStore.m_ColTitle.add(strCLNAME);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -10;
			}
		}
		return 1;
	}

	public int closeSource() {
		try {
			if (m_tempDataStore != null) {
				if (m_tempDataStore.m_Entry != null) {
					m_tempDataStore.m_Entry.recycle();
				}
				if (m_tempDataStore.m_Col != null) {
					m_tempDataStore.m_Col.recycle();
				}
				if (m_tempDataStore.m_View != null) {
					m_tempDataStore.m_View.recycle();
				}
				if (m_tempDataStore.m_Database != null) {
					m_tempDataStore.m_Database.recycle();
				}
			}
			m_tempDataStore = null;
		} catch (Exception e) {
			e.printStackTrace();
			return -10;
		}
		return 1;
	}

	public Object getValue(IDefinition idCurrent, String strVarName, String strIndName, int nIndex, FacesContext context) {
		String strVarNameUse = (strVarName == null || "".equals(strVarName)) ? "exportRow" : strVarName;
		String strIndNameUse = (strIndName == null || "".equals(strIndName)) ? "indexRow" : strIndName;
		try {
			String strTitle = idCurrent.getColumnTitle();
			if (strTitle != null && !"".equals(strTitle)) {
				if (m_tempDataStore.m_ColTitle.contains(strTitle)) {
					int nPos = m_tempDataStore.m_ColTitle.indexOf(strTitle);
					return m_tempDataStore.m_ColValues.elementAt(nPos);
				}
			} else {

				return idCurrent.executeComputeValue(context, m_tempDataStore.m_Entry, nIndex, strVarNameUse, strIndNameUse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getViewName() {
		if (m_ViewName != null) {
			return m_ViewName;
		}
		return ValueBindingSupport.getVBString("viewName", this, getFacesContext());

	}

	public void setViewName(String viewName) {
		m_ViewName = viewName;
	}

	public String getDatabase() {
		if (m_Database != null) {
			return m_Database;
		}
		return ValueBindingSupport.getVBString("database", this, getFacesContext());

	}

	public void setDatabase(String database) {
		m_Database = database;
	}

	public String getKey() {
		if (m_Key != null) {
			return m_Key;
		}
		return ValueBindingSupport.getVBString("key", this, getFacesContext());

	}

	public void setKey(String key) {
		m_Key = key;
	}

	public String getSearch() {
		if (m_Search != null) {
			return m_Search;
		}
		return ValueBindingSupport.getVBString("search", this, getFacesContext());

	}

	public void setSearch(String search) {
		m_Search = search;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[6];
		state[0] = super.saveState(context);
		state[1] = m_Database;
		state[2] = m_ViewName;
		state[3] = m_Key;
		state[4] = m_Search;
		state[5] = new Integer(m_maxRow);
		return state;
	}

	@Override
	public void restoreState(FacesContext context, Object arg1) {
		Object[] state = (Object[]) arg1;
		super.restoreState(context, state[0]);
		m_Database = (String) state[1];
		m_ViewName = (String) state[2];
		m_Key = (String) state[3];
		m_Search = (String) state[4];
		m_maxRow = (Integer) state[5];
	}

}
