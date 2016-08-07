package gov.nist.toolkit.xdsexception.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;

public class EnvironmentNotSelectedException extends ToolkitRuntimeException implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EnvironmentNotSelectedException(String msg) {
		super(msg);
	}

	public EnvironmentNotSelectedException(String msg, Exception e) {
		super(msg, e);
	}

}
