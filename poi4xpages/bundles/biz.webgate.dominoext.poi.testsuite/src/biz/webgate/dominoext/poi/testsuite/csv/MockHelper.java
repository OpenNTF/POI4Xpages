package biz.webgate.dominoext.poi.testsuite.csv;

import java.util.Date;
import java.util.Vector;

import org.easymock.EasyMock;

import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

public class MockHelper {

	public static View buildMockView() throws NotesException {
		View view = EasyMock.createNiceMock(View.class);
		EasyMock.expect(view.getColumns()).andReturn(buildColumns());
		EasyMock.replay(view);
		return view;
	}

	public static Vector<ViewColumn> buildColumns() throws NotesException {
		Vector<ViewColumn> columns = new Vector<>();
		columns.add(buildSingleColumn(false, "Lastname", 0));
		columns.add(buildSingleColumn(false, "Firstname", 0));
		columns.add(buildSingleColumn(true, "icon", 0));
		columns.add(buildSingleColumn(false, "Birthday", ViewColumn.FMT_DATE));
		return columns;
	}

	public static ViewColumn buildSingleColumn(boolean isHidden, String title, int dateFormat) throws NotesException {
		ViewColumn col = EasyMock.createNiceMock(ViewColumn.class);
		EasyMock.expect(col.isHidden()).andReturn(isHidden);
		EasyMock.expect(col.getTitle()).andReturn(title);
		EasyMock.expect(col.getTimeDateFmt()).andReturn(dateFormat);
		col.recycle();
		EasyMock.replay(col);
		return col;
	}

	@SuppressWarnings("deprecation")
	public static ViewEntryCollection buildViewEntryCollection() throws NotesException {
		ViewEntryCollection collection = EasyMock.createNiceMock(ViewEntryCollection.class);
		EasyMock.expect(collection.getFirstEntry()).andReturn(buildViewEntry("Ren�", "M�ller", 80, new Date(1978, 3, 22)));
		EasyMock.expect(collection.getNextEntry()).andReturn(buildViewEntry("Peter", "Pan", 102, new Date(1974, 6, 11)));
		EasyMock.expect(collection.getNextEntry()).andReturn(buildViewEntry("G�rald", "R�misberger", 66, new Date(1982, 1, 12)));
		EasyMock.expect(collection.getNextEntry()).andReturn(null);
		EasyMock.replay(collection);
		return collection;
	}

	public static ViewEntry buildViewEntry(String firstName, String lastName, int weight, Date birthday) throws NotesException {
		ViewEntry entry = EasyMock.createNiceMock(ViewEntry.class);
		DateTime dt = EasyMock.createNiceMock(DateTime.class);
		EasyMock.expect(dt.toJavaDate()).andReturn(birthday);
		dt.recycle();
		EasyMock.replay(dt);
		Vector<Object> values = new Vector<>();
		values.add(firstName);
		values.add(lastName);
		values.add(weight);
		values.add(dt);
		EasyMock.expect(entry.getColumnValues()).andReturn(values);
		EasyMock.expect(entry.getUniversalID()).andReturn(firstName+lastName+weight);
		entry.recycle();
		EasyMock.replay(entry);
		return entry;
	}

}
