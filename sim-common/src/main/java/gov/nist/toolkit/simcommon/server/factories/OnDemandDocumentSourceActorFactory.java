package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This actor factory is based on the repository actor factory.
 *
 */
public class OnDemandDocumentSourceActorFactory extends AbstractActorFactory implements IActorFactory {
	static Logger logger = Logger.getLogger(OnDemandDocumentSourceActorFactory.class.getName());

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

	@Override
	public Simulator buildNew(SimManager simm, SimId simId, String environment, boolean configureBase) throws Exception {
		ActorType actorType = ActorType.ONDEMAND_DOCUMENT_SOURCE;
//		logger.fine("Creating " + actorType.getName() + " with id " + simId);
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId, simId.getTestSession(), environment);
		else
			sc = new SimulatorConfig();

		configEnv(simm,simId,sc);
		SimulatorConfigElement envSce = sc.get(SimulatorProperties.environment);
		if (envSce!=null) {
			sc.getElements().remove(envSce);
		}
		addFixedConfig(sc, SimulatorProperties.environment, ParamType.TEXT, simId.getEnvironmentName());

		// Registry for the ODDE registration
		addEditableConfig(sc, SimulatorProperties.oddePatientId, ParamType.TEXT, "");
		addEditableConfig(sc, SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT, ParamType.TEXT, "15812");
		addEditableConfig(sc, SimulatorProperties.oddsRegistrySite, ParamType.SELECTION, new ArrayList<String>(), false);

		// Repository
		addFixedConfig(sc, SimulatorProperties.repositoryUniqueId, ParamType.TEXT, getNewRepositoryUniqueId());

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

	@Override
	public Site buildActorSite(SimulatorConfig asc, Site site) {
		String siteName = asc.getDefaultName();

		if (site == null)
			site = new Site(siteName, asc.getId().getTestSession());

		site.setTestSession(asc.getId().getTestSession());  // labels this site as coming from a sim

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


	@Override
	public ActorType getActorType() {
		return ActorType.ONDEMAND_DOCUMENT_SOURCE;
	}
}
