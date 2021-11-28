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
package biz.webgate.dominoext.poi.util.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import biz.webgate.dominoext.poi.util.AbstractPOIPowerActionExtended;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

public class DocX2HTMLAction extends AbstractPOIPowerActionExtended<OutputStream, InputStream> {

	@Override
	protected OutputStream doItPriv(InputStream inputObj, OutputStream outputStream, HashMap<String, String> hsCurrent) throws Exception {
		XWPFDocument document = new XWPFDocument(inputObj); // createDocument(is);

		XHTMLOptions options = XHTMLOptions.create(); // createOptions();

		OutputStream out = new ByteArrayOutputStream();// new
		if (outputStream != null) {
			out = outputStream;
		}

		XHTMLConverter.getInstance().convert(document, out, options);

		out.flush();
		return out;
	}

}
