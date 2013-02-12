package biz.webgate.dominoext.poi.pdf;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import biz.webgate.dominoext.poi.pdf.service.FOPService;

import com.ibm.designer.runtime.Application;
import com.ibm.xsp.application.ApplicationEx;

public class PDFConversionService {

	private static final String PDF_SERVICE_KEY = "poi.pdfservice"; // $NON-NLS-1$

	private static final String PDF_DATAPROVIDER_SERVICE = "biz.webgate.poi.pdf.service"; // $NON-NLS-1$

	private static final String PREF_PROVIDER = "poi.pdfservice.provider";

	private IPDFService m_PdfService;

	private PDFConversionService() {

	}

	public static PDFConversionService getInstance() {
		PDFConversionService f = (PDFConversionService) Application.get()
				.getObject(PDF_SERVICE_KEY);
		if (f == null) {
			synchronized (PDFConversionService.class) {
				f = (PDFConversionService) Application.get().getObject(
						PDF_SERVICE_KEY);
				if (f == null) {
					f = new PDFConversionService();
					Application.get().putObject(PDF_SERVICE_KEY, f);
				}
			}
		}
		return f;
	}

	public IPDFService getPDFService() {
		if (m_PdfService == null) {
			// Execute everything in a privileged block as it accesses class
			// loaders and read extension points
			AccessController.doPrivileged(new PrivilegedAction<Void>() {
				public Void run() {
					//
					// Find the people providers
					//

					// Read the authorized providers
					String providersProp = ApplicationEx.getInstance()
							.getApplicationProperty(PREF_PROVIDER, null);

					@SuppressWarnings("unchecked")
					List<IPDFService> allPDFProviders = ApplicationEx
							.getInstance().findServices(
									PDF_DATAPROVIDER_SERVICE);
					for (int i = 0; i < allPDFProviders.size(); i++) {
						IPDFService p = allPDFProviders.get(i);
						if (p.getName().equalsIgnoreCase(providersProp)) {
							m_PdfService = p;
							break;
						}
					}
					if (m_PdfService == null) {
						m_PdfService = new FOPService();
					}

					return null;
				}
			});
		}
		return m_PdfService;
	}

}
