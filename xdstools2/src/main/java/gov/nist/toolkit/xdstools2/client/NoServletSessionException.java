package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoServletSessionException extends Exception  implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoServletSessionException(String msg) {
		super(msg);
	}

	public NoServletSessionException() {}

}
