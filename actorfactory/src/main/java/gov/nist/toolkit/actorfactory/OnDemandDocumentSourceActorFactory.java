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

	static final String repositoryUniqueIdBase = "1.1.4567248.1."; // This arbitrary value is different from the regular repository unique id.
	static int repositoryUniqueIdIncr = 1;
	boolean isRecipient = false;

	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.RETRIEVE,
				TransactionType.ODDS_RETRIEVE
		);


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

		// Registry for the ODDE registration
		addEditableConfig(sc, SimulatorProperties.oddePatientId, ParamType.TEXT, "");
		addEditableConfig(sc, SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT, ParamType.TEXT, "15812");
		addEditableConfig(sc, SimulatorProperties.oddsRegistrySite, ParamType.SELECTION, new ArrayList<String>(), false);

		// Repository
		addEditableConfig(sc, SimulatorProperties.repositoryUniqueId, ParamType.TEXT, getNewRepositoryUniqueId());

		addFixedEndpoint(sc, SimulatorProperties.retrieveEndpoint, actorType, TransactionType.ODDS_RETRIEVE, false);
		addFixedEndpoint(sc, SimulatorProperties.retrieveTlsEndpoint, actorType, TransactionType.ODDS_RETRIEVE, true);

//		addFixedConfig(sc, SimulatorProperties.oddsContentSupplyState, ParamType.TEXT, ""); // This should be a dynamic value

		// By default the persistence option will be off. It does not make sense to enable this option by default because additional user inputs are required, which are unknown at this point.
		addEditableConfig(sc, SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS, ParamType.BOOLEAN, false);

		// If the user enables the persistence option, then we will require additional details such as the repository and the patient Id.
		addEditableConfig(sc, SimulatorProperties.oddsRepositorySite, ParamType.SELECTION, new ArrayList<String>(), false);

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
