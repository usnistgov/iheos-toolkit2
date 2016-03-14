package gov.nist.toolkit.xdsexception;

public class NoSubmissionSetException extends MetadataException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSubmissionSetException(String msg, String resource) {
		super(msg, resource);
	}

	public NoSubmissionSetException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
