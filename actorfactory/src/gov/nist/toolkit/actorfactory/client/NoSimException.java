package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoSimException extends Exception implements IsSerializable {

	public NoSimException(String string) {
		super(string);
	}
	
	public NoSimException() {}

	/**
	 * 
	 */
	private static final long serialVersionUID = -624784677016763640L;

}
