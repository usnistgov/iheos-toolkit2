package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFolders extends CommonService {
	String returnType = "LeafClass";
	Session session;
	
	public GetFolders(Session session) throws XdsException {
		this.session = session;
	}

	public GetFolders setLeafClassReturn() {
		returnType = "LeafClass";
		return this;
	}

	public GetFolders setObjectRefReturn() {
		returnType = "ObjectRef";
		return this;
	}

	public List<Result> run(SiteSpec site, AnyIds aids) {
		try {
			session.setSiteSpec(site);

			TestInstance testInstance = new TestInstance("GetFolders", session.getTestSession());
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("$returnType$", returnType);
			String home = site.homeId;
			if (home != null && !home.equals("")) {
				params.put("$home$", home);
			}

			String prefix;
			if (aids.isUUID())
				prefix = "id";
			else 
				prefix = "uid";
			
			for (int i=0; i<aids.size(); i++) {
				params.put("$" + prefix + i + "$", aids.ids.get(i).id);
			}
			
			return session.queryServiceManager().runPerCommunityQuery(aids, session, testInstance, sections, params);
			
		} catch (Exception e) {
			return buildResultList(e);
		} finally {
			session.clear();
		}

	}


}
