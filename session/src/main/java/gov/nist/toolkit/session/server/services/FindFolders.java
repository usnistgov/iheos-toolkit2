package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindFolders extends CommonService {
	Session session;
	
	public FindFolders(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testInstance = new TestInstance("FindFolders");
			List<String> sections = new ArrayList<String>();

			Map<String, String> params = new HashMap<String, String>();

			if (session.siteSpec.actorType.equals(ActorType.REGISTRY))
				sections.add("XDS");
			else if (session.siteSpec.actorType.equals(ActorType.INITIATING_GATEWAY))
				sections.add("IG");
			else {
				sections.add("XCA");
				String home = site.homeId;
				if (home != null && !home.equals("")) {
					params.put("$home$", home);
				}
			}
			params.put("$patient_id$", pid);

			Result r = session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

}
