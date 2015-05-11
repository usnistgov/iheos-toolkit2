package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.util.Arrays;
import java.util.List;

public class IGActorFactory extends ActorFactory {

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.STORED_QUERY, 
				TransactionType.RETRIEVE);

	protected Simulator buildNew(SimManager simm, boolean configureBase) {
		ActorType actorType = ActorType.INITIATING_GATEWAY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType);
		else
			sc = new SimulatorConfig();

		addFixedEndpoint(sc, igqEndpoint, actorType, TransactionType.IG_QUERY, false);
		addFixedEndpoint(sc, igqTlsEndpoint, actorType, TransactionType.IG_QUERY, true);
		addFixedEndpoint(sc, igrEndpoint, actorType, TransactionType.IG_RETRIEVE, false);
		addFixedEndpoint(sc, igrTlsEndpoint, actorType, TransactionType.IG_RETRIEVE, true);

		sc.setRemoteSitesNecessary(true, "RGs");
		
		return new Simulator(sc);
	}


	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
		
	}

	public Site getActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName);
		
		boolean isAsync = false;
		
		site.addTransaction(new TransactionBean(
				TransactionType.IG_QUERY.getCode(),
				RepositoryType.NONE,
				sc.get(igqEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.IG_QUERY.getCode(),
				RepositoryType.NONE,
				sc.get(igqTlsEndpoint).asString(), 
				true, 
				isAsync));
		
		site.addTransaction(new TransactionBean(
				TransactionType.IG_RETRIEVE.getCode(),
				RepositoryType.NONE,
				sc.get(igrEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.IG_RETRIEVE.getCode(),
				RepositoryType.NONE,
				sc.get(igrTlsEndpoint).asString(), 
				true, 
				isAsync));
		
		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}


}
