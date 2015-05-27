package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrieveDocument extends CommonServiceManager {
	Session session;
	
	public RetrieveDocument(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, Uids uids) throws Exception {
		session.setSiteSpec(site);

			// the client should fill in the uids details, but for now... 
			// eventually, the client request for getRepositoryNames needs to change to 
			// getRepositories and the repuid and home are included. Then
			// the client can send back a complete uid
			AnyIds aids = session.queryServiceManager().fillInHome(new AnyIds(uids));
			uids = new Uids(aids);
			
			// set repository from tool selection
			for (Uid uid : uids.uids) {
				if ((uid.repositoryUniqueId == null || uid.repositoryUniqueId.equals(""))
						&& session.repUid != null && !session.repUid.equals(""))
					uid.repositoryUniqueId = session.repUid;
			}

			String testName = "RetrieveDocumentSet";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			if (session.siteSpec.isRG()) {
				sections.add("XCA");
				params.put("$home$", site.homeId);
			} 
			else if (session.siteSpec.isIG()) {
				sections.add("IG");
				params.put("$home$", site.homeId);
			} 
			else {
				sections.add("XDS");
			}
			List<Result> results = session.queryServiceManager().perRepositoryRetrieve(uids, testName, sections, params);
			return results;
	}


}
