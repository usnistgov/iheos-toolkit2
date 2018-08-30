package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.util.Arrays;
import java.util.List;

public class ImagingDocSourceActorFactory extends AbstractActorFactory implements IActorFactory {
	SimId newID = null;
	static final String idsRepositoryUniqueIdBase = "1.1.4567332.10.";
	static int idsRepositoryUniqueIdIncr = 1;


	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.RET_IMG_DOC_SET,
				TransactionType.WADO_RETRIEVE
				);

	protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
		this.newID = newID;

		ActorType actorType = ActorType.IMAGING_DOC_SOURCE;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, newID, newID.getTestSession());
		else
			sc = new SimulatorConfig();

		addEditableConfig(sc, SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, getNewIdsRepositoryUniqueId());
		addEditableConfig(sc, SimulatorProperties.idsImageCache, ParamType.TEXT, newID.getId());
		addEditableEndpoint(sc, SimulatorProperties.idsrEndpoint, actorType, TransactionType.RET_IMG_DOC_SET, false);
		addEditableEndpoint(sc, SimulatorProperties.idsrTlsEndpoint, actorType, TransactionType.RET_IMG_DOC_SET, true);
      addEditableEndpoint(sc, SimulatorProperties.wadoEndpoint, actorType, TransactionType.WADO_RETRIEVE, false);
      addEditableEndpoint(sc, SimulatorProperties.wadoTlsEndpoint, actorType, TransactionType.WADO_RETRIEVE, true);

		return new Simulator(sc);
	}

	static String getNewIdsRepositoryUniqueId() {
		return idsRepositoryUniqueIdBase + idsRepositoryUniqueIdIncr++;
	}



	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
		
	}

	@Override
	public Site buildActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName, sc.getId().getTestSession());

		site.setTestSession(sc.getId().getTestSession());  // labels this site as coming from a sim

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
      
      site.addTransaction(new TransactionBean(
            TransactionType.WADO_RETRIEVE.getCode(),
            RepositoryType.NONE,
            sc.get(SimulatorProperties.wadoEndpoint).asString(),
            false, 
            isAsync));
      site.addTransaction(new TransactionBean(
            TransactionType.WADO_RETRIEVE.getCode(),
            RepositoryType.NONE,
            sc.get(SimulatorProperties.wadoTlsEndpoint).asString(),
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


	@Override
	public ActorType getActorType() {
		return ActorType.IMAGING_DOC_SOURCE;
	}
}
