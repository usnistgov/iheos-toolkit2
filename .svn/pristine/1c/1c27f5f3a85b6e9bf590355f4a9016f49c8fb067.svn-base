package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSubmissionSets extends CommonServiceManager {
	String returnType = "LeafClass";
	Session session;
	
	public GetSubmissionSets(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, AnyIds aids) {
		try {
			try {
				if ( ! aids.isUUID())
					throw new Exception("GetSubmissionSets requires a UUID parameter");
			} catch (Exception e) {
				return buildResultList(e);
			}
			session.setSiteSpec(site);

			String testName = "GetSubmissionSets";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("$returnType$", returnType);
			String prefix;
			if (aids.isUUID())
				prefix = "id";
			else 
				prefix = "uid";
			
			for (int i=0; i<aids.size(); i++) {
				params.put("$" + prefix + i + "$", aids.ids.get(i).id);
			}

			
			try {
				if (session.siteSpec.isRG()) {
					sections.add("XCA");
					String home = site.homeId;
					if (home != null && !home.equals("")) {
						params.put("$home$", home);
					}
				} 
				else if (session.siteSpec.isIG()) {
					sections.add("IG");
				} 
				else{
					sections.add("XDS");
					return asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false));
				}
			} catch (Exception e) {
				return buildResultList(e);
			}
			List<Result> results = session.queryServiceManager().perCommunityQuery(aids, testName, sections, params);
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
