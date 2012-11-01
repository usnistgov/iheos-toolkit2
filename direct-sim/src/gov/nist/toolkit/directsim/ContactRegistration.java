package gov.nist.toolkit.directsim;

import gov.nist.toolkit.directsim.client.ContactRegistrationData;

public class ContactRegistration {

	static public ContactRegistrationDataServer toServer(ContactRegistrationData c) {
		ContactRegistrationDataServer d;
		
		d = new ContactRegistrationDataServer();
		d.contactAddr = c.contactAddr;
		d.directToCertMap = c.directToCertMap;
		
		return d;
	}
	
	static public ContactRegistrationData toClient(ContactRegistrationDataServer c) {
		ContactRegistrationData d = new ContactRegistrationData();
		
		d.contactAddr = c.contactAddr;
		d.directToCertMap = c.directToCertMap;
		
		return d;
	}
}
