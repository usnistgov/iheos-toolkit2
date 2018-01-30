package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvideAndRetrieve extends CommonService {
	Session session;
	
	public ProvideAndRetrieve(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testInstance = new TestInstance("ProvideAndRetrieve", session.getTestSession());
			List<String> sections = new ArrayList<String>();
			sections.add("text");
			sections.add("xml");
			sections.add("pdf");
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			Site si = SiteServiceManager.getSiteServiceManager().getSite(session.id(), session.siteSpec.name, session.getTestSession());
//			Site si = session.siteServiceManager().getSites().getSite(session.siteSpec.name);
			String repuid = si.getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY);
			if (repuid == null) {
				Result r = ResultBuilder.RESULT(testInstance, null, new AssertionResult("Repository has no configured repositoryUniqueId","",false), null);
				return asList(r);
			}
			params.put("$repositoryUniqueId$", repuid);
			params.put("$repuid$", repuid);

			Result r = session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, false);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
