package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RegistryActorFactory extends ActorFactory {

	public static final String update_metadata_option = "Update_Metadata_Option";
	
	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.REGISTER,
				TransactionType.STORED_QUERY,
				TransactionType.UPDATE
				);
	
	public Simulator buildNew(SimManager simm, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		return buildNew(simm, null, configureBase);
	}

	public Simulator buildNew(SimManager simm, String simId, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		ActorType actorType = ActorType.REGISTRY;
		SimulatorConfig sc;
		if (configureBase)
			sc = configureBaseElements(actorType, simId);
		else
			sc = new SimulatorConfig();

		File codesFile = EnvSetting.getEnvSetting(simm.sessionId).getCodesFile();
		addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());

		addEditableConfig(sc, update_metadata_option, ParamType.BOOLEAN, false);
		addEditableConfig(sc, extraMetadataSupported, ParamType.BOOLEAN, true);
		addEditableEndpoint(sc, registerEndpoint,       actorType, TransactionType.REGISTER,     false);
		addEditableEndpoint(sc, registerTlsEndpoint,    actorType, TransactionType.REGISTER,     true);
		addEditableEndpoint(sc, storedQueryEndpoint,    actorType, TransactionType.STORED_QUERY, false);
		addEditableEndpoint(sc, storedQueryTlsEndpoint, actorType, TransactionType.STORED_QUERY, true);		

		return new Simulator(sc);
	}
	 
	protected void verifyActorConfigurationOptions(SimulatorConfig config) {
		SimulatorConfigElement ele = config.get(update_metadata_option);
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
		
		SimulatorConfigElement ele = asc.get(update_metadata_option);
		if (ele.asBoolean()) {
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

		return site;
	}

	public List<TransactionType> getIncomingTransactions() {
		return incomingTransactions;
	}

	
}
