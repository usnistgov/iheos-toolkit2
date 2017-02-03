package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;

public class SchemaValidationException extends XdsInternalException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SchemaValidationException(String msg) {
		super(msg);
	}

	public SchemaValidationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
