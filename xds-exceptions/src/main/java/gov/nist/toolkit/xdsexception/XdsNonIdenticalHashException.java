package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.MetadataValidationException;

public class XdsNonIdenticalHashException extends MetadataValidationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsNonIdenticalHashException(String msg, String resource) {
		super(msg, resource);
	}

	public XdsNonIdenticalHashException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}


}
