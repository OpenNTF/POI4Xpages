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
package biz.webgate.dominoext.poi.pdf.fopdocx4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.utils.Log4jConfigurator;

import biz.webgate.dominoext.poi.utils.exceptions.PDFException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public class FOPServiceImpl {

	private static FOPServiceImpl m_Service;

	private FOPServiceImpl() {

	}

	public static FOPServiceImpl getInstance() {
		if (m_Service == null) {
			m_Service = new FOPServiceImpl();
		}
		return m_Service;
	}

	public void buildPDF(InputStream isDocument, OutputStream osTarget)
			throws PDFException {
		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());
		boolean blRC = true;
		Exception eRESP = null;
		Thread currentThread = Thread.currentThread();
		ClassLoader clCurrent = currentThread.getContextClassLoader();
		logCurrent.info("Current thread class loader is: " +clCurrent);
		try {
			currentThread.setContextClassLoader(Activator.class
					.getClassLoader());
			Log4jConfigurator.configure();
			if (logCurrent.getLevel().equals(Level.ALL)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ALL);
			}
			if (logCurrent.getLevel().equals(Level.FINE)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.TRACE);
			}
			if (logCurrent.getLevel().equals(Level.FINER)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.TRACE);
			}
			if (logCurrent.getLevel().equals(Level.FINEST)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.TRACE);
			}
			if (logCurrent.getLevel().equals(Level.INFO)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
			}
			if (logCurrent.getLevel().equals(Level.WARNING)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.WARN);
			}
			if (logCurrent.getLevel().equals(Level.SEVERE)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ERROR);
			}
			if (logCurrent.getLevel().equals(Level.OFF)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
			}

			logCurrent.info("Getting WordprozessingPackage");
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
					.load(isDocument);

			logCurrent.info("Getting PdfSettings");
			// 2) Prepare Pdf settings
			PdfSettings pdfSettings = new PdfSettings();

			// 3) Convert WordprocessingMLPackage to Pdf
			logCurrent.info("Getting PdfConversion");
			PdfConversion converter = new Conversion(wordMLPackage);

			logCurrent.info("do Conversion");
			converter.output(osTarget, pdfSettings);
		} catch (Exception e) {
			eRESP = e;
			logCurrent.log(Level.SEVERE, "Error during PDF Conversion: "+e.getMessage(),e);
			blRC = false;
		} finally {
			currentThread.setContextClassLoader(clCurrent);
		}
		if (!blRC) {
			throw new PDFException("Error during FOP PDF Generation", eRESP);
		}
	}

}
