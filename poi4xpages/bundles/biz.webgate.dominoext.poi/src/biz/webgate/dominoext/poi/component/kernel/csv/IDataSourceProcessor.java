/*
 * � Copyright WebGate Consulting AG, 2013
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

package biz.webgate.dominoext.poi.component.kernel.csv;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.csv.CSVPrinter;

import biz.webgate.dominoext.poi.component.containers.UICSV;
import biz.webgate.dominoext.poi.component.data.csv.CSVColumn;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;

public interface IDataSourceProcessor {

	public void process(List<CSVColumn> lstColumns, UICSV csvDef,
			CSVPrinter csvPrinter, FacesContext context) throws POIException;
}
