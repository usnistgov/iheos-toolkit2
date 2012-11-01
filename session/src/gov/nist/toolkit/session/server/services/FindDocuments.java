package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindDocuments extends CommonServiceManager {
	Session session;
	
	public FindDocuments(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid, boolean onDemand) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			String testName = "FindDocuments";
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
			if (onDemand)
				params.put("$object_types$","('urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1','urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248')");
			else
				params.put("$object_types$","('urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1')");
				

			Result r = session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
