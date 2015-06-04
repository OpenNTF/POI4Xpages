package biz.webgate.dominoext.poi.util.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import biz.webgate.dominoext.poi.pdf.IPDFService;
import biz.webgate.dominoext.poi.pdf.PDFConversionService;
import biz.webgate.dominoext.poi.util.AbstractPOIPowerActionExtended;

public class DocX2PDFAction extends AbstractPOIPowerActionExtended<OutputStream, InputStream> {

	@Override
	protected OutputStream doItPriv(InputStream inputObj, OutputStream outputStream, HashMap<String, String> hsCurrent) throws Exception {
		IPDFService service = PDFConversionService.getInstance().getPDFService();
		OutputStream out = new ByteArrayOutputStream();
		if (outputStream != null) {
			out = outputStream;
		}
		service.buildPDF(inputObj, out);
		return out;
	}

}
