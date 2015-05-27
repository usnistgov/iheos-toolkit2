package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAssociations extends CommonServiceManager {
	String returnType = "LeafClass";
	Session session;

	public GetAssociations(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, ObjectRefs ids) {
		try {
			session.setSiteSpec(site);

			String testName = "GetAssociations";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("$returnType$", returnType);

			String prefix;
				prefix = "id";
			
			for (int i=0; i<ids.objectRefs.size(); i++) {
				params.put("$" + prefix + i + "$", ids.objectRefs.get(i).id);
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
					List<Result> results = session.queryServiceManager().perCommunityQuery(new AnyIds(ids), testName, sections, params);
					session.clear();
					return results;
				}
				else {
					sections.add("XDS");
					return asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false));
				}
			} catch (Exception e) {
				return buildResultList(e);
			}
			List<Result> results = session.queryServiceManager().perCommunityQuery(new AnyIds(ids), testName, sections, params);
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}
	
}
