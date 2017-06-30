package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrieveImagingDocSet extends CommonService {
	static Logger logger = Logger.getLogger(RetrieveImagingDocSet.class);
	Session session;
	
	public RetrieveImagingDocSet(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, Uids uids, String studyRequest, String transferSyntax) throws Exception {
		logger.debug("RetrieveImagingDocSet::run");
//		logger.debug(" Study Request " + studyRequest);
		logger.debug(" Session.repUID" + session.repUid);
		session.setSiteSpec(site);

			// the client should fill in the uids details, but for now... 
			// eventually, the client request for getRepositoryNames needs to change to 
			// getRepositories and the repuid and home are included. Then
			// the client can send back a complete uid
			AnyIds aids = session.queryServiceManager().fillInHome(new AnyIds(uids));
			uids = new Uids(aids);
			
			// set repository from tool selection
			for (Uid uid : uids.uids) {
				logger.debug(" uid.uid: " + uid.uid);
				logger.debug(" uid.repositoryUniqueId: " + uid.repositoryUniqueId);
				if ((uid.repositoryUniqueId == null || uid.repositoryUniqueId.equals(""))
						&& session.repUid != null && !session.repUid.equals(""))
					uid.repositoryUniqueId = session.repUid;
				logger.debug(" New uid.repositoryUniqueId: " + uid.repositoryUniqueId);
			}

			TestInstance testInstance = new TestInstance("RetrieveImagingDocSet");
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			if (session.siteSpec.isImagingDocumentSourceActor()) {
				logger.debug("Actor is Imaging Document Source, assume XDS-I transaction");
				sections.add("XDSI");
				params.put("$full_imaging_request$", studyRequest);
			} 
//			else if (session.siteSpec.isIG()) {
//				SECTIONS.add("IG");
//				params.put("$home$", site.homeId);
//			} 
			else {
				logger.fatal("Unrecognized SiteSpec actor type. SiteSpec.name = " + session.siteSpec.getName() +
					" Actor Type (name) = " + session.siteSpec.getActorType().getName());

			}
//			for (String param : params.keySet()) {
//			    logger.debug("..." + param + ": " + params.get(param));
//			}

			List<Result> results = session.queryServiceManager().perRepositoryImagingDocSetRetrieve(uids, testInstance, sections, params);
			return results;
	}


}
