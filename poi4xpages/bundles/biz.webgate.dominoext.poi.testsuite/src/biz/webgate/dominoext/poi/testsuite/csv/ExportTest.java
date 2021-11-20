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
package biz.webgate.dominoext.poi.testsuite.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.easymock.EasyMock;
import org.junit.Test;

import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.CSVExportProcessor;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportDataRow;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportModel;
import biz.webgate.dominoext.poi.component.kernel.simpleviewexport.ExportModelBuilder;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;
import lotus.domino.NotesException;
import lotus.domino.ViewEntryCollection;

public class ExportTest {

	@Test
	public void testUmlauteToStringWriter() throws IOException {

		StringWriter sw = new StringWriter();
		CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT);
		printValues(printer);
		String result = sw.toString();
		assertTrue(result.contains("Ren�"));
	}

	private void printValues(CSVPrinter printer) throws IOException {
		printer.print("Hans");
		printer.print("Meier");
		printer.print("Peter");
		printer.print("M�ller");
		printer.print("G�rald");
		printer.print("Ren�");
		printer.println();
		printer.close();
	}

	@Test
	public void testUmlautWithExportModelandCSVExportProcessor() throws IOException {
		ExportModel model = buildModel();
		DateTimeHelper dth = EasyMock.createNiceMock(DateTimeHelper.class);
		UISimpleViewExport uis = new UISimpleViewExport();
		uis.setIncludeHeader(true);
		HttpServletResponse hsr = EasyMock.createNiceMock(HttpServletResponse.class);
		hsr.setContentType("text/csv");
		hsr.setHeader("Cache-Control", "no-cache");
		hsr.setDateHeader("Expires", -1);
		hsr.setContentLength(0);
		hsr.addHeader("Content-disposition", "inline; filename=\"" + uis.getDownloadFileName() + "\"");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		EasyMock.expect(hsr.getWriter()).andReturn(pw);
		EasyMock.replay(hsr);
		CSVExportProcessor.getInstance().process2HTTP(model, uis, hsr, dth,false,null,null);
		assertTrue(sw.toString().contains("Ren�"));
	}

	@Test
	public void testBuildModelFromView() throws NotesException {
		ExportModel model = ExportModelBuilder.INSTANCE.buildFromView(MockHelper.buildMockView());
		assertNotNull(model);
		assertEquals(3, model.getColumns().size());
	}

	@Test
	public void testBuildModelRowEntries() throws NotesException {
		ViewEntryCollection collection = MockHelper.buildViewEntryCollection();
		ExportModel model = ExportModelBuilder.INSTANCE.buildFromView(MockHelper.buildMockView());
		ExportModelBuilder.INSTANCE.applyRowData(model, collection);
		assertNotNull(model.getRows());
		assertEquals(3, model.getRows().size());
	}

	private ExportModel buildModel() {
		ExportModel model = new ExportModel();
		model.addColumnByName("Vorname", 0, 0);
		model.addColumnByName("Name", 0, 1);
		ExportDataRow ed1 = ExportDataRow.buildDataRow("9911038101");
		ed1.addValue(0, "Ren�");
		ed1.addValue(1, "M�ller");
		model.addRow(ed1);
		return model;
	}

}
