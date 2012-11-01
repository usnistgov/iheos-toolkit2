package gov.nist.toolkit.directsim;

import gov.nist.toolkit.directsim.client.ContactRegistrationData;

public class DirectUser {

	ContactRegistrationData reg;
	
	public DirectUser(ContactRegistrationData reg) {
		this.reg = reg;
	}
	
	public ContactRegistrationData registrationData() {
		return reg;
	}
	
	public DirectEmailAddr contactFilename() {
		return new DirectEmailAddr(reg.contactAddr);
	}

}
