package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Discover which Registry(s) holds a DocumentEntry given the id (entryUUID 
 * or uniqueId)
 * @author bill
 *
 */
public class DiscoverDocumentEntry extends CommonServiceManager {
	Session session;
	
	public DiscoverDocumentEntry(Session session) throws XdsException {
		this.session = session;;
	}

	public List<String> run(List<String> registryNames, AnyId id) throws XdsException {
		List<String> foundRegistries = new ArrayList<String>();
		
		for (String registryName : registryNames) {
			SiteSpec site = new SiteSpec(registryName, ActorType.REGISTRY, null);
			AnyIds ids = new AnyIds(id);
			GetDocuments gd = new GetDocuments(session);
			gd.setObjectRefReturn();
			List<Result> result = gd.run(site, ids);
			
			if (result.size() == 0)
				continue;
			
			List<StepResult> stepResults = result.get(0).getStepResults();
			if (stepResults.size() == 0)
				continue;
			
			StepResult stepResult = stepResults.get(0);
			if (stepResult.getObjectRefCount() > 0)
				foundRegistries.add(registryName);
		}
		
		return foundRegistries;
	}
	
}
