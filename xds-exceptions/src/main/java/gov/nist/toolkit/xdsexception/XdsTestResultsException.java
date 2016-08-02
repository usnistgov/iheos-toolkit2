package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;

public class XdsTestResultsException extends XdsInternalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsTestResultsException(String reason) {
		super(reason);
	}

}
