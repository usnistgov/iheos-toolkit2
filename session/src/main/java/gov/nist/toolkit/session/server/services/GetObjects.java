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

public class GetObjects extends CommonServiceManager {
	String returnType = "LeafClass";
	Session session;
	
	public GetObjects(Session session) throws XdsException {
		this.session = session;
	}

	public List<Result> run(SiteSpec site, ObjectRefs ids) {
		try {
			session.setSiteSpec(site);

			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("$returnType$", returnType);
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
					List<Result> results = getObjects(new AnyIds(ids), sections, params);
					session.clear();
					return results;
				}
				else {
					sections.add("XDS");
				}
			} catch (Exception e) {
				return buildResultList(e);
			}
			List<Result> results = getObjects(new AnyIds(ids), sections, params);
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}
	
	boolean hasContent(List<Result> results) {
		if (results == null)
			return false;
		if (results.size() == 0)
			return false;
		for (Result r : results) {
			if (r.hasContent())
				return true;
		}
		return false;
	}

	List<Result> getObjects(AnyIds aids, List<String> sections, Map<String, String> params)  {
		List<Result> results = new ArrayList<Result>();

		try {
			List<Result> results1 = session.queryServiceManager().perCommunityQuery(aids, "GetAssociations", sections, params);
			if (hasContent(results1))
				results.addAll(results1);
		} catch (Exception e) {		}

		try {
			List<Result> results2 = session.queryServiceManager().perCommunityQuery(aids, "GetSubmissionSetAndContents", sections, params);
			if (hasContent(results2))
				results.addAll(results2);
		} catch (Exception e) {}

		try {
			List<Result> results3 = session.queryServiceManager().perCommunityQuery(aids, "GetDocuments", sections, params);
			if (hasContent(results3))
				results.addAll(results3);
		} catch (Exception e) {}

		try {
			List<Result> results4 = session.queryServiceManager().perCommunityQuery(aids, "GetFolders", sections, params);
			if (hasContent(results4))
				results.addAll(results4);
		} catch (Exception e) {}


		return results;
	}

}
