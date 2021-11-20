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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ExportModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final List<ExportColumn> m_Columns = new LinkedList<>();
	private final List<ExportDataRow> m_Rows = new LinkedList<>(); {
	}

	public List<ExportColumn> getColumns() {
		return m_Columns;
	}

	public List<ExportDataRow> getRows() {
		return m_Rows;
	}

	public void addColumnByName(String title, int timeDateFmt, int counter) {
		ExportColumn col = ExportColumn.buildWithName(title, timeDateFmt, counter);
		m_Columns.add(col);
	}

	public void addRow(ExportDataRow dataRow) {
		m_Rows.add(dataRow);

	}

}
