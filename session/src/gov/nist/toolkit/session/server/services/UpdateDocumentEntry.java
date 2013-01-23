package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.List;

public class UpdateDocumentEntry extends CommonServiceManager {
	Session session;
	
	public UpdateDocumentEntry(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, DocumentEntry de) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
//			String testName = datasetName;
//			List<String> sections = null;
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("$patientid$", pid);

			String[] areas = new String[1];
			areas[0] = "testdata-registry";

//			Result r = toolkit.xdstest(testName, sections, params, areas, true);
			return asList(ResultBuilder.RESULT("UpdateDocumentEntry"));
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

}
