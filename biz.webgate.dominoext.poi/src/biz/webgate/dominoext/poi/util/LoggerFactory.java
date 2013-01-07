package biz.webgate.dominoext.poi.util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
	private static HashMap<String, Logger> m_RegistredLoggers = new HashMap<String, Logger>();

	private static int m_logLevel = -1;

	public static Logger getLogger(String strName) {
		try {
			if (m_RegistredLoggers.containsKey(strName)) {
				return m_RegistredLoggers.get(strName);
			}
			Logger logRC = java.util.logging.Logger.getAnonymousLogger();
			if (m_logLevel == -1) {
				checkLogLevel();
			}
			logRC.setLevel(getLogLevel(m_logLevel));
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
			String strPOI = POILibUtil.getCurrentSession()
					.getEnvironmentString("DEBUG_POI", true);
			if (strPOI == null || "".equals(strPOI)) {
				m_logLevel = 1;
				return;
			}
			System.out.println("POI LOG-LEVEL is set to: " + strPOI);
			try {
				m_logLevel = Integer.parseInt(strPOI);
			} catch (Exception e) {
				m_logLevel = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
