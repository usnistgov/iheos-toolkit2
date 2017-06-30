package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.List;

public class UpdateDocumentEntry extends CommonService {
	Session session;
	
	public UpdateDocumentEntry(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, DocumentEntry de) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
//			String testId = datasetName;
//			List<String> SECTIONS = null;
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("$patientid$", pid);

			String[] areas = new String[1];
			areas[0] = "testdata-registry";

//			Result r = toolkit.xdstest(testId, SECTIONS, params, areas, true);
			return asList(ResultBuilder.RESULT(new TestInstance("UpdateDocumentEntry")));
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

}
