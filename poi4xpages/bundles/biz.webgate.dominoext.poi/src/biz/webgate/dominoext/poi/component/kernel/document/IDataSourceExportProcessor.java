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
package biz.webgate.dominoext.poi.component.kernel.document;

import javax.faces.context.FacesContext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;


public interface IDataSourceExportProcessor {
	XWPFTable processExportTable(DocumentTable lstExport, XWPFDocument dxDocument, FacesContext context, String strVar, String strIndex) throws POIException;
}