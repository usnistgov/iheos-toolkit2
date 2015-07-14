package gov.nist.toolkit.xdsexception;

import com.google.gwt.user.client.rpc.IsSerializable;

public class XdsException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String resource = null;   // pointer back into documentation 

	public XdsException(String msg, String resource) {
		super(msg);
		this.resource = resource;
	}

	public XdsException(String msg, String resource, Throwable cause) {
		super(msg, cause);
		this.resource = resource;
	}
	
	public String getResource() {
		return resource;
	}
	
	public String getDetails() {
		return ExceptionUtil.exception_details(this);
	}
}
