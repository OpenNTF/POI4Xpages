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
package biz.webgate.dominoext.poi.utils.logging;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import biz.webgate.dominoext.poi.utils.LibUtil;

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
			pw.println("POI LIB  : " + LibUtil.getPOILibUtilVersion());
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
