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
package biz.webgate.domino.poi.demodb.poweraction;

import java.io.File;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import biz.webgate.dominoext.poi.util.AbstractPOIPowerAction;

public class ImportAction extends AbstractPOIPowerAction<Workbook> {

	@Override
	protected Workbook doItPriv(Workbook arg0, HashMap<String, String> hsCurrent) throws Exception {
		System.out.println("Accessing -> " + hsCurrent.get("FILE"));
		File excelFile = new java.io.File(hsCurrent.get("FILE"));
		Workbook wb2 = WorkbookFactory.create(excelFile);
		System.out.println("###Got the file and first sheetName is -> " + wb2.getSheetName(0));
		return wb2;
	}
}
