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
package biz.webgate.dominoext.poi.component.kernel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.POIException;
import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.component.kernel.csv.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.csv.XPagesDataSourceExportProcessor;
import biz.webgate.dominoext.poi.util.ErrorPageBuilder;

public class CSVProcessor {

	private static CSVProcessor m_Processor;

	private CSVProcessor() {

	}

	public static CSVProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new CSVProcessor();
		}
		return m_Processor;
	}

	public void generateNewFile(UICSV csvDef, HttpServletResponse httpResponse,
			FacesContext context) {

		try {
			// First getting the File

			ByteArrayOutputStream csvBAOS = generateCSV(csvDef, context);

			httpResponse.setContentType("text/csv");
			httpResponse.setHeader("Cache-Control", "no-cache");
			httpResponse.setDateHeader("Expires", -1);
			httpResponse.setContentLength(csvBAOS.size());
			httpResponse.addHeader("Content-disposition", "inline; filename=\""
					+ csvDef.getDownloadFileName() + "\"");
			OutputStream os = httpResponse.getOutputStream();
			csvBAOS.writeTo(os);
			os.close();
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse,
					"Error during CSV-Generation", e);
		}

	}

	public ByteArrayOutputStream generateCSV(UICSV csvDef, FacesContext context)
			throws IOException, POIException {
		ByteArrayOutputStream csvBAOS = new ByteArrayOutputStream();
		OutputStreamWriter csvWriter = new OutputStreamWriter(csvBAOS);
		CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT);

		List<CSVColumn> lstColumns = csvDef.getColumns();
		Collections.sort(lstColumns, new Comparator<CSVColumn>() {

			public int compare(CSVColumn o1, CSVColumn o2) {
				Integer p1 = new Integer(o1.getPosition());
				Integer p2 = new Integer(o2.getPosition());
				return p1.compareTo(p2);
			}

		});
		if (csvDef.isIncludeHeader()) {
			for (CSVColumn cl : lstColumns) {
				csvPrinter.print(cl.getTitle());
			}
			csvPrinter.println();
		}

		// DATASOURCE holen und verarbeiten.
		if (csvDef.getDataSource() != null) {
			EmbeddedDataSourceExportProcessor.getInstance().process(lstColumns,
					csvDef, csvPrinter, context);
		} else {
			XPagesDataSourceExportProcessor.getInstance().process(lstColumns,
					csvDef, csvPrinter, context);
		}

		csvPrinter.flush();
		return csvBAOS;
	}

}
