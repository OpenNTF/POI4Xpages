package biz.webgate.dominoext.poi.pdf.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.docx4j.Docx4jProperties;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import biz.webgate.dominoext.poi.pdf.IPDFService;
import biz.webgate.dominoext.poi.pdf.PDFException;

public class FOPService implements IPDFService {

	public void buildPDF(InputStream isDocument, OutputStream osTarget)
			throws PDFException {
		try {
			ClassLoader clCurrent = Thread.currentThread()
					.getContextClassLoader();
			System.out.println(clCurrent);
			System.out.println(clCurrent.getClass());
			System.out.println(System.getProperty("java.version"));
			Field fldProp = Docx4jProperties.class.getDeclaredField("properties");
			fldProp.setAccessible(true);
			Properties propNew = new Properties();

			propNew.setProperty("docx4j.PageSize", "LETTER");
			propNew.setProperty("docx4j.PageMargins", "NORMAL");
			propNew.setProperty("docx4j.PageOrientationLandscape", "false");
			propNew.setProperty("docx4j.App.write", "true");
			propNew.setProperty("docx4j.Application", "docx4j");
			propNew.setProperty("docx4j.AppVersion", "2.7");
			propNew.setProperty("docx4j.dc.write", "true");
			propNew.setProperty("docx4j.dc.creator.value", "docx4j");
			propNew.setProperty("docx4j.dc.lastModifiedBy.value", "docx4j");

			propNew.setProperty("docx4j.Log4j.Configurator.disabled", "false");

			fldProp.set(Docx4jProperties.class, propNew);
			System.out.println("Und jetzt?????");
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
					.load(isDocument);

			// 2) Prepare Pdf settings
			PdfSettings pdfSettings = new PdfSettings();

			// 3) Convert WordprocessingMLPackage to Pdf

			PdfConversion converter = new Conversion(wordMLPackage);

			converter.output(osTarget, pdfSettings);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PDFException("Error during conversion", e);
		}
	}

	public String getName() {
		return "FOP";
	}

}
