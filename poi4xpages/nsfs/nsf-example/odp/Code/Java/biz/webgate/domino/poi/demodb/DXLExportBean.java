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
package biz.webgate.domino.poi.demodb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Document;
import lotus.domino.DxlExporter;
import lotus.domino.NoteCollection;

import com.ibm.domino.services.HttpServiceConstants;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.extlib.component.rest.CustomService;
import com.ibm.xsp.extlib.component.rest.CustomServiceBean;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class DXLExportBean extends CustomServiceBean {

	@Override
	public void renderService(CustomService cs, RestServiceEngine rest) throws ServiceException {
		HttpServletRequest request = rest.getHttpRequest();
		HttpServletResponse response = rest.getHttpResponse();

		String action = request.getParameter("action");
		String id = request.getParameter("id");
		if ("DOCUMENT".equalsIgnoreCase(action)) {
			processDocument(id, response);
		}
		if ("DB".equalsIgnoreCase(action)) {
			processDB(id, response);
		}

	}

	private void processDocument(String id, HttpServletResponse response) {
		response.setContentType(HttpServiceConstants.CONTENTTYPE_TEXT_XML_UTF8);
		try {
			Document doc = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(id);
			DxlExporter exp = ExtLibUtil.getCurrentSession().createDxlExporter();
			exp.setConvertNotesBitmapsToGIF(true);
			response.getWriter().print(exp.exportDxl(doc));
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processDB(String id, HttpServletResponse response) {
		response.setContentType(HttpServiceConstants.CONTENTTYPE_TEXT_XML_UTF8);
		try {
			NoteCollection nc = ExtLibUtil.getCurrentDatabase().createNoteCollection(false);
			nc.selectAllDesignElements(true);
			nc.buildCollection();
			DxlExporter exp = ExtLibUtil.getCurrentSession().createDxlExporter();
			response.getWriter().print(exp.exportDxl(nc));
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
