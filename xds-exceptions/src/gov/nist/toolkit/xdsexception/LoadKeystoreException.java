package gov.nist.toolkit.xdsexception;

public class LoadKeystoreException extends XdsException {

	private static final long serialVersionUID = 1L;

	public LoadKeystoreException(String msg, String resource) {
		super(msg, resource);
	}

	public LoadKeystoreException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}
}
