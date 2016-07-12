package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSSandContents extends CommonService {
	Session session;
	
	public GetSSandContents(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, String ssid, Map<String, List<String>> codeSpec) {
		try {
			session.setSiteSpec(site);

			TestInstance testInstance = new TestInstance("GetSubmissionSetAndContents");
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			try {

				// XDS Codes
				List<String> deType = codeSpec.get(CodesConfiguration.DocumentEntryType);
				// Only apply the deType code when provided, otherwise get the normal submission set without this filter
				if (deType != null) {
					// DocumentEntryType
					int i=0;
					for (String codeDef : deType) {
						params.put("$ot" + String.valueOf(i) + "$", codeDef);
						i++;
					}
				}

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

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, true));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
