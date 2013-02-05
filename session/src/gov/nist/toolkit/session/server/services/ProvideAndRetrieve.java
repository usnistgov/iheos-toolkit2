package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvideAndRetrieve extends CommonServiceManager {
	Session session;
	
	public ProvideAndRetrieve(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			String testName = "ProvideAndRetrieve";
			List<String> sections = new ArrayList<String>();
			sections.add("text");
			sections.add("xml");
			sections.add("pdf");
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			Site si = SiteServiceManager.getSiteServiceManager().getSite(session.id(), session.siteSpec.name);
//			Site si = session.siteServiceManager().getSites().getSite(session.siteSpec.name);
			String repuid = si.getRepositoryUniqueId();
			if (repuid == null) {
				Result r = ResultBuilder.RESULT(testName, null, new AssertionResult("Repository has no configured repositoryUniqueId","",false), null);
				return asList(r);
			}
			params.put("$repositoryUniqueId$", repuid);
			params.put("$repuid$", repuid);

			Result r = session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
