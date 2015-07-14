package gov.nist.toolkit.xdsexception;

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
