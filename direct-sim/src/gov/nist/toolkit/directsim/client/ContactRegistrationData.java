package gov.nist.toolkit.directsim.client;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ContactRegistrationData implements Serializable, IsSerializable {

	/**
	 * This object is serialized as external_cache/direct/contact/name
	 * where name is the value of contactAddr below.  So, given the 
	 * contactAddr, each directAddr can be retrieved.  In the sibling directory
	 * external_cache/direct/direct/name name refers to the directAddr.  
	 * The contents of this file is the contactAddr that the directAddr
	 * belongs to. The names listed in this directory (direct) are used
	 * as the White List of email addresses from which to accept Direct
	 * requests from.  The contactAddr is the responsible human that gets the
	 * validation report email after the validation is performed.
	 * 
	 * From this we can map back and forth between a contactAddr and the 
	 * directAddress that it uses.  
	 * 
	 * All this I/O is performed by the class DirectUserManager
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// For sending reports to
	public String contactAddr;
	
	// From address for Direct requests
	public Map<String, byte[]> directToCertMap = new HashMap<String, byte[]>();
		
	public ContactRegistrationData() {
	
	}
	
	public void add(String direct, byte[] cert) {
		directToCertMap.put(direct, cert);
	}
	
	public byte[] getCert(String fromDirectAddr) {
		return directToCertMap.get(fromDirectAddr);
	}
	
}
