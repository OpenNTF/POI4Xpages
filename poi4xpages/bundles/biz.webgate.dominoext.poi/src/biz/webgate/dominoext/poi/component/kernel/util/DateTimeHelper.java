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
