/*
 * � Copyright WebGate Consulting AG, 2013
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
package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.DateTime;
import lotus.domino.ViewColumn;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;
import biz.webgate.dominoext.poi.component.kernel.util.MethodExecutor;
import biz.webgate.dominoext.poi.utils.logging.ErrorPageBuilder;

public class CSVExportProcessor implements IExportProcessor {

	private static CSVExportProcessor m_Processor;

	private CSVExportProcessor() {

	}

	public static CSVExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new CSVExportProcessor();
		}
		return m_Processor;
	}

	public void process2HTTP(ExportModel expModel, UISimpleViewExport uis, HttpServletResponse hsr, DateTimeHelper dth, boolean noDownload, MethodBinding preDownload, FacesContext context) {
		try {
			StringWriter sw = new StringWriter();
			CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT);

			// BUILDING HEADER
			if (uis.isIncludeHeader()) {
				for (ExportColumn expColumn : expModel.getColumns()) {
					csvPrinter.print(expColumn.getColumnName());
				}
				csvPrinter.println();
			}
			// Processing Values
			for (ExportDataRow expRow : expModel.getRows()) {
				for (ExportColumn expColumn : expModel.getColumns()) {
					csvPrinter.print(convertValue(expRow.getValue(expColumn.getPosition()), expColumn, dth));
				}
				csvPrinter.println();
			}
			// csvPrinter.flush();
			csvPrinter.close();
			MethodExecutor.INSTANCE.execute(preDownload, uis, context, sw);
			if (!noDownload) {
				hsr.setContentType("text/csv; charset=UTF-8");
				hsr.setHeader("Cache-Control", "no-cache");
				hsr.setDateHeader("Expires", -1);
				// hsr.setContentLength(csvBAOS.size());
				hsr.addHeader("Content-disposition", "inline; filename=\"" + uis.getDownloadFileName() + "\"");
				PrintWriter pw = hsr.getWriter();
				pw.write(sw.toString());
				pw.close();
			} else {
				PrintWriter pw = hsr.getWriter();
				pw.close();
			}
		} catch (Exception e) {
			ErrorPageBuilder.getInstance().processError(hsr, "Error during SVE-Generation (CSV Export)", e);
		}
	}

	private String convertValue(Object obj, ExportColumn expCol, DateTimeHelper dth) {
		if (obj instanceof Double) {
			return ((Double) obj).toString();
		}
		if (obj instanceof Integer) {
			return ((Integer) obj).toString();
		}

		if (obj instanceof Date) {
			switch (expCol.getTimeDateFormat()) {
			case ViewColumn.FMT_DATE:
				return dth.getDFDate().format((Date) obj);
			case ViewColumn.FMT_TIME:
				return dth.getDFTime().format((Date) obj);
			default:
				return dth.getDFDateTime().format((Date) obj);
			}
		}
		if (obj instanceof DateTime) {
			try {
				Date dtObj = ((DateTime) obj).toJavaDate();
				switch (expCol.getTimeDateFormat()) {
				case ViewColumn.FMT_DATE:
					return dth.getDFDate().format(dtObj);
				case ViewColumn.FMT_TIME:
					return dth.getDFTime().format(dtObj);
				default:
					return dth.getDFDateTime().format(dtObj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "" + obj;
			}
		}
		if (obj instanceof Vector<?>) {
			Vector<?> vec = (Vector<?>) obj;
			StringBuilder sb = new StringBuilder();
			for (Iterator<?> it = vec.iterator(); it.hasNext();) {
				sb.append("" + it.next());
				if (it.hasNext()) {
					sb.append(";");
				}
			}
			return sb.toString();
		}

		return "" + obj;
	}
}
