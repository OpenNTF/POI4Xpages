package biz.webgate.dominoext.poi.component.data.image;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.poi.xwpf.usermodel.Document;


import com.ibm.xsp.complex.ValueBindingObjectImpl;

public class BookmarkImage extends ValueBindingObjectImpl implements IBookmarkImage {

	private String m_Name;
	private Integer m_Id;
	private String m_FileName;
	private Integer m_Width;
	private Integer m_Height;
	private Integer m_Extension;
	private String m_FileType;
	
	public static final String FILETYPE_URL = "URL";
	public static final String FILETYPE_FILE = "FILE";

	public String getName() {
		if (m_Name != null) {
			return m_Name;
		}
		ValueBinding vb = getValueBinding("name");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setName(String name) {
		m_Name = name;
	}
	
	public int getId() {
		if (m_Id != null) {
			return m_Id;
		}
		ValueBinding vb = getValueBinding("id");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		}
		return 0;
	}

	public void setId(int id) {
		m_Id = id;
	}
	
	public String getFileName() {
		if (m_FileName != null) {
			return m_FileName;
		}
		ValueBinding vb = getValueBinding("filename");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}
	
	public void setFileName(String fileName) {
		m_FileName = fileName;
		
		if (m_FileName != null) {
			String extension = m_FileName.substring(m_FileName.lastIndexOf(".")).toUpperCase();
			
			if (extension.equals(".JPG") || extension.equals(".JPEG"))
				_setExtension(Document.PICTURE_TYPE_JPEG);				
			else if (extension.equals(".GIF"))
				_setExtension(Document.PICTURE_TYPE_GIF);				
			else if (extension.equals(".PNG"))
				_setExtension(Document.PICTURE_TYPE_PNG);				
			else if (extension.equals(".BMP"))
				_setExtension(Document.PICTURE_TYPE_BMP);			
		}
		
		if (m_FileName.indexOf("http") > -1)
			_setFileType(FILETYPE_URL);
		else
			_setFileType(FILETYPE_FILE);
	}
	
	public int getWidth() {
		if (m_Width != null) {
			return m_Width;
		}
		ValueBinding vb = getValueBinding("width");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		}
		return 0;
	}
	
	public void setWidth(int width) {
		m_Width = width;
	}
	
	public int getHeight() {
		if (m_Height != null) {
			return m_Height;
		}
		ValueBinding vb = getValueBinding("height");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		}
		return 0;
	}
	
	public void setHeight(int height) {
		m_Height = height;
	}
	
	public int getExtension() {
		if (m_Extension != null) {
			return m_Extension;
		}
		ValueBinding vb = getValueBinding("extension");
		if (vb != null) {
			return (Integer) vb.getValue(getFacesContext());
		}
		return 0;
	}
	
	private void _setExtension(int extension) {
		m_Extension = extension;
	}
	
	public String getFileType() {
		if (m_FileType != null) {
			return m_FileType;
		}
		ValueBinding vb = getValueBinding("filetype");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	private void _setFileType(String fileType) {
		m_FileType = fileType;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		m_Name = (String) state[1];
		m_Id = (Integer) state[2];
		m_Width = (Integer) state[3];
		m_Height = (Integer) state[4];
		m_Extension = (Integer) state[5];
		m_FileType = (String) state[6];
	}
    @Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[7];
		state[0] = super.saveState(context);
		state[1] = m_Name;
		state[2] = m_Id;
		state[3] = m_Width;
		state[4] = m_Height;
		state[5] = m_Extension;
		state[6] = m_FileType;
		return state;

	}
}
