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

public class GetDocuments extends CommonServiceManager {
	String returnType = "LeafClass";
	Session session;
	
	public GetDocuments(Session session) throws XdsException {
		this.session = session;;
	}

	public List<Result> run(SiteSpec site, AnyIds aids) {
		try {
			// load site config into session
			session.setSiteSpec(site);
			
			String testName = "GetDocuments";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("$returnType$", returnType);

			String home = site.homeId;
			if (home != null && !home.equals("")) {
				params.put("$home$", home);
			}
			
			String prefix;
			if (aids.isLid())
				prefix = "lid";
			else if (aids.isUUID())
				prefix = "id";
			else
				prefix = "uid";
			
			for (int i=0; i<aids.size(); i++) {
				params.put("$" + prefix + i + "$", aids.ids.get(i).id);
			}
			
			params.put("$metadatalevel$", 
					(aids.isLid()) ? "2" : "1");

			return session.queryServiceManager().runPerCommunityQuery(aids, session, testName, sections, params);
				
		} catch (Exception e) {
			return buildResultList(e);
		} 
		finally {
			session.clear();
		}

	}

	public void setLeafClassReturn() {
		returnType = "LeafClass";
	}
	
	public void setObjectRefReturn() {
		returnType = "ObjectRef";
	}
	

}
