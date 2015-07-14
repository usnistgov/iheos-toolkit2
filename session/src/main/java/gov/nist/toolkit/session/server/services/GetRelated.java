package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRelated extends CommonServiceManager {
	Session session;
	
	public GetRelated(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, ObjectRef or, List<String> assocs) {
		try {
			session.setSiteSpec(site);
			AnyIds aids;
			AnyId aid;
			try { 
				aids = session.queryServiceManager().fillInHome(new AnyIds(or));
				aid = aids.ids.get(0);
			} catch (Exception e) {
				return buildResultList(e);
			}

			String testName = "GetRelated";
			List<String> sections = new ArrayList<String>();
			Map<String, String> params = new HashMap<String, String>();
			params.put("$uuid$", aid.id);
			int i=1;
			for (String assoc : assocs) {
				params.put("$assoc" + Integer.toString(i) + "$", getFullAssocType(assoc));
				i++;
			}
			if (session.siteSpec.isRG()) {
				sections.add("XCA");
//				params.put("$home$", aid.home);
				String home = aid.home;
				if (home != null && !home.equals("")) {
					params.put("$home$", home);
				}
			}
			else if (session.siteSpec.isIG()) {
				sections.add("IG");
				params.put("$home$", aid.home);
			} else {
				sections.add("XDS");
			}

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, true));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

	public String getFullAssocType(String type) {
		if ("HasMember".equals(type))
			return "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type;
		return "urn:ihe:iti:2007:AssociationType:" + type;
	}


}
