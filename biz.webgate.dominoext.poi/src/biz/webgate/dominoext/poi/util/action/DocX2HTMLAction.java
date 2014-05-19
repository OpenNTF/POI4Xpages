package biz.webgate.dominoext.poi.util.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import biz.webgate.dominoext.poi.util.AbstractPOIPowerActionExtended;

public class DocX2HTMLAction extends AbstractPOIPowerActionExtended<OutputStream, InputStream> {

	@Override
	protected OutputStream doItPriv(InputStream inputObj, OutputStream objPoi, HashMap<String, String> hsCurrent) throws Exception {
		XWPFDocument document = new XWPFDocument(inputObj); // createDocument(is);

		XHTMLOptions options = XHTMLOptions.create(); // createOptions();

		OutputStream out = new ByteArrayOutputStream();// new

		XHTMLConverter.getInstance().convert(document, out, options);

		out.flush();
		return out;
	}

}
