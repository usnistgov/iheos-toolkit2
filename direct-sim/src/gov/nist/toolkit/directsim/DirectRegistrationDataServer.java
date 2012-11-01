package gov.nist.toolkit.directsim;

import java.io.Serializable;

public class DirectRegistrationDataServer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String directAddr;
	public String contactAddr;

	public DirectRegistrationDataServer() {
		
	}
	
	public DirectRegistrationDataServer(String direct) {
		directAddr = direct;
	}
	
	public DirectRegistrationDataServer(DirectRegistrationDataServer d) {
		directAddr = d.directAddr;
		contactAddr = d.contactAddr;
	}

}
