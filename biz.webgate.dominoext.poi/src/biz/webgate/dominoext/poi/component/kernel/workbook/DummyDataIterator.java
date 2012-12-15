package biz.webgate.dominoext.poi.component.kernel.workbook;

import javax.faces.model.DataModel;

public class DummyDataIterator implements com.ibm.xsp.component.FacesDataIterator{

    public DataModel getDataModel() {
        return null;
    }

    public int getFirst() {
        return 0;
    }

    public int getRowIndex() {
        return 0;
    }

    public int getRows() {
        return 0;
    }

    public void setFirst(int paramInt) {}

    public void setRows(int paramInt) {}
}
