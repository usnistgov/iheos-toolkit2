package gov.nist.toolkit.dst.cmd.selectables;

import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.io.File;
import java.util.List;

public class SiteLoader {
	static Sites sites = null;
	static List<String> siteNames = null;
	
	public SiteLoader() {}
	
	public SiteLoader(File actorsFile) throws Exception {
		CombinedSiteLoader sl = new CombinedSiteLoader();
		sites = sl.load(actorsFile, null);
		siteNames = sites.getSiteNames();
	}

	public List<String> getSiteNames() { return siteNames; }
	
	public Site getSite(String name) throws Exception {
		return sites.getSite(name);
	}
	
}
