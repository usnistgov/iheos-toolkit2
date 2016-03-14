package gov.nist.toolkit.xdsexception;

public class XDSRegistryOutOfResourcesException extends Exception {
	
	public XDSRegistryOutOfResourcesException(String msg) {
		super(msg);
	}

	public XDSRegistryOutOfResourcesException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
