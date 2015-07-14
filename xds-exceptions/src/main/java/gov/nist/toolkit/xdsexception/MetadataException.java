package gov.nist.toolkit.xdsexception;

public class MetadataException extends XdsException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MetadataException(String msg, String resource) {
		super(msg, resource);
	}

	public MetadataException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
