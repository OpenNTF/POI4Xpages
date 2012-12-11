package biz.webgate.dominoext.poi;

public class POIException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public POIException() {
		super();
	}
	public POIException(String strMessage) {
		super(strMessage);
	}
	public POIException(Throwable t) {
		super(t);
	}
	public POIException(String strMessage,Throwable t) {
		super(strMessage,t);
	}
}
