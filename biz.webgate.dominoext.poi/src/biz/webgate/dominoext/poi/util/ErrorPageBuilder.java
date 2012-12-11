package biz.webgate.dominoext.poi.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ErrorPageBuilder {

	private static ErrorPageBuilder m_Builder;

	private ErrorPageBuilder() {

	}

	public static ErrorPageBuilder getInstance() {
		if (m_Builder == null) {
			m_Builder = new ErrorPageBuilder();
		}
		return m_Builder;
	}

	public void processError(HttpServletResponse httpResponse,
			String strErrorMessage, Throwable t) {
		try {
			httpResponse.setContentType("text/plain");
			PrintWriter pw = httpResponse.getWriter();
			pw.println("POI 4 XPages -> ERROR");
			pw.println("--------------------------------------------------------------");
			pw.println("Error    : " + strErrorMessage);
			pw.println("POI LIB  : " + POILibUtil.getPOILibUtilVersion());
			if (t != null) {
				pw.println("StackTrace:");
				t.printStackTrace(pw);
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
