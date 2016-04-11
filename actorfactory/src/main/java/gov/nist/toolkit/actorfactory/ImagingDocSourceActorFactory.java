package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.util.Arrays;
import java.util.List;

public class ImagingDocSourceActorFactory extends AbstractActorFactory {
	SimId newID = null;
	static final String idsRepositoryUniqueIdBase = "1.1.4567332.10.";
	static int idsRepositoryUniqueIdIncr = 1;


	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.RET_IMG_DOC_SET 
				);

	protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) {
		this.newID = newID;

		ActorType actorType = ActorType.IMAGING_DOC_SOURCE;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, newID);
		else
			sc = new SimulatorConfig();

		addEditableConfig(sc, SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, getNewIdsRepositoryUniqueId());
		addFixedEndpoint(sc, SimulatorProperties.idsrEndpoint, actorType, TransactionType.RET_IMG_DOC_SET, false);
		addFixedEndpoint(sc, SimulatorProperties.idsrTlsEndpoint, actorType, TransactionType.RET_IMG_DOC_SET, true);
//        addEditableConfig(sc, SimulatorProperties.respondingGateways, ParamType.SELECTION, new ArrayList<String>(), true);

		return new Simulator(sc);
	}

	static String getNewIdsRepositoryUniqueId() {
		return idsRepositoryUniqueIdBase + idsRepositoryUniqueIdIncr++;
	}



	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
		
	}

	public Site getActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName);

		site.user = sc.getId().user;  // labels this site as coming from a sim

		boolean isAsync = false;
		
		site.addTransaction(new TransactionBean(
				TransactionType.RET_IMG_DOC_SET.getCode(),
				RepositoryType.NONE,
				sc.get(SimulatorProperties.idsrEndpoint).asString(),
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.RET_IMG_DOC_SET.getCode(),
				RepositoryType.NONE,
				sc.get(SimulatorProperties.idsrTlsEndpoint).asString(),
				true, 
				isAsync));

		site.addRepository(new TransactionBean(
				sc.get(SimulatorProperties.idsRepositoryUniqueId).asString(),
				RepositoryType.IDS,
				sc.get(SimulatorProperties.idsrEndpoint).asString(),
				false,
				isAsync));
		site.addRepository(new TransactionBean(
				sc.get(SimulatorProperties.idsRepositoryUniqueId).asString(),
				RepositoryType.IDS,
				sc.get(SimulatorProperties.idsrTlsEndpoint).asString(),
				true,
				isAsync));
		
		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}


}
