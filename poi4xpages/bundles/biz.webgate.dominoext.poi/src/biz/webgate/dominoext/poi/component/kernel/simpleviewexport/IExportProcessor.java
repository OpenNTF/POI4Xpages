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
package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.servlet.http.HttpServletResponse;

import biz.webgate.dominoext.poi.component.containers.UISimpleViewExport;
import biz.webgate.dominoext.poi.component.kernel.util.DateTimeHelper;

public interface IExportProcessor {

	public void process2HTTP(ExportModel expModel, UISimpleViewExport uis, HttpServletResponse hsr, DateTimeHelper dth, boolean noDownload, MethodBinding preDownload, FacesContext context);
}
