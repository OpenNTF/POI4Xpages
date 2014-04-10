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
package biz.webgate.dominoext.poi.component.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.CSVExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportColumn;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportDataRow;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportModel;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.IExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.WorkbooklExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;
import biz.webgate.dominoext.poi.util.DatabaseProvider;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

import com.ibm.commons.util.StringUtil;

public class SimpleViewExportProcessor {

	private static SimpleViewExportProcessor m_Processor;

	private SimpleViewExportProcessor() {

	}

	private HashMap<String, IExportProcessor> m_ExportProcessors;

	public static SimpleViewExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new SimpleViewExportProcessor();
		}
		return m_Processor;
	}

	@SuppressWarnings("unchecked")
	private List<ExportColumn> buildColumnDefinitions(View viewCurrent) {
		List<ExportColumn> lstRC = new ArrayList<ExportColumn>();
		try {
			Vector<ViewColumn> vecColumnns = viewCurrent.getColumns();
			int nCounter = 0;
			for (Iterator<ViewColumn> itCol = vecColumnns.iterator(); itCol.hasNext();) {
				ViewColumn vcCurrent = itCol.next();
				if (!vcCurrent.isHidden()) {
					ExportColumn expCurrent = new ExportColumn();
					expCurrent.setPosition(nCounter);
					expCurrent.setColumnName(vcCurrent.getTitle());
					expCurrent.setTimeDateFormat(vcCurrent.getTimeDateFmt());
					lstRC.add(expCurrent);
				}
				nCounter++;
				vcCurrent.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstRC;
	}

	private List<ExportDataRow> buildExportDatas(ViewEntryCollection nvcCurrent, List<ExportColumn> lstExportColumns) {
		List<ExportDataRow> lstRC = new ArrayList<ExportDataRow>();
		try {
			ViewEntry nveNext = nvcCurrent.getFirstEntry();
			while (nveNext != null) {
				ViewEntry nveProcess = nveNext;
				nveNext = nvcCurrent.getNextEntry();

				ExportDataRow expDR = new ExportDataRow();
				expDR.setUNID(nveProcess.getUniversalID());
				for (ExportColumn expColumn : lstExportColumns) {
					if (expColumn.getPosition() < nveProcess.getColumnValues().size()) {
						expDR.addValue(expColumn.getPosition(), nveProcess.getColumnValues().get(expColumn.getPosition()));
					}
				}
				lstRC.add(expDR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstRC;
	}

	public void generateNewFile(UISimpleViewExport uiSimpleViewExport, HttpServletResponse httpResponse, FacesContext context) {
		try {
			if (StringUtil.isEmpty(uiSimpleViewExport.getDownloadFileName())) {
				ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: no DownloaldFileName specified.", null);
				return;
			}
			String strDB = uiSimpleViewExport.getDatabase();
			String strView = uiSimpleViewExport.getView();
			String strKey = uiSimpleViewExport.getKey();
			String strSearch = uiSimpleViewExport.getSearch();
			if (strView == null) {
				ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: no view specified.", null);
				return;
			}
			Database ndbAccess = DatabaseProvider.INSTANCE.getDatabase(strDB, false);
			if (ndbAccess == null) {
				ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: Database not accessable.", null);
				return;
			}
			View viwLUP = ndbAccess.getView(strView);

			if (viwLUP == null) {
				DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
				ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: View " + strView + " not found.", null);
				return;
			}
			ViewEntryCollection nvcCurrent = viwLUP.getAllEntries();
			if (strKey != null) {
				nvcCurrent = viwLUP.getAllEntriesByKey(strKey, true);
			} else {
				if (strSearch != null) {
					nvcCurrent.FTSearch(strSearch);
				}
			}
			List<ExportColumn> expColumns = buildColumnDefinitions(viwLUP);
			List<ExportDataRow> expResult = buildExportDatas(nvcCurrent, expColumns);
			ExportModel expModel = new ExportModel();
			expModel.setColumns(expColumns);
			expModel.setRows(expResult);
			if (m_ExportProcessors == null) {
				m_ExportProcessors = new HashMap<String, IExportProcessor>();
				m_ExportProcessors.put("xlsx", WorkbooklExportProcessor.getInstance());
				m_ExportProcessors.put("csv", CSVExportProcessor.getInstance());
			}
			String strExpFormat = uiSimpleViewExport.getExportFormat();
			IExportProcessor processor = null;
			if (strExpFormat != null) {
				processor = m_ExportProcessors.get(uiSimpleViewExport.getExportFormat().toLowerCase());
			}
			if (processor == null) {
				ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: No exportFormat defined or not valid (exportFormat = " + strExpFormat + ").", null);

			} else {
				processor.process2HTTP(expModel, uiSimpleViewExport, httpResponse, new DateTimeHelper());
			}
			DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
		} catch (NotesException e) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: A general error.", e);
			return;
		}
	}
}
