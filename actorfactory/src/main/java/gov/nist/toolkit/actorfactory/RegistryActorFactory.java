package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.adt.ListenerFactory;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RegistryActorFactory extends AbstractActorFactory {
	boolean isRecipient = false;  // used as part of Document Recipient

	static final List<TransactionType> incomingTransactions =
		Arrays.asList(
				TransactionType.REGISTER,
				TransactionType.STORED_QUERY,
				TransactionType.UPDATE
				);

	// This does not start any listeners allocated.  The port assignment is made
	// and the caller gets the responsibility for starting the listeners
	// Listeners cannot be started until the sim config is saved
	@Override
	public Simulator buildNew(SimManager simm, SimId simId, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		ActorType actorType = ActorType.REGISTRY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId);
		else
			sc = new SimulatorConfig();

		if (isRecipient) {  // part of recipient
			addEditableConfig(sc, SimulatorConfig.VALIDATE_CODES, ParamType.BOOLEAN, false);
			addEditableConfig(sc, extraMetadataSupported, ParamType.BOOLEAN, true);
			addEditableConfig(sc, SimulatorConfig.REST_CALLBACK_URI, ParamType.TEXT, "");
			addFixedConfig(sc, SimulatorConfig.UPDATE_METADATA_OPTION, ParamType.BOOLEAN, false);
			addFixedConfig(sc, SimulatorConfig.PART_OF_RECIPIENT, ParamType.BOOLEAN, true);
			addFixedEndpoint(sc, registerEndpoint,       actorType, TransactionType.REGISTER,     false);
			addFixedEndpoint(sc, registerTlsEndpoint,    actorType, TransactionType.REGISTER,     true);
		} else {  // not part of recipient
            if (simId.getEnvironmenName() != null) {
                EnvSetting es = new EnvSetting(simId.getEnvironmenName());
                File codesFile = es.getCodesFile();
                addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());
            } else {
                File codesFile = EnvSetting.getEnvSetting(simm.sessionId).getCodesFile();
                addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());
            }

			addEditableConfig(sc, SimulatorConfig.UPDATE_METADATA_OPTION, ParamType.BOOLEAN, false);
			addEditableConfig(sc, SimulatorConfig.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, ParamType.BOOLEAN, true);
			addEditableConfig(sc, extraMetadataSupported, ParamType.BOOLEAN, true);
			addEditableConfig(sc, SimulatorConfig.VALIDATE_CODES, ParamType.BOOLEAN, true);
			addEditableConfig(sc, SimulatorConfig.REST_CALLBACK_URI, ParamType.TEXT, "");
			addFixedConfig(sc, SimulatorConfig.PIF_PORT, ParamType.TEXT, Integer.toString(ListenerFactory.allocatePort(simId.toString())));
			addFixedEndpoint(sc, registerEndpoint,       actorType, TransactionType.REGISTER,     false);
			addFixedEndpoint(sc, registerTlsEndpoint,    actorType, TransactionType.REGISTER,     true);
			addFixedEndpoint(sc, storedQueryEndpoint,    actorType, TransactionType.STORED_QUERY, false);
			addFixedEndpoint(sc, storedQueryTlsEndpoint, actorType, TransactionType.STORED_QUERY, true);
		}

		return new Simulator(sc);
	}

	public void asRecipient() { isRecipient = true; }

	protected void verifyActorConfigurationOptions(SimulatorConfig config) {
		SimulatorConfigElement ele = config.get(SimulatorConfig.UPDATE_METADATA_OPTION);
		if (ele == null)
			return;
		Boolean optionOn = ele.asBoolean();
				
		SimulatorConfigElement updateEndpointEle = config.get(updateEndpoint); 
		
		if (optionOn && updateEndpointEle == null) {
			 //option is enabled but no endpoint present - create it
			
			updateEndpointEle = new SimulatorConfigElement();
			updateEndpointEle.name = updateEndpoint;
			updateEndpointEle.type = ParamType.ENDPOINT;
			updateEndpointEle.transType = TransactionType.UPDATE; 
			updateEndpointEle.setValue(mkEndpoint(config, updateEndpointEle, ActorType.REGISTRY.getShortName(), false)); 
			addFixed(config, updateEndpointEle);
			 
			updateEndpointEle = new SimulatorConfigElement();
			updateEndpointEle.name = updateTlsEndpoint;
			updateEndpointEle.type = ParamType.ENDPOINT;
			updateEndpointEle.transType = TransactionType.UPDATE; 
			updateEndpointEle.setValue(mkEndpoint(config, updateEndpointEle, ActorType.REGISTRY.getShortName(), true)); 
			addFixed(config, updateEndpointEle);
			 
			
		} else if (!optionOn && updateEndpointEle != null) {
			// option is disabled but endpoint is present - delete it
			
			config.deleteFixedByName(updateEndpoint);
			
		}
	}
	
	public Site getActorSite(SimulatorConfig asc, Site site) {
		String siteName = asc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName);

		site.user = asc.getId().user;  // labels this site as coming from a sim
		site.user = asc.getId().user;  // labels this site as coming from a sim

		boolean isAsync = false;
		
		site.addTransaction(new TransactionBean(
				TransactionType.REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(registerEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.REGISTER.getCode(),
				RepositoryType.NONE,
				asc.get(registerTlsEndpoint).asString(), 
				true, 
				isAsync));

		site.addTransaction(new TransactionBean(
				TransactionType.STORED_QUERY.getCode(),
				RepositoryType.NONE,
				asc.get(storedQueryEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.STORED_QUERY.getCode(),
				RepositoryType.NONE,
				asc.get(storedQueryTlsEndpoint).asString(), 
				true, 
				isAsync));
		
		SimulatorConfigElement updateElement = asc.get(SimulatorConfig.UPDATE_METADATA_OPTION);
		if (updateElement.asBoolean()) {
			site.addTransaction(new TransactionBean(
					TransactionType.UPDATE.getCode(),
					RepositoryType.NONE,
					asc.get(updateEndpoint).asString(), 
					false, 
					isAsync));
			site.addTransaction(new TransactionBean(
					TransactionType.UPDATE.getCode(),
					RepositoryType.NONE,
					asc.get(updateTlsEndpoint).asString(), 
					true, 
					isAsync));
		}
		SimulatorConfigElement pifPortElement = asc.get(SimulatorConfig.PIF_PORT);
		site.pifPort = pifPortElement.asString();
		site.pifHost = Installation.installation().propertyServiceManager().getToolkitHost();

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}

	
}
