package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.registrymetadata.client.Code;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MpqFindDocuments extends CommonService {
	Session session;
	
	public MpqFindDocuments(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, String pid, List<String> classCodes, List<String> hcftCodes, List<String> eventCodes) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testInstance = new TestInstance("MPQ-FindDocuments");
			List<String> sections = new ArrayList<String>();
			if (session.siteSpec.actorType.equals(ActorType.REGISTRY))
				sections.add("XDS");
			else
				sections.add("XCA");
			Map<String, String> params = new HashMap<String, String>();
			if (pid != null && !pid.equals(""))
				params.put("$patient_id$", pid);

			int i=0;
			for (String codeDef : classCodes) {
				params.put("$cc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
				i++;
			}

			i=0;
			for (String codeDef : hcftCodes) {
				params.put("$hcft" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
				i++;
			}

			i=0;
			for (String codeDef : eventCodes) {
				params.put("$ec" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
				i++;
			}

			Result r = session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
