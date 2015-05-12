package gov.nist.toolkit.xdsexception;

public class HttpCodeException extends XdsException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HttpCodeException(String msg, String resource) {
		super(msg, resource);
	}

	public HttpCodeException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
