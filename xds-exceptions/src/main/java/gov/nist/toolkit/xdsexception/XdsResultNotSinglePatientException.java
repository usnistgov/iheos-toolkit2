package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsException;

public class XdsResultNotSinglePatientException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public XdsResultNotSinglePatientException(String reason, String resource) {
		super(reason, resource);
	}


	public XdsResultNotSinglePatientException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}
}
