package gov.nist.toolkit.saml.exception;

public class SamlInternalException extends SamlException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public SamlInternalException(String reason) {
		super(reason, "Internal Error");
	}


	public SamlInternalException(String msg, Throwable cause) {
		super(msg,  "Internal Error", cause);
	}
}
