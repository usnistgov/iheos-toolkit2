package gov.nist.toolkit.actortransaction.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class BadSimIdException extends RuntimeException implements Serializable, IsSerializable {

	public BadSimIdException(String string) {
		super(string);
	}

	public BadSimIdException() {}

	/**
	 * 
	 */
	private static final long serialVersionUID = -624784677016763640L;

}
