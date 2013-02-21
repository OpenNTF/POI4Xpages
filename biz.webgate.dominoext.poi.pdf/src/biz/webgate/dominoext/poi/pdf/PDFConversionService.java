/*
 * © Copyright WebGate Consulting AG, 2013
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
package biz.webgate.dominoext.poi.pdf;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Logger;

import biz.webgate.dominoext.poi.pdf.service.FOPService;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

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
		final Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		if (m_PdfService == null) {
			AccessController.doPrivileged(new PrivilegedAction<Void>() {
				public Void run() {
					String providersProp = ApplicationEx.getInstance()
							.getApplicationProperty(PREF_PROVIDER, null);
					logCurrent.info(PREF_PROVIDER +" = "+ providersProp);
					@SuppressWarnings("unchecked")
					List<IPDFService> allPDFProviders = ApplicationEx
							.getInstance().findServices(
									PDF_DATAPROVIDER_SERVICE);
					for (int i = 0; i < allPDFProviders.size(); i++) {
						IPDFService p = allPDFProviders.get(i);
						logCurrent.info( "TEST of "+ p.getName());
						if (p.getName().equalsIgnoreCase(providersProp)) {
							m_PdfService = p;
							break;
						}
					}
					if (m_PdfService == null) {
						logCurrent.info("No PDF Service found. Assign FOP Service!");
						m_PdfService = new FOPService();
					}

					return null;
				}
			});
		}
		return m_PdfService;
	}

}
