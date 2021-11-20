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
package biz.webgate.domino.poi.demodb.poweraction;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.ibm.xsp.component.UIFileuploadEx.UploadedFile;
import com.ibm.xsp.http.IUploadedFile;

public class WorkbookUpload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UploadedFile m_File;
	private List<String> m_SheetNames;
	private UploadedFile last_File;

	public void setFile(UploadedFile file) {
		m_File = file;
	}

	public UploadedFile getFile() {
		return m_File;
	}

	public List<String> getSheetNames() {
		List<String> lstRC = new ArrayList<String>();
		if (m_File != null) {
			if (last_File != m_File) {
				last_File = m_File;
				IUploadedFile FTemp = m_File.getUploadedFile();
				String inputFile = FTemp.getServerFile().getPath();
				ImportAction ioAction = new ImportAction();
				HashMap<String, String> hsCurrent = new HashMap<String, String>();
				hsCurrent.put("FILE", inputFile);
				Workbook wb = ioAction.run(null, hsCurrent);
				if (ioAction.hasError()) {
					Exception exLst = ioAction.getLastException();
					StringWriter swCurrent = new StringWriter();
					PrintWriter pwCurrent = new PrintWriter(swCurrent);
					exLst.printStackTrace(pwCurrent);
					lstRC.add("ERROR !!!!");
					String[] arrErr = swCurrent.toString().split("\n");
					lstRC.addAll(Arrays.asList(arrErr));
				} else {
					for (int nCount = 0; nCount < wb.getNumberOfSheets(); nCount++) {
						lstRC.add(wb.getSheetName(nCount));
					}
				}
				m_SheetNames = lstRC;
			}
		}
		return m_SheetNames;
	}
}
