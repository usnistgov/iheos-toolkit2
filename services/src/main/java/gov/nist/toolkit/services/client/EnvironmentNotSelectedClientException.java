package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnvironmentNotSelectedClientException extends Exception implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnvironmentNotSelectedClientException() {}

	public EnvironmentNotSelectedClientException(String msg) {
		super(msg);
	}

	public EnvironmentNotSelectedClientException(String msg, Exception e) {
		super(msg, e);
	}

}
