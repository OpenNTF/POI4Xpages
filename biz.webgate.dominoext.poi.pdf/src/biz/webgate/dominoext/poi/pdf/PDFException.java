package biz.webgate.dominoext.poi.pdf;

public class PDFException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PDFException() {
		super();
	}
	public PDFException(String strMessage) {
		super(strMessage);
	}
	public PDFException(Throwable t) {
		super(t);
	}
	public PDFException(String strMessage,Throwable t) {
		super(strMessage,t);
	}
}
