package gov.nist.toolkit.directsim.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DirectRegistrationData  implements IsSerializable  {
	// From address for Direct requests
	// All the real info is in the Contact, this exists
	// for quick validation that the Direct addr
	// should be accepted on the interface 
	// file exists -> ok
	public String directAddr;
	public String contactAddr;
	
	public DirectRegistrationData() {
		
	}
	
	public DirectRegistrationData(String direct, String contact) {
		directAddr = direct;
		contactAddr = contact;
	}
	

}
