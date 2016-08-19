package gov.nist.toolkit.xdsexception.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class EnvironmentNotSelectedException extends ToolkitRuntimeException implements IsSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnvironmentNotSelectedException() { super("Environment not selected"); }

	public EnvironmentNotSelectedException(String msg) {
		super(msg);
	}

	public EnvironmentNotSelectedException(String msg, Exception e) {
		super(msg, e);
	}

}
