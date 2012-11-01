package gov.nist.toolkit.xdsexception;

public class XdsValidationException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public XdsValidationException(String reason, String resource) {
		super(reason, resource);
	}


	public XdsValidationException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
