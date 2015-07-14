package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSSandContents extends CommonServiceManager {
	Session session;
	
	public GetSSandContents(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, String ssid) {
		try {
			session.setSiteSpec(site);

			String testName = "GetSubmissionSetAndContents";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			try {
				if (session.siteSpec.isRG()) {
					sections.add("XCA");
//					params.put("$home$", toolkit.getHome());
					String home = site.homeId; //session.getHome();
					if (home != null && !home.equals("")) {
						params.put("$home$", home);
					}
				} 
				else if (session.siteSpec.isIG()) {
					sections.add("IG");
					params.put("$home$", site.homeId);  //session.getHome());
				} 
				else{
					sections.add("XDS");
				}
				if (ssid.startsWith("urn:uuid:")) {
					params.put("$uuid$", ssid);
				} else {
					params.put("$uid$", ssid);
				}
			} catch (Exception e) {
				return buildResultList(e);
			}

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, true));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
