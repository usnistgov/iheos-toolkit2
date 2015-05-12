package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.common.adt.AdtRecordBean;

import com.sun.ebxml.registry.util.UUID;
import com.sun.ebxml.registry.util.UUIDFactory;

public class PidGenerator {

    private int uuidCharsToUse = 15;
    
    public static final String PATIENT_ID_DOMAIN_SEPARATOR = "^^^";
    
    String domain;

    public PidGenerator(String assigningAuthority) {
    	domain = "&" + assigningAuthority + "&ISO";
    }
    
	public String get() {
		UUID uuid = UUIDFactory.getInstance().newUUID();
		return firstNChars(stripUuid(uuid.toString()),uuidCharsToUse) + AdtRecordBean.PATIENT_ID_DOMAIN_SEPARATOR + domain;
	}

    public String stripUuid(String uuid) {
        String withoutColons = uuid.substring(uuid.lastIndexOf(':') + 1);
        StringBuffer withoutHyphens = new StringBuffer();
        for(int i = 0; i < withoutColons.length(); i++) {
            if(withoutColons.charAt(i) != '-')
                withoutHyphens.append(withoutColons.charAt(i));
        }
        return withoutHyphens.toString();
    }
    
    public String firstNChars(String str, int n) {
    	return str.substring(0, n);
    }

}
