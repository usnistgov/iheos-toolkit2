package gov.nist.toolkit.sitemanagement.client;


import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.util.*;

/**
 * This is as close as toolkit comes to managing Actor configurations. ATFactory
 * defines the linkage between Transactions and Actors, this holds the 
 * transaction configurations (actual endpoints).
 * @author bill
 *
 */
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
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("TransactionOfferings:\n");
		buf.append("Non-TLS\n");
		for (TransactionType t : map.keySet()) {
			buf.append("\t").append(t).append("\n");
		}
		buf.append("TLS\n");
		for (TransactionType t : tmap.keySet()) {
			buf.append("\t").append(t).append("\n");
		}
		
		return buf.toString();
	}
	
	public TransactionOfferings() {} // For GWT

	public List<Site> getAllSites() {
		List<Site> sites = new ArrayList<>();

		Set<Site> siteSet = new HashSet<>();
		for (List<Site> aList : map.values()) {
			siteSet.addAll(aList);
		}
		for (List<Site> aList : tmap.values()) {
			siteSet.addAll(aList);
		}
		sites.addAll(siteSet);

		List<String> names = new ArrayList<>();
		for (Site s : sites) names.add(s.getName());
		names = StringSort.sort(names);

		List<Site> sortedSites = new ArrayList<>();
		for (String name : names) {
			sortedSites.add(getSite(sites, name));
		}

		return sortedSites;
	}

	private static Site getSite(List<Site> sites, String name) {
		for (Site site : sites) {
			if (name.equals(site.getName())) return site;
		}
		return null;
	}

}
