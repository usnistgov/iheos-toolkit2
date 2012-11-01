package gov.nist.toolkit.sitemanagement.client;

import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TransactionOfferings implements IsSerializable {
	// regular and tls
	public Map<TransactionType, List<Site>> map = new HashMap<TransactionType, List<Site>>();
	public Map<TransactionType, List<Site>> tmap = new HashMap<TransactionType, List<Site>>();
	
	public boolean hasTransaction(TransactionType tt, boolean isTLS) {
		if (isTLS) {
			return tmap.containsKey(tt);
		} else {
			return map.containsKey(tt);
		}
	}
	
	public TransactionOfferings() {} // For GWT

}
