package biz.webgate.dominoext.poi.pdf;

import java.io.InputStream;
import java.io.OutputStream;

public interface IPDFService {

	public void buildPDF(InputStream isDocument, OutputStream osTarget) throws PDFException;
	public String getName();
}
