package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.ArrayList;
import java.util.List;

public class RepositoryRegistryActorFactory extends AbstractActorFactory {
	SimId newID = null;

	RegistryActorFactory registryActorFactory;
	RepositoryActorFactory repositoryActorFactory;

	protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
		this.newID = newID;
		ActorType actorType = ActorType.REPOSITORY_REGISTRY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, newID);
		else
			sc = new SimulatorConfig();

		SimId simId = sc.getId();
		// This needs to be grouped with a Document Registry
		registryActorFactory = new RegistryActorFactory();
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

	public Site getActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();

		if (site == null)
			site = new Site(siteName);

		boolean isAsync = false;

		new RegistryActorFactory().getActorSite(sc, site);
		new RepositoryActorFactory().getActorSite(sc, site);

		return site;
	}

	@Override
	public List<TransactionType> getIncomingTransactions() {
		List<TransactionType> tt = new ArrayList<TransactionType>();
		tt.addAll(new RegistryActorFactory().getIncomingTransactions());
		tt.addAll(new RepositoryActorFactory().getIncomingTransactions());
		return tt;
	}


}
