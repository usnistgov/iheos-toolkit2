package gov.nist.toolkit.xdsexception;

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
