package gov.nist.toolkit.actorfactory.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RepositoryActorFactory extends AbstractActorFactory implements IActorFactory {

	static final String repositoryUniqueIdBase = "1.1.4567332.1.";
	static int repositoryUniqueIdIncr = 1;
	boolean isRecipient = false;

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.PROVIDE_AND_REGISTER,
				TransactionType.RETRIEVE);

	RepositoryActorFactory(){ super(); }

	// Label as a DocumentRecipient
	public void asRecipient() {
		isRecipient = true;
	}

	public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) {
		ActorType actorType = ActorType.REPOSITORY;
		logger.debug("Creating " + actorType.getName() + " with id " + simId);
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId);
		else
			sc = new SimulatorConfig();

		if (isRecipient) {
			addFixedEndpoint(sc, SimulatorProperties.pnrEndpoint, actorType, TransactionType.XDR_PROVIDE_AND_REGISTER, false);
			addFixedEndpoint(sc, SimulatorProperties.pnrTlsEndpoint, actorType, TransactionType.XDR_PROVIDE_AND_REGISTER, true);
		} else {   // Repository
			addEditableConfig(sc, SimulatorProperties.repositoryUniqueId, ParamType.TEXT, getNewRepositoryUniqueId());
			addFixedEndpoint(sc, SimulatorProperties.pnrEndpoint, actorType, TransactionType.PROVIDE_AND_REGISTER, false);
			addFixedEndpoint(sc, SimulatorProperties.pnrTlsEndpoint, actorType, TransactionType.PROVIDE_AND_REGISTER, true);
			addFixedEndpoint(sc, SimulatorProperties.retrieveEndpoint, actorType, TransactionType.RETRIEVE, false);
			addFixedEndpoint(sc, SimulatorProperties.retrieveTlsEndpoint, actorType, TransactionType.RETRIEVE, true);
			addEditableNullEndpoint(sc, SimulatorProperties.registerEndpoint, actorType, TransactionType.REGISTER, false);
			addEditableNullEndpoint(sc, SimulatorProperties.registerTlsEndpoint, actorType, TransactionType.REGISTER, true);
		}

		return new Simulator(sc);
	}
	
	static synchronized String getNewRepositoryUniqueId() {
		Collection<String> existingIds;
		try {
			existingIds = SimCache.getAllRepositoryUniqueIds();
		} catch (Throwable t) {
			existingIds = new ArrayList<>();
		}
		String value = newValue();
		while (existingIds.contains(value)) {
			value = newValue();
		}
		return value;
	}

	private static String newValue() {
		return repositoryUniqueIdBase + repositoryUniqueIdIncr++;
	}

	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {

	}

	public Site getActorSite(SimulatorConfig asc, Site site) {
		String siteName = asc.getDefaultName();

		if (site == null)
			site = new Site(siteName);

		site.user = asc.getId().user;  // labels this site as coming from a sim

		boolean isAsync = false;

		site.addTransaction(new TransactionBean(
				TransactionType.PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(SimulatorProperties.pnrEndpoint).asString(),
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.PROVIDE_AND_REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(SimulatorProperties.pnrTlsEndpoint).asString(),
				true, 
				isAsync));

		site.addRepository(new TransactionBean(
				asc.get(SimulatorProperties.repositoryUniqueId).asString(),
				RepositoryType.REPOSITORY,
				asc.get(SimulatorProperties.retrieveEndpoint).asString(),
				false, 
				isAsync));
		site.addRepository(new TransactionBean(
				asc.get(SimulatorProperties.repositoryUniqueId).asString(),
				RepositoryType.REPOSITORY,
				asc.get(SimulatorProperties.retrieveTlsEndpoint).asString(),
				true, 
				isAsync));

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}


	@Override
	public ActorType getActorType() {
		return ActorType.REPOSITORY;
	}
}
