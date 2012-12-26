package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class RGActorFactory extends ActorFactory {
	
	static final String homeCommunityIdBase = "urn:oid:1.1.4567334.1.";
	static int homeCommunityIdIncr = 1;

	static String getNewHomeCommunityId() {
		return homeCommunityIdBase + homeCommunityIdIncr++;
	}

	
	static final List<TransactionType> incomingTransactions = 
		Arrays.asList(
				TransactionType.XC_QUERY, 
				TransactionType.XC_RETRIEVE);

	RegistryActorFactory registryActorFactory;
	RepositoryActorFactory repositoryActorFactory;

	protected List<SimulatorConfig> buildNew(SimManager simm, boolean configureBase) throws EnvironmentNotSelectedException, NoSessionException {
		ActorType actorType = ActorType.RESPONDING_GATEWAY;
		SimulatorConfig sc; 
		if (configureBase)
			sc = configureBaseElements(actorType);
		else
			sc = new SimulatorConfig();
		
		String simId = sc.getId();

		File codesFile = simm.getCodesFile();
		addEditableConfig(sc, codesEnvironment, ParamType.SELECTION, codesFile.toString());
		addEditableConfig(sc, homeCommunityId, ParamType.TEXT, getNewHomeCommunityId());
		
		addFixedEndpoint(sc, xcqEndpoint, actorType, TransactionType.XC_QUERY, false);
		addFixedEndpoint(sc, xcqTlsEndpoint, actorType, TransactionType.XC_QUERY, true);
		addFixedEndpoint(sc, xcrEndpoint, actorType, TransactionType.XC_RETRIEVE, false);
		addFixedEndpoint(sc, xcrTlsEndpoint, actorType, TransactionType.XC_RETRIEVE, true);
		addFixedEndpoint(sc, xcpdEndpoint, actorType, TransactionType.XC_PATIENT_DISCOVERY, false);
		addFixedEndpoint(sc, xcpdTlsEndpoint, actorType, TransactionType.XC_PATIENT_DISCOVERY, true);
		
		// This needs to be grouped with a Document Registry
		registryActorFactory = new RegistryActorFactory();
		SimulatorConfig registryConfig = registryActorFactory.buildNew(simm, simId, true).get(0);   // was false
		
		// This needs to be grouped with a Document Repository also
		repositoryActorFactory = new RepositoryActorFactory();
		SimulatorConfig repositoryConfig = repositoryActorFactory.buildNew(simm, simId, true).get(0);    //was false
		
		sc.add(registryConfig); // this adds the individual SimulatorConfigElements to the RG SimulatorConfig
								// their identity as belonging to the Registry or Repository is lost
								// which means the SimServlet cannot find them when a message comes in
		sc.add(repositoryConfig);
		
		return asList(sc);
	}

	protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
		
	}

	public Site getActorSite(SimulatorConfig sc, Site site) {
		String siteName = sc.getDefaultName();
		
		if (site == null)
			site = new Site(siteName);
		
		boolean isAsync = false;
		
		site.addTransaction(new TransactionBean(
				TransactionType.XC_QUERY.getCode(),
				RepositoryType.NONE,
				sc.get(xcqEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.XC_QUERY.getCode(),
				RepositoryType.NONE,
				sc.get(xcqTlsEndpoint).asString(), 
				true, 
				isAsync));
		
		site.addTransaction(new TransactionBean(
				TransactionType.XC_RETRIEVE.getCode(),
				RepositoryType.NONE,
				sc.get(xcrEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.XC_RETRIEVE.getCode(),
				RepositoryType.NONE,
				sc.get(xcrTlsEndpoint).asString(), 
				true, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.XC_PATIENT_DISCOVERY.getCode(),
				RepositoryType.NONE,
				sc.get(xcpdEndpoint).asString(), 
				false, 
				isAsync));
		site.addTransaction(new TransactionBean(
				TransactionType.XC_PATIENT_DISCOVERY.getCode(),
				RepositoryType.NONE,
				sc.get(xcpdTlsEndpoint).asString(), 
				true, 
				isAsync));
		
		
		site.setHome(sc.get(homeCommunityId).asString());
		
		registryActorFactory.getActorSite(sc, site);
		repositoryActorFactory.getActorSite(sc, site);
		
		return site;
	}

	public  List<TransactionType> getIncomingTransactions() {
		List<TransactionType> tt = new ArrayList<TransactionType>();
		tt.addAll(incomingTransactions);
		tt.addAll(registryActorFactory.getIncomingTransactions());
		tt.addAll(repositoryActorFactory.getIncomingTransactions());
		return tt;
	}


}
