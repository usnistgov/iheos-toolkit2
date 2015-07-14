package gov.nist.toolkit.xdsexception;

public class XdsConfigurationException extends XdsException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XdsConfigurationException(String msg, String resource) {
		super(msg, resource);
	}

	public XdsConfigurationException(String msg, String resource, Throwable cause) {
		super(msg, resource, cause);
	}

}
