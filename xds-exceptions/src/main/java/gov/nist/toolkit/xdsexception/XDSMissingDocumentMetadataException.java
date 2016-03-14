package gov.nist.toolkit.xdsexception;

public class XDSMissingDocumentMetadataException extends XdsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XDSMissingDocumentMetadataException(String msg, String resource) {
		super(msg, resource);
	}

	public XDSMissingDocumentMetadataException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}
}
