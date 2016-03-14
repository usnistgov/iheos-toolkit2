package gov.nist.toolkit.xdsexception;

public class XMLParserException extends XdsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XMLParserException(String msg, String resource) {
		super(msg, resource);
	}

	public XMLParserException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
