package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.MetadataException;

public class NoMetadataException extends MetadataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoMetadataException(String msg, String resource) {
		super(msg, resource);
	}

	public NoMetadataException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
