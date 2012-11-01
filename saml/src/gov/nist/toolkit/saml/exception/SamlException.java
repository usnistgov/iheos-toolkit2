package gov.nist.toolkit.saml.exception;

public class SamlException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resource = null;   // pointer back into documentation 

	public SamlException(String msg, String resource) {
		super(msg);
		this.resource = resource;
	}

	public SamlException(String msg, String resource, Throwable cause) {
		super(msg, cause);
		this.resource = resource;
	}
	
	public String getResource() {
		return resource;
	}
	
	}
