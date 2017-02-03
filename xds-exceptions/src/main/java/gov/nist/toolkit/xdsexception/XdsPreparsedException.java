package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;

public class XdsPreparsedException extends XdsInternalException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public XdsPreparsedException(String msg) {
		super(msg);
	}
	public XdsPreparsedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
