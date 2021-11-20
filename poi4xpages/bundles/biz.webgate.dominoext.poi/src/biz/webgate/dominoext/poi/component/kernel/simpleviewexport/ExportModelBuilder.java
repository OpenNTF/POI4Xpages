package biz.webgate.dominoext.poi.component.kernel.simpleviewexport;

import java.util.Vector;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

public enum ExportModelBuilder {
	INSTANCE;
	public ExportModel buildFromView(View view) throws NotesException {
		ExportModel model = new ExportModel();
		@SuppressWarnings("unchecked")
		Vector<ViewColumn> vecColumnns = view.getColumns();
		int counter = 0;
		for (ViewColumn colum : vecColumnns) {
			if (!colum.isHidden()) {
				model.addColumnByName(colum.getTitle(), colum.getTimeDateFmt(), counter);
			}
			colum.recycle();
			counter++;
		}
		return model;
	}

	public void applyRowData(ExportModel model, ViewEntryCollection collection) throws NotesException {
		ViewEntry nextEntry = collection.getFirstEntry();

		while (nextEntry != null) {
			ViewEntry entry = nextEntry;
			nextEntry = collection.getNextEntry();
			@SuppressWarnings("unchecked")
			Vector<Object> values = entry.getColumnValues();
			ExportDataRow dataRow = ExportDataRow.buildDataRow(entry.getUniversalID());
			for (ExportColumn column : model.getColumns()) {
				if (column.getPosition() < values.size()) {
					dataRow.addValue(column.getPosition(), values.get(column.getPosition()));
				}
			}
			model.addRow(dataRow);
			entry.recycle();
		}

	}

}
