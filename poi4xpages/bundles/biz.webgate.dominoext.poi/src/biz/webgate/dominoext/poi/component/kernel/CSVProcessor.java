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
package biz.webgate.dominoext.poi.component.kernel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.ibm.xsp.webapp.XspHttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.component.kernel.csv.EmbeddedDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.csv.XPagesDataSourceExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.util.MethodExecutor;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

public enum CSVProcessor {
	INSTANCE;

	public static CSVProcessor getInstance() {
		return INSTANCE;
	}

	public void generateNewFile(UICSV csvDef, HttpServletResponse httpResponse, FacesContext context, boolean noDownload, MethodBinding preDownload) {
		try {
			StringWriter sw = generateCSV(csvDef, context);
			MethodExecutor.INSTANCE.execute(preDownload, csvDef, context, sw);
			if (!noDownload) {
				httpResponse.setContentType("text/csv; charset=UTF-8");
				httpResponse.setHeader("Cache-Control", "no-cache");
				httpResponse.setDateHeader("Expires", -1);
				httpResponse.addHeader("Content-disposition", "inline; filename=\"" + csvDef.getDownloadFileName() + "\"");
				PrintWriter pw = httpResponse.getWriter();
				pw.write(sw.toString());
				pw.close();
			} else {
				PrintWriter pw = httpResponse.getWriter();
				pw.write(sw.toString());
				pw.close();
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(httpResponse, "Error during CSV-Generation", e);
		}

	}

	public StringWriter generateCSV(UICSV csvDef, FacesContext context) throws IOException, POIException {
		StringWriter sw = new StringWriter();
		CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT);

		List<CSVColumn> lstColumns = csvDef.getColumns();
		Collections.sort(lstColumns, (o1, o2) -> {
			int p1 = o1.getPosition();
			Integer p2 = o2.getPosition();
			return Integer.compare(p1, p2);
		});
		if (csvDef.isIncludeHeader()) {
			for (CSVColumn cl : lstColumns) {
				csvPrinter.print(cl.getTitle());
			}
			csvPrinter.println();
		}

		// DATASOURCE holen und verarbeiten.
		if (csvDef.getDataSource() != null) {
			EmbeddedDataSourceExportProcessor.getInstance().process(lstColumns, csvDef, csvPrinter, context);
		} else {
			XPagesDataSourceExportProcessor.getInstance().process(lstColumns, csvDef, csvPrinter, context);
		}

		csvPrinter.close();
		return sw;
	}

	public void processCall(FacesContext context, UICSV uicsv, boolean noDownload, MethodBinding preDownload) {
		HttpServletResponse httpResponse = (HttpServletResponse) context.getExternalContext().getResponse();
		if (httpResponse instanceof XspHttpServletResponse) {
			XspHttpServletResponse r = (XspHttpServletResponse) httpResponse;
			r.setCommitted(true);
			httpResponse = r.getDelegate();
		}

		try {
			generateNewFile(uicsv, httpResponse, context, noDownload, preDownload);
		} catch (Exception e) {
			try {
				e.printStackTrace();
				e.printStackTrace(httpResponse.getWriter());
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e);
			} catch (Exception e2) {
				e2.printStackTrace();
				e.printStackTrace();
				ErrorPageBuilder.getInstance().processError(httpResponse, "General Error!", e2);

			}
		}

	}

}
