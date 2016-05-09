package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitRegistryTestdata extends CommonService {
	Session session;
	
	public SubmitRegistryTestdata(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String datasetName, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testInstance = new TestInstance(datasetName);
			List<String> sections = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			String[] areas = new String[1];
			areas[0] = "testdata-registry";

			Result r = session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, areas, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

}
