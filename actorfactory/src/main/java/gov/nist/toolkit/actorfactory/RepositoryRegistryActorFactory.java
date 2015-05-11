package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.util.ArrayList;
import java.util.List;

public class RepositoryRegistryActorFactory extends ActorFactory {

	RegistryActorFactory registryActorFactory;
	RepositoryActorFactory repositoryActorFactory;

	protected Simulator buildNew(SimManager simm, boolean configureBase) throws Exception {
		ActorType actorType = ActorType.REPOSITORY_REGISTRY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType);
		else
			sc = new SimulatorConfig();

		String simId = sc.getId();
		// This needs to be grouped with a Document Registry
		registryActorFactory = new RegistryActorFactory();
		SimulatorConfig registryConfig = registryActorFactory.buildNew(simm, simId, true).getConfig(0);
		
		// This needs to be grouped with a Document Repository also
		repositoryActorFactory = new RepositoryActorFactory();
		SimulatorConfig repositoryConfig = repositoryActorFactory.buildNew(simm, simId, true).getConfig(0);
		
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
