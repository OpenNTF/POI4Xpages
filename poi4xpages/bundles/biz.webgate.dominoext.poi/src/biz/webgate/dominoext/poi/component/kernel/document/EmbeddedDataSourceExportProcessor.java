package biz.webgate.dominoext.poi.component.kernel.document;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ibm.xsp.util.DataPublisher.ShadowedObject;

import biz.webgate.dominoext.poi.component.data.document.table.DocumentTable;
import biz.webgate.dominoext.poi.component.data.document.table.cell.DocColumnDefinition;
import biz.webgate.dominoext.poi.component.sources.IExportSource;
import biz.webgate.dominoext.poi.util.RequestVarsHandler;
import biz.webgate.dominoext.poi.utils.exceptions.POIException;
import biz.webgate.dominoext.poi.utils.logging.LoggerFactory;

public enum EmbeddedDataSourceExportProcessor implements IDataSourceExportProcessor {
	INSTANCE;

	@Override
	public XWPFTable processExportTable(DocumentTable lstExport, XWPFDocument dxDocument, FacesContext context, String strVar, String strIndex) throws POIException {

		Logger logCurrent = LoggerFactory.getLogger(this.getClass().getCanonicalName());

		try {
			logCurrent.finer("Proccess Export Row");
			IExportSource is = lstExport.getDataSource();

			logCurrent.finer("Load Access Source");
			int nAccess = is.accessSource();
			if (nAccess < 1) {
				throw new POIException("Error accessing Source: " + is.getClass() + " Error: " + nAccess);
			}
			int currentOutputRow = lstExport.getStartRow();
			int nStepSize = lstExport.getStepSize();

			int dataRowCount = 0;

			logCurrent.finer("Strart Creating Table");

			XWPFTable dxTable = dxDocument.getTables().get(lstExport.getTableNr() - 1);

			logCurrent.finer("Proccess Create Table - DONE");

			logCurrent.finer("Start Processing Columns");
			currentOutputRow = buildHeaderIfNeeded(lstExport, currentOutputRow, dxTable);
			List<ShadowedObject> controlData = RequestVarsHandler.INSTANCE.publishControlData(context, strVar, strIndex);

			while (is.accessNextRow() == 1 && currentOutputRow < lstExport.getMaxRow() + (lstExport.getIncludeHeader() ? 1 : 0)) {
				dataRowCount++;
				RequestVarsHandler.INSTANCE.pushVars(context, strVar, strIndex, is.getDataRow(), dataRowCount);

				processRow(lstExport, context, is, currentOutputRow, dxTable);
				currentOutputRow = currentOutputRow + nStepSize;
				RequestVarsHandler.INSTANCE.removeVars(context, strVar, strIndex);

			}
			RequestVarsHandler.INSTANCE.revokeControlData(controlData, context);

			is.closeSource();
			logCurrent.finer("Proccess Export Cells - DONE");
			return dxTable;
		} catch (Exception e) {
			throw new POIException("Error in processExportTable", e);
		}
	}

	private void processRow(DocumentTable lstExport, FacesContext context, IExportSource is, int currentOutputRow, XWPFTable dxTable) {
		for (DocColumnDefinition clDef : lstExport.getDocColumns()) {
			int nCol = clDef.getColumnNumber();
			int nMyRow = clDef.getRowShift() + currentOutputRow;
			Object objCurrent = is.getValue(clDef, context);
			setDocCellValue(dxTable, nMyRow, nCol, objCurrent, lstExport.getMaxRow(), false);
		}
	}

	private int buildHeaderIfNeeded(DocumentTable lstExport, int nRow, XWPFTable dxTable) {
		if (lstExport.getIncludeHeader()) {
			nRow++;
			for (DocColumnDefinition clDef : lstExport.getDocColumns()) {
				setDocCellValue(dxTable, 0, clDef.getColumnNumber(), clDef.getColumnHeader(), lstExport.getMaxRow(), true);
			}
		}
		return nRow;
	}

	public void setDocCellValue(XWPFTable dxTable, int nRow, int nCol, Object objValue, int maxRow, boolean isHeader) {

		try {
			if (dxTable.getRow(nRow) == null) {
				// DEFINIE MAX VALUE!
				while (dxTable.getRow(nRow) == null && dxTable.getRows().size() < maxRow) {
					dxTable.createRow();
					// rowHasChanged = true;
				}
			}
			if (dxTable.getRow(nRow) != null) {
				if (dxTable.getRow(nRow).getCell(nCol) == null) {
					// CHECK MAX COL
					while (dxTable.getRow(nRow).getCell(nCol) == null && dxTable.getRow(nRow).getTableCells().size() < 50) {
						dxTable.getRow(nRow).addNewTableCell();
					}
				}

				for (XWPFParagraph paraCurrent : dxTable.getRow(nRow).getCell(nCol).getParagraphs()) {
					if (paraCurrent.getRuns().size() == 0) {
						XWPFRun runCurrent = paraCurrent.createRun();
						if (isHeader)
							runCurrent.setBold(true);
						runCurrent.setText("" + objValue.toString());
					} else {
						for (XWPFRun runCurrent : paraCurrent.getRuns()) {
							if (isHeader)
								runCurrent.setBold(true);
							runCurrent.setText("" + objValue.toString());
						}
					}
				}

			} else {
				System.out.println("Still null: " + nRow + " MaxRow = " + maxRow);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
