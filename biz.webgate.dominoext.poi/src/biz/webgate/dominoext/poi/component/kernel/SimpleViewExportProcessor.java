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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntryCollection;
import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.CSVExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportModel;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportModelBuilder;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.IExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.WorkbooklExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;
import biz.webgate.dominoext.poi.util.DatabaseProvider;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

import com.ibm.commons.util.StringUtil;

public enum SimpleViewExportProcessor {
	CSV(CSVExportProcessor.getInstance()), XLSX(WorkbooklExportProcessor.getInstance());

	private SimpleViewExportProcessor(IExportProcessor exportProcessor) {
		m_ExportProcessors = exportProcessor;
	}

	private IExportProcessor m_ExportProcessors;

	public static SimpleViewExportProcessor getInstance(UISimpleViewExport uicontrol, HttpServletResponse httpResponse) {
		String strExpFormat = uicontrol.getExportFormat();
		for (SimpleViewExportProcessor proc : values()) {
			if (proc.name().equalsIgnoreCase(strExpFormat)) {
				return proc;
			}
		}
		ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: No exportFormat defined or not valid (exportFormat = " + strExpFormat + ").", null);
		return null;
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
			ExportModel expModel = ExportModelBuilder.INSTANCE.buildFromView(viwLUP);
			ExportModelBuilder.INSTANCE.applyRowData(expModel, nvcCurrent);
			m_ExportProcessors.process2HTTP(expModel, uiSimpleViewExport, httpResponse, new DateTimeHelper());

			DatabaseProvider.INSTANCE.handleRecylce(ndbAccess);
		} catch (NotesException e) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "SimpleViewExport failed: A general error.", e);
			return;
		}
	}
}
