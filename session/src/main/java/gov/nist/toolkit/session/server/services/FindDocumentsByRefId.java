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

public class FindDocumentsByRefId extends CommonService {
	Session session;
	
	public FindDocumentsByRefId(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid, List<String> refIds) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testName = new TestInstance("FindDocumentsByRefId");
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
			if (refIds.size() > 0) 
				params.put("$refId0$", refIds.get(0));
			if (refIds.size() > 1) 
				params.put("$refId1$", refIds.get(1));
			if (refIds.size() > 2) 
				params.put("$refId2$", refIds.get(2));
			
			Result r = session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
