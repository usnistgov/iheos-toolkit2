package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsException;

public class XdsDeprecatedException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsDeprecatedException(String msg, String resource) {
		super(msg, resource);
	}

	public XdsDeprecatedException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
