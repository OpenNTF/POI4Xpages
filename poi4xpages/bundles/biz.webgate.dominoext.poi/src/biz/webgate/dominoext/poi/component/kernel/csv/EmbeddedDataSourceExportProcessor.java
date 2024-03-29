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
package biz.webgate.dominoext.poi.component.kernel.csv;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.csv.CSVPrinter;

import com.ibm.xsp.util.DataPublisher.ShadowedObject;

import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.component.sources.IExportSource;
import biz.webgate.dominoext.poi.util.RequestVarsHandler;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;

public class EmbeddedDataSourceExportProcessor implements IDataSourceProcessor {

	private static EmbeddedDataSourceExportProcessor m_Processor;

	private EmbeddedDataSourceExportProcessor() {

	}

	public static EmbeddedDataSourceExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new EmbeddedDataSourceExportProcessor();
		}
		return m_Processor;
	}

	@Override
	public void process(List<CSVColumn> lstColumns, UICSV csvDef, CSVPrinter csvPrinter, FacesContext context) throws POIException {
		try {
			IExportSource is = csvDef.getDataSource();
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: " + is.getClass() + " Error: " + nAccess);
			}
			int nCount = 0;
			List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, csvDef.getVar(), csvDef.getIndex());
			while (is.accessNextRow() == 1) {
				nCount++;
				RequestVarsHandler.INSTANCE.pushVars(context, csvDef.getVar(), csvDef.getIndex(), is.getDataRow(), nCount);

				for (CSVColumn cl : lstColumns) {
					Object objCurrent = is.getValue(cl, context);
					csvPrinter.print(objCurrent);
				}
				csvPrinter.println();
				RequestVarsHandler.INSTANCE.removeVars(context, csvDef.getVar(), csvDef.getIndex());

			}
			RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);
			is.closeSource();
		} catch (Exception e) {
			throw new POIException("Error in processExportRow", e);
		}

	}

}
