package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsException;

public class XdsPatientIdDoesNotMatchException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsPatientIdDoesNotMatchException(String message, String resource) {
		super(message, resource);
	}

	public XdsPatientIdDoesNotMatchException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}
}
