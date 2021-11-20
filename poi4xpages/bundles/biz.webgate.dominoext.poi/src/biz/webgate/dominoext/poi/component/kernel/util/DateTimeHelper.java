package biz.webgate.dominoext.poi.component.kernel.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.faces.context.FacesContext;

import com.ibm.xsp.designer.context.XSPContext;

public class DateTimeHelper implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat m_DFDate;
	private SimpleDateFormat m_DFDateTime;
	private SimpleDateFormat m_DFTime;

	public DateTimeHelper() {
		super();
		try {
			Locale loc = Locale.getDefault();
			FacesContext fc = FacesContext.getCurrentInstance();
			if (fc != null) {
				XSPContext xspC = XSPContext.getXSPContext(fc);
				if (xspC != null) {
					loc = xspC.getLocale();
				}
			}
			m_DFDate = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, loc);
			m_DFDateTime = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, loc);
			m_DFTime = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, loc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimpleDateFormat getDFDate() {
		return m_DFDate;
	}

	public SimpleDateFormat getDFDateTime() {
		return m_DFDateTime;
	}

	public SimpleDateFormat getDFTime() {
		return m_DFTime;
	}

}
