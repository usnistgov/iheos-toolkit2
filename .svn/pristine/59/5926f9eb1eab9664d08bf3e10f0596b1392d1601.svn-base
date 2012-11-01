package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderValidation  extends CommonServiceManager {
	Session session;
	
	public FolderValidation(Session session) throws XdsException {
		this.session = session;
	}
	public List<Result> run(SiteSpec site, String pid) {

		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			String testName = "tc:folder";
			List<String> sections = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

}
