package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.util.Arrays;
import java.util.List;

public class RepositoryActorFactory extends AbstractActorFactory {

	static final String repositoryUniqueIdBase = "1.1.4567332.1.";
	static int repositoryUniqueIdIncr = 1;

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.PROVIDE_AND_REGISTER,
				TransactionType.RETRIEVE);

//	public Simulator buildNew(SimManager simm, boolean configureBase) {
//		return buildNew(simm, null, configureBase);
//	}

	public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) {
		ActorType actorType = ActorType.REPOSITORY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId);
		else
			sc = new SimulatorConfig();

		addEditableConfig(sc, repositoryUniqueId, ParamType.TEXT, getNewRepositoryUniqueId());
		addFixedEndpoint(sc, pnrEndpoint, actorType, TransactionType.PROVIDE_AND_REGISTER, false);
		addFixedEndpoint(sc, pnrTlsEndpoint, actorType, TransactionType.PROVIDE_AND_REGISTER, true);
		addFixedEndpoint(sc, retrieveEndpoint, actorType, TransactionType.RETRIEVE, false);
		addFixedEndpoint(sc, retrieveTlsEndpoint, actorType, TransactionType.RETRIEVE, true);
		addFixedEndpoint(sc, registerEndpoint, actorType, TransactionType.REGISTER, false);
		addFixedEndpoint(sc, registerTlsEndpoint, actorType, TransactionType.REGISTER, true);
		
		return new Simulator(sc);
	}
	
	static String getNewRepositoryUniqueId() {
		return repositoryUniqueIdBase + repositoryUniqueIdIncr++;
	}

	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {

	}

	public Site getActorSite(SimulatorConfig asc, Site site) {
		String siteName = asc.getDefaultName();

		if (site == null)
			site = new Site(siteName);

		boolean isAsync = false;

		site.addTransaction(new TransactionBean(
				TransactionType.PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(pnrEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(pnrTlsEndpoint).asString(), 
				true, 
				isAsync));

		site.addRepository(new TransactionBean(
				asc.get(AbstractActorFactory.repositoryUniqueId).asString(),
				RepositoryType.REPOSITORY,
				asc.get(retrieveEndpoint).asString(), 
				false, 
				isAsync));
		site.addRepository(new TransactionBean(
				asc.get(AbstractActorFactory.repositoryUniqueId).asString(),
				RepositoryType.REPOSITORY,
				asc.get(retrieveTlsEndpoint).asString(), 
				true, 
				isAsync));

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}



}
