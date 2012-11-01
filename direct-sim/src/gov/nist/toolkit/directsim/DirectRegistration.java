package gov.nist.toolkit.directsim;

import gov.nist.toolkit.directsim.client.DirectRegistrationData;

public class DirectRegistration {

	static public DirectRegistrationDataServer toServer(DirectRegistrationData r) {
		DirectRegistrationDataServer d;
	
		d = new DirectRegistrationDataServer();
		d.contactAddr = r.contactAddr;
		d.directAddr = r.directAddr;
		
		return d;
	}

}
