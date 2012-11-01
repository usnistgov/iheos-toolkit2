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

public class SrcStoresDocVal extends CommonServiceManager {
	Session session;
	
	public SrcStoresDocVal(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String ssid) {
		try {
			session.setSiteSpec(site);
			String testName = "SourceStoresDocumentValidation";
			List<String> sections = new ArrayList<String>();
			sections.add("query");
			sections.add("retrieve");
			Map<String, String> params = new HashMap<String, String>();
			if (ssid.startsWith("urn:uuid:")) {
				params.put("$uuid$", ssid);
			} else {
				params.put("$uid$", ssid);
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
