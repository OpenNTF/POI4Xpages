/*
 * © Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi.library;

import com.ibm.xsp.library.AbstractXspLibrary;

public class POILibrary extends AbstractXspLibrary {

	public String getLibraryId() {
		return "biz.webgate.dominoext.poi.library";
	}

	@Override
	public String[] getFacesConfigFiles() {
		String[] files = new String[] { "META-INF/poi-beans-faces-config.xml",
				"META-INF/poi-csv-faces-config.xml",
				"META-INF/poi-spreadsheet-faces-config.xml",
				"META-INF/poi-document-faces-config.xml",
				"META-INF/poi-sve-faces-config.xml"};
		return files;
	}

	@Override
	public String getPluginId() {
		return "biz.webgate.dominoext.poi";
	}

	@Override
	public String[] getXspConfigFiles() {
		return new String[] { "META-INF/poi-sources.xsp-config",
				"META-INF/poi-wb-iexport.xsp-config",
				"META-INF/poi-wb-icellvalue.xsp-config",
				"META-INF/poi-wb-listexporter.xsp-config",
				"META-INF/poi-wb.xsp-config",
				"META-INF/poi-document.xsp-config",
				"META-INF/poi-csv.xsp-config",
				"META-INF/poi-sve.xsp-config"};
	}

	@Override
	public String getTagVersion() {
		return "1.2.6";
	}
}
