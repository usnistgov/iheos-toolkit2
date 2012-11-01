package gov.nist.toolkit.directsim;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContactRegistrationDataServer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// For sending reports to
	public String contactAddr;
	
	// From address for Direct requests
	public Map<String, byte[]> directToCertMap = new HashMap<String, byte[]>();
		
	public ContactRegistrationDataServer() {
	
	}
	
	public void add(String direct, byte[] cert) {
		directToCertMap.put(direct, cert);
	}
	
	public byte[] getCert(String fromDirectAddr) {
		return directToCertMap.get(fromDirectAddr);
	}

}
