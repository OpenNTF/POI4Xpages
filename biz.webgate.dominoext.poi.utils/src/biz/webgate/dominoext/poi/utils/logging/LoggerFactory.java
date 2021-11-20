package biz.webgate.dominoext.poi.utils.logging;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.webgate.dominoext.poi.utils.LibUtil;

public class LoggerFactory {
	private static HashMap<String, Logger> m_RegistredLoggers = new HashMap<String, Logger>();

	private static int m_logLevel = -1;
	private static int m_logLevelPDF = -1;
	private static int m_logLevelFOP = -1;

	public static Logger getLogger(String strName) {
		try {
			if (m_RegistredLoggers.containsKey(strName)) {
				return m_RegistredLoggers.get(strName);
			}
			Logger logRC = java.util.logging.Logger.getAnonymousLogger();
			if (m_logLevel == -1) {
				checkLogLevel();
			}
			logRC.setLevel(getLogLevel(getLogLevelForClass(strName)));
			ConsoleHandler ch = new ConsoleHandler(strName,
					getLogLevel(m_logLevel));
			logRC.addHandler(ch);
			m_RegistredLoggers.put(strName, logRC);
			return logRC;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Level getLogLevel(int nLevel) {
		switch (nLevel) {
		case 0:
			return Level.OFF;
		case 1:
			return Level.SEVERE;
		case 2:
			return Level.WARNING;
		case 3:
			return Level.INFO;
		case 4:
			return Level.FINE;
		case 5:
			return Level.FINER;
		case 6:
			return Level.FINEST;

		}
		return Level.ALL;

	}

	private static void checkLogLevel() {
		try {
			String strPOI = LibUtil.getCurrentSession().getEnvironmentString(
					"DEBUG_POI", true);
			if (strPOI == null || "".equals(strPOI)) {
				m_logLevel = 1;
			}
			String strPOIPDF = LibUtil.getCurrentSession()
					.getEnvironmentString("DEBUG_POI_PDF", true);
			if (strPOIPDF == null || "".equals(strPOIPDF)) {
				m_logLevelPDF = 1;
			}
			String strPOIFOP = LibUtil.getCurrentSession()
					.getEnvironmentString("DEBUG_POI_FOP", true);
			if (strPOIFOP == null || "".equals(strPOIFOP)) {
				m_logLevelFOP = 1;
			}

			try {
				if (strPOI != null && !"".equals(strPOI)) {
					m_logLevel = Integer.parseInt(strPOI);
				}
				if (strPOIPDF != null && !"".equals(strPOIPDF)) {
					m_logLevelPDF = Integer.parseInt(strPOIPDF);
				}
				if (strPOIFOP != null && !"".equals(strPOIFOP)) {
					m_logLevelFOP = Integer.parseInt(strPOIFOP);
				}
			} catch (Exception e) {
				m_logLevel = 0;
			}
			System.out.println("POI     LOG-LEVEL is set to: " + strPOI +" / "+ m_logLevel);
			System.out.println("POI PDF LOG-LEVEL is set to: " + strPOIPDF+" / "+ m_logLevelPDF);
			System.out.println("POI FOP LOG-LEVEL is set to: " + strPOIFOP+" / "+ m_logLevelFOP);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int getLogLevelForClass(String strClass) {
		if (strClass == null) {
			return m_logLevel;
		}
		if (strClass.toLowerCase().startsWith(
				"biz.webgate.dominoext.poi.pdf.fopdocx4j")) {
			return m_logLevelFOP;
		}
		if (strClass.toLowerCase().startsWith("biz.webgate.dominoext.poi.pdf")) {
			return m_logLevelPDF;
		}
		return m_logLevel;
	}
}
