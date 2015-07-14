package gov.nist.toolkit.xdsexception;

public class XdsFormatException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsFormatException(String msg, String resource) {
		super(msg, resource);
	}

	public XdsFormatException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}


}
