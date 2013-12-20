package biz.webgate.dominoext.poi.component.kernel.document;

import java.math.BigInteger;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.document.table.cell.DocCellValue;
import biz.webgate.dominoext.poi.component.data.document.table.cell.DocColumnDefinition;
import biz.webgate.dominoext.poi.component.kernel.DocumentProcessor;
import biz.webgate.dominoext.poi.component.sources.IExportSource;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public class EmbeddedDataSourceExportProcessor implements IDataSourceExportProcessor {
	private static EmbeddedDataSourceExportProcessor m_Processor;

	private EmbeddedDataSourceExportProcessor() {

	}

	public static EmbeddedDataSourceExportProcessor getInstance() {
		if (m_Processor == null) {
			m_Processor = new EmbeddedDataSourceExportProcessor();
		}
		return m_Processor;
	}

	public XWPFTable processExportTable(DocumentTable lstExport, XWPFDocument dxDocument, FacesContext context, String strVar, String strIndex)
			throws POIException {

		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		try {
			logCurrent.finer("Proccess Export Row");
			IExportSource is = lstExport.getDataSource();

			logCurrent.finer("Load Access Source");
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: " + is.getClass() + " Error: " + nAccess);
			}
			int nRow = lstExport.getStartRow();
			int nStepSize = lstExport.getStepSize();

			int nCount = 0;

			logCurrent.finer("Strart Creating Table");
			// CALCULATE NUMBER OF COLUMNS AND ROWS
			int maxCols = lstExport.getDocColumns().size();

			if (lstExport.getDocCellValues() != null && lstExport.getDocCellValues().size() > 0)
			for (DocCellValue dc : lstExport.getDocCellValues()) {
				if (maxCols < dc.getColumnNumber() + 1) {
					maxCols = dc.getColumnNumber() + 1;
				}
			}
			int maxRows = 1;// is.getSize();

			XWPFTable dxTable = dxDocument.createTable(maxRows, maxCols);


			CTTbl ctTbl = dxTable.getCTTbl();

			CTTblPr cpr = ctTbl.getTblPr();
			//cpr.addNewTblW().setW(new BigInteger("100"));
			cpr.getTblW().setW(new BigInteger("5000"));
			cpr.getTblW().setType(STTblWidth.PCT);


			logCurrent.finer("Proccess Create Table - DONE");

			logCurrent.finer("Start Processing Columns");
			int iHeaderRow = 0;
			while (is.accessNextRow() == 1 && nRow < lstExport.getMaxRow() + iHeaderRow) {
				nCount++;
				for (DocColumnDefinition clDef : lstExport.getDocColumns()) {
					int nCol = clDef.getColumnNumber();
					int nMyRow = clDef.getRowShift() + nRow;
					Object objCurrent = is.getValue(clDef, strVar, strIndex, nCount, context);
					if (nCount == 1 && lstExport.getIncludeHeader()) {
						DocumentProcessor.setDocCellValue(dxTable, nMyRow, nCol, clDef.getColumnHeader(), lstExport.getMaxRow(), true);
						DocumentProcessor.setDocCellValue(dxTable, nMyRow + 1, nCol, objCurrent, lstExport.getMaxRow(), false);
						iHeaderRow = 1;
					} else {
						DocumentProcessor.setDocCellValue(dxTable, nMyRow, nCol, objCurrent, lstExport.getMaxRow(), false);
					}
				}
				nRow = nRow + nStepSize;
			}
			is.closeSource();
			logCurrent.finer("Proccess Export Cells - DONE");
			return dxTable;
		} catch (Exception e) {
			throw new POIException("Error in processExportTable", e);
		}
	}

}
