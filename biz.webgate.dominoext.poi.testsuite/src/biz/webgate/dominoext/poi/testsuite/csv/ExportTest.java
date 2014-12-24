package biz.webgate.dominoext.poi.testsuite.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import lotus.domino.NotesException;
import lotus.domino.ViewEntryCollection;

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

public class ExportTest {

	@Test
	public void testUmlauteToStringWriter() throws IOException {

		StringWriter sw = new StringWriter();
		CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT);
		printValues(printer);
		String result = sw.toString();
		assertTrue(result.contains("René"));
	}

	private void printValues(CSVPrinter printer) throws IOException {
		printer.print("Hans");
		printer.print("Meier");
		printer.print("Peter");
		printer.print("Müller");
		printer.print("Gérald");
		printer.print("René");
		printer.println();
		printer.close();
	}

	@Test
	public void testUmlauteWithByteArrayOutputStream() throws IOException {
		ByteArrayOutputStream csvBAOS = new ByteArrayOutputStream();
		OutputStreamWriter csvWriter = new OutputStreamWriter(csvBAOS);
		CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT);

		printValues(csvPrinter);

		String result = csvBAOS.toString();
		assertTrue(result.contains("René"));

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
		CSVExportProcessor.getInstance().process2HTTP(model, uis, hsr, dth);
		assertTrue(sw.toString().contains("René"));
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
		ed1.addValue(0, "René");
		ed1.addValue(1, "Müller");
		model.addRow(ed1);
		return model;
	}

}
