/*
 * � Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi.renderkit.html_extended;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.renderkit.FacesRenderer;

public class WorkbookRenderer extends FacesRenderer {
	@Override
	public void decode(FacesContext context, UIComponent component) {
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) {
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
	}
}
