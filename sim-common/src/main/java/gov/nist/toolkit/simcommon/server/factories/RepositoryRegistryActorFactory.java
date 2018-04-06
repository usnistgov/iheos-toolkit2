package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.ArrayList;
import java.util.List;

public class RepositoryRegistryActorFactory extends AbstractActorFactory implements IActorFactory {

	protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
		RegistryActorFactory registryActorFactory;
		RepositoryActorFactory repositoryActorFactory;
		ActorType actorType = ActorType.REPOSITORY_REGISTRY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, newID, newID.getTestSession());
		else
			sc = new SimulatorConfig();

		SimId simId = sc.getId();
		// This needs to be grouped with a Document Registry
		registryActorFactory = new RegistryActorFactory();
		registryActorFactory.setTransactionOnly(isTransactionOnly());
		SimulatorConfig registryConfig = registryActorFactory.buildNew(simm, simId, true).getConfig(0);
		
		// This needs to be grouped with a Document Repository also
		repositoryActorFactory = new RepositoryActorFactory();
		SimulatorConfig repositoryConfig = repositoryActorFactory.buildNew(simm, simId, true).getConfig(0);

		// two combined simulators do not have separate lives
		sc.add(registryConfig);
		sc.add(repositoryConfig);
		
		return new Simulator(sc);
	}

	@Override
	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
		
	}

	@Override
	public Site buildActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();

		if (site == null)
			site = new Site(siteName, sc.getId().getTestSession());
		site.setTestSession(sc.getId().getTestSession());  // labels this site as coming from a sim

		boolean isAsync = false;

		site = new RegistryActorFactory().buildActorSite(sc, site);
		site = new RepositoryActorFactory().buildActorSite(sc, site);

		return site;
	}

	@Override
	public List<TransactionType> getIncomingTransactions() {
		List<TransactionType> tt = new ArrayList<TransactionType>();
		tt.addAll(new RegistryActorFactory().getIncomingTransactions());
		tt.addAll(new RepositoryActorFactory().getIncomingTransactions());
		return tt;
	}


	@Override
	public ActorType getActorType() {
		return ActorType.REPOSITORY_REGISTRY;
	}
}
