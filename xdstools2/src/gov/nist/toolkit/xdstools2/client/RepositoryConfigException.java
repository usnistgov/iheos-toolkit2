package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.repository.api.RepositoryException;

public class RepositoryConfigException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2910306789199740013L;
	
	
	public RepositoryConfigException() {}
	

	public RepositoryConfigException(String msg) {
		super(msg);
	}

	public RepositoryConfigException(String msg, Exception e) {
		super(msg, e);
	}


}
