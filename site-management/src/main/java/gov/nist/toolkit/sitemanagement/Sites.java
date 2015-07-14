package gov.nist.toolkit.sitemanagement;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionCollection;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Sites {
	HashMap<String, Site> siteMap = new HashMap<String, Site>();   // siteName -> Site
	String defaultSiteName;
	static String all = "allRepositories";
	
	public boolean equals(Sites s) {
		if (s == null)
			return false;
		HashMap<String, Site> map1 = new HashMap<String, Site>(siteMap);
		HashMap<String, Site> map2 = new HashMap<String, Site>(s.siteMap);
		map1.remove(all);
		map2.remove(all);
		boolean mapped = equals(map1, map2);
		
		return
				((defaultSiteName == null) ? s.defaultSiteName == null : defaultSiteName.equals(s.defaultSiteName)) &&
				mapped;
	}
	
	public int size() {
		return siteMap.size();
	}
	
	boolean equals(HashMap<String, Site> map1, HashMap<String, Site> map2) {
		if (map1 == null)
			if (map2 == null)
				return true;
		if (map1 == null)
			return false;
		if (map2 == null)
			return false;
		if (map1.size() != map2.size())
			return false;
		for (String name : map1.keySet()) {
			if (!map2.containsKey(name))
				return false;
			if (!map1.get(name).equals(map2.get(name)))
				return false;
		}
		return true;
	}
	
	public Sites clone() {
		Sites s = new Sites();
		s.siteMap = new HashMap<String, Site>();
		for (String key: siteMap.keySet()) {
			Site value = siteMap.get(key);
			s.siteMap.put(key, value);
		}
		s.defaultSiteName = defaultSiteName;
		
		return s;
	}
	
	public HashMap<String, Site> getSiteMap() {
		return siteMap;
	}
	
	public Sites getAllSites() {
		return new Sites(siteMap.values());
	}
	
	public Collection<Site> asCollection() {
		return siteMap.values();
	}
	
	public void validate(StringBuffer buf) {
		for (Site s : asCollection()) {
			s.validate(buf);
		}
	}
	
	public Sites() {
		
	}
	
	public Sites(Site site) {
		siteMap.put(site.getName(), site);
	}
	
	public Sites(Collection<Site> sites) {
		for (Site site : sites) {
			siteMap.put(site.getName(), site);
		}
	}
	
	public void add(Site site) {
		siteMap.put(site.getName(), site);
	}

	public void add(Sites sites) {
		for (Site s : sites.siteMap.values()) {
			add(s);
		}
	}

	public void deleteSite(String siteName) {
		siteMap.remove(siteName);
	}
	
	public String getAllRepositoriesSiteName() { // This string is present in ActorConfigTab.java as well (client)
		return "allRepositories";
	}

	public Site getAllRepositoriesSite() {
		return siteMap.get(getAllRepositoriesSiteName());
	}

	// The special site "allRepositories" is created dynamically at run time
	// and holds the repositoryUniqueId => endpoint mappings for all
	// repositories in the configuration.  This allows any repository
	// to be targetted at a Connectathon. The GUI tool xdstools2 knows
	// about this special site.
	public void buildRepositoriesSite() throws XdsException {
		Site repSite = new Site(getAllRepositoriesSiteName());
		TransactionCollection repSiteReps = repSite.repositories();
		for (String siteName : siteMap.keySet()) {
			Site site = siteMap.get(siteName);
			TransactionCollection reps = site.repositories();
			for (TransactionBean t : reps.transactions ) {
				repSiteReps.transactions.add(t);
			}
		}
		siteMap.put(getAllRepositoriesSiteName(), repSite);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("[Sites]\n");
		buf.append("default site : ");
		buf.append(defaultSiteName);
		buf.append("\n");
		for (Iterator<String> it=siteMap.keySet().iterator(); it.hasNext(); ) {
			String name = it.next();
			Site site = siteMap.get(name);
			buf.append("\t");
			buf.append(name);
			buf.append(" = \n");
			buf.append(site.toString());
		}
		buf.append("[[Sites]]");

		return buf.toString();
	}
	
	public List<Site> getSitesWithActor(ActorType actorType) throws Exception {
		List<Site> rs = new ArrayList<Site>();

		for (String siteName : siteMap.keySet()) {
			Site site = getSite(siteName);
			if (site.hasActor(actorType))
				rs.add(site);
		}

		return rs;
		
	}
	
	public List<Site> getSitesWithTransaction(TransactionType tt) throws Exception {
		List<Site> rs = new ArrayList<Site>();

		for (String siteName : siteMap.keySet()) {
			Site site = getSite(siteName);
			if (site.hasTransaction(tt))
				rs.add(site);
		}

		return rs;
		
	}
	
	public Site getSiteForHome(String home) {
		for (Site site : siteMap.values()) {
			if (home.equals(site.getHome()))
				return site;
		}
		return null;
	}

	public List<String> getSiteNamesWithActor(ActorType actorType) throws Exception {
		List<String> rs = new ArrayList<String>();
				
		for (Site s : getSitesWithActor(actorType)) {
			rs.add(s.getName());
		}

		return rs;
	}

	public List<String> getSiteNamesWithTransaction(TransactionType tt) throws Exception {
		List<String> rs = new ArrayList<String>();
				
		for (Site s : getSitesWithTransaction(tt)) {
			rs.add(s.getName());
		}

		return rs;
	}

	public List<String> getSiteNamesWithRepository() throws Exception {
		List<String> rs = new ArrayList<String>();

		for (String siteName : siteMap.keySet()) {
			Site site = getSite(siteName);
			if (site.hasRepositoryB()) 
				rs.add(siteName);
		}

		return rs;
	}

	public List<String> getSiteNames() {
		List<String> lst = new ArrayList<String>();
		lst.addAll(siteMap.keySet());
		return lst;
	}

	public Site getDefaultSite() throws Exception {
		if (defaultSiteName == null || defaultSiteName.equals(""))
			throw new Exception("No Default Site");
		return getSite(defaultSiteName);
	}

	public Site getSite(String siteName) throws Exception {
		if (siteName == null)
			throw new Exception("Internal error: null site requested");
		List<String> sitenames = getSiteNames();
		if ( !sitenames.contains(siteName))
			throw new Exception("Site [" + siteName + "] is not defined");
		Site s = siteMap.get(siteName);
		return s;
	}

	public void setSites(HashMap<String, Site> sites) {
		this.siteMap = sites;
	}
	
	public void setDefaultSite(String name) {
		defaultSiteName = name;
	}


	public void putSite(Site s) {
		siteMap.put(s.getName(), s);
	}
	
	boolean isEmpty(String x) {
		if (x == null)
			return true;
		if (x.equals(""))
			return true;
		return false;
	}

//	public Sites getSites(Collection<SimulatorConfig> simConfigs) throws Exception {
//		Collection<Site> origSites = asCollection();
//		Collection<Site> scs = new ArrayList<Site>();
//		scs.addAll(origSites);
//		scs.addAll(SimManager.getSites(simConfigs));
//		return new Sites(scs);
//	}

	public HashMap<String, Site> getSites() {
		return siteMap;
	}

	public String getDefaultSiteName() {
		return defaultSiteName;
	}


}
