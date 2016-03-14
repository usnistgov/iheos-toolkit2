package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BadSimIdException extends RuntimeException implements IsSerializable {

	public BadSimIdException(String string) {
		super(string);
	}

	public BadSimIdException() {}

	/**
	 * 
	 */
	private static final long serialVersionUID = -624784677016763640L;

}
