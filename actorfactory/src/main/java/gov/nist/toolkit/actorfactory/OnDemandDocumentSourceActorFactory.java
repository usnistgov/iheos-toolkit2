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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This actor factory is based on the repository actor factory.
 *
 */
public class OnDemandDocumentSourceActorFactory extends AbstractActorFactory {

	static final String repositoryUniqueIdBase = "1.1.4567248.1."; // It is an arbitrary value.
	static int repositoryUniqueIdIncr = 1;
	boolean isRecipient = false;

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.RETRIEVE);


	// Label as a DocumentRecipient
	public void asRecipient() {
		isRecipient = true;
	}

	public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) {
		ActorType actorType = ActorType.ONDEMAND_DOCUMENT_SOURCE;
		logger.debug("Creating " + actorType.getName() + " with id " + simId);
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId);
		else
			sc = new SimulatorConfig();

		// Repository
		addEditableConfig(sc, SimulatorProperties.repositoryUniqueId, ParamType.TEXT, getNewRepositoryUniqueId());

		addFixedEndpoint(sc, SimulatorProperties.retrieveEndpoint, actorType, TransactionType.ODDS_RETRIEVE, false);
		addFixedEndpoint(sc, SimulatorProperties.retrieveTlsEndpoint, actorType, TransactionType.ODDS_RETRIEVE, true);

		addEditableConfig(sc, SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS, ParamType.BOOLEAN, true);
		addEditableConfig(sc, SimulatorProperties.oddsRepositorySite, ParamType.SELECTION, new ArrayList<String>(), false);
		addEditableConfig(sc, SimulatorProperties.contentBundle, ParamType.TEXT, "15812/Register_OD/ContentBundle");

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

		site.user = asc.getId().user;  // labels this site as coming from a sim

		boolean isAsync = false;

		site.addRepository(new TransactionBean(
				asc.get(SimulatorProperties.repositoryUniqueId).asString(),
				RepositoryType.ODDS,
				asc.get(SimulatorProperties.retrieveEndpoint).asString(),
				false, 
				isAsync));
		site.addRepository(new TransactionBean(
				asc.get(SimulatorProperties.repositoryUniqueId).asString(),
				RepositoryType.ODDS,
				asc.get(SimulatorProperties.retrieveTlsEndpoint).asString(),
				true, 
				isAsync));

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}



}
