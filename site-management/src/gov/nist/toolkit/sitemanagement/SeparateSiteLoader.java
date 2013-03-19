package gov.nist.toolkit.sitemanagement;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;

import java.io.File;

import org.apache.axiom.om.OMElement;

public class SeparateSiteLoader extends SiteLoader {

	public Sites load(OMElement conf, Sites sites) throws Exception {
		parseSite(conf);
		
		if (sites == null)
			sites = new Sites();
		
		sites.setSites(siteMap);
		sites.buildRepositoriesSite();
		
		return sites;
	}

	public Sites load(File actorsDir, Sites sites) throws Exception {
		if (!actorsDir.isDirectory())
			throw new Exception("Cannot load actor descriptions: " +
					actorsDir + " is not a directory");
		for (File file : actorsDir.listFiles()) {
			if (!file.getName().endsWith("xml"))
				continue;
			OMElement conf = Util.parse_xml(file);
			if (sites == null)
				sites = new Sites();
			sites = load(conf, sites);
		}
		return sites;
	}
	
	public void saveToFile(File actorsDir, Sites sites) throws Exception {
		for (Site s : sites.asCollection()) {
			saveToFile(actorsDir, s);
		}
	}

	public void saveToFile(File actorsDir, Site site) throws Exception {
		StringBuffer errs = new StringBuffer();
		site.validate(errs);
		if (errs.length() != 0)
			throw new Exception("Validation Errors: " + errs.toString());
		OMElement xml = siteToXML(site);
		String siteName = site.getName();
		Io.xmlToFile(new File(actorsDir + File.separator + siteName + ".xml"), xml);
	}
	
	public void delete(File actorsDir, String siteName) {
		for (String fileName : actorsDir.list()) {
			if (!fileName.startsWith(siteName))
				continue;
			new File(actorsDir + File.separator + fileName).delete();
		}
	}

}
