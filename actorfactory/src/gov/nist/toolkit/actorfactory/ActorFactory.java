package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for simulators.  Technically ActorFactry is no longer accurate
 * since simulators are being built for things that are not IHE actors.
 * Oh well!
 * @author bill
 *
 */
public abstract class ActorFactory {

	protected abstract Simulator buildNew(SimManager simm, boolean configureBase) throws Exception;
	//	protected abstract List<SimulatorConfig> buildNew(Session session, SimulatorConfig asc) throws Exception;
	protected abstract void verifyActorConfigurationOptions(SimulatorConfig config);
	public abstract Site getActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException;
	public abstract List<TransactionType> getIncomingTransactions();
	//	protected abstract void addConfigElements(SimulatorConfig asc);

	static final Map<String /* ActorType.name */, ActorFactory> factories = new HashMap<String, ActorFactory>(); 
	static {
		factories.put(ActorType.REGISTRY.getName(),           new RegistryActorFactory());
		factories.put(ActorType.REPOSITORY.getName(),         new RepositoryActorFactory());
		factories.put(ActorType.DOCUMENT_RECIPIENT.getName(),  new RecipientActorFactory());
		factories.put(ActorType.REPOSITORY_REGISTRY.getName(), new RepositoryRegistryActorFactory());
		factories.put(ActorType.INITIATING_GATEWAY.getName(),  new IGActorFactory());
		factories.put(ActorType.RESPONDING_GATEWAY.getName(),  new RGActorFactory());
		factories.put(ActorType.DIRECT_SERVER.getName(),  new DirectActorFactory());
	}

	static public ActorFactory getActorFactory(ActorType at) {
		return factories.get(at.getName());
	}

	public static final String                         pnrEndpoint = "PnR_endpoint";
	public static final String                      pnrTlsEndpoint = "PnR_TLS_endpoint";
	public static final String                    retrieveEndpoint = "Retrieve_endpoint";
	public static final String                 retrieveTlsEndpoint = "Retrieve_TLS_endpoint";
	public static final String                    registerEndpoint = "Register_endpoint";
	public static final String                 registerTlsEndpoint = "Register_TLS_endpoint";
	public static final String                 storedQueryEndpoint = "StoredQuery_endpoint";
	public static final String              storedQueryTlsEndpoint = "StoredQuery_TLS_endpoint";
	public static final String                      updateEndpoint = "update_endpoint";
	public static final String                   updateTlsEndpoint = "update_TLS_endpoint";
	public static final String                        xcqrEndpoint = "XCQR_endpoint";
	public static final String                     xcqrTlsEndpoint = "XCQR_TLS_endpoint";
	public static final String                         xcqEndpoint = "XCQ_endpoint";
	public static final String                      xcqTlsEndpoint = "XCQ_TLS_endpoint";
	public static final String                         igqEndpoint = "IGQ_endpoint";
	public static final String                      igqTlsEndpoint = "IGQ_TLS_endpoint";
	public static final String                         xcrEndpoint = "XCR_endpoint";
	public static final String                      xcrTlsEndpoint = "XCR_TLS_endpoint";
	public static final String                         igrEndpoint = "IGR_endpoint";
	public static final String                      igrTlsEndpoint = "IGR_TLS_endpoint";
	public static final String                        xcpdEndpoint = "XCPD_endpoint";
	public static final String                     xcpdTlsEndpoint = "XCPD_TLS_endpoint";
	public static final String                    codesEnvironment = "Codes_Environment";
	public static final String              extraMetadataSupported = "Extra_Metadata_Supported";


	static final String creationTime = "Creation Time";


	public static final String repositoryUniqueId = "repositoryUniqueId";
	public static final String homeCommunityId = "homeCommunityId";
	static final String name = "Name";
	static final String isTls = "UseTLS";
	static final String owner = "Owner";
	static final String description = "Description";

	PropertyServiceManager propertyServiceMgr = null;
	//	protected Session session;
	SimManager simManager;

	static public ActorType getActorTypeFromName(String name) {
		return ActorType.findActor(name);
	}

	protected SimulatorConfig configureBaseElements(ActorType simType) {
		return configureBaseElements(simType, null);
	}

	protected SimulatorConfig configureBaseElements(ActorType simType, String newId) {
		if (newId == null)
			newId = getNewId();
		SimulatorConfig sc = new SimulatorConfig(newId, simType.getShortName(), SimDb.getNewExpiration(SimulatorConfig.class));

		return configureBaseElements(sc);
	}	

	SimulatorConfig configureBaseElements(SimulatorConfig sc) {
		SimulatorConfigElement ele;

		ele = new SimulatorConfigElement();
		ele.name = creationTime;
		ele.type = ParamType.TIME;
		ele.setValue(new Date().toString());
		addFixed(sc, ele);

		ele = new SimulatorConfigElement();
		ele.name = name;
		ele.type = ParamType.TEXT;
		ele.setValue("Private");
		addUser(sc, ele);

		return sc;
	}

	protected ActorFactory() {}

	protected void setSimManager(SimManager simManager) {
		this.simManager = simManager;
	}

	public ActorFactory(SimManager simManager) {
		this.simManager = simManager;
	}

	// Returns list since multiple simulators could be built as a grouping/cluster
	public Simulator buildNewSimulator(SimManager simm, String simtype, boolean save) throws Exception {

		ActorType at = ActorType.findActor(simtype);
		
		if (at == null)
			throw new NoSimException("Simulator type [" + simtype + "] does not exist");

		ActorFactory af = factories.get(at.getName());

		af.setSimManager(simManager);

		Simulator simulator = af.buildNew(simm, true);

		if (save) {
			for (SimulatorConfig conf : simulator.getConfigs())
				saveConfiguration(conf);
		}

		return simulator;
	}

	//	void verifyConfigurationOptions(SimulatorConfig config) throws Exception {
	//
	//		ActorFactory af = SimManager.getActorFactory(config);
	//
	//		af.verifyActorConfigurationOptions(config);
	//
	//	}

	public List<SimulatorConfig> checkExpiration(List<SimulatorConfig> configs) {
		List<SimulatorConfig> remove = new ArrayList<SimulatorConfig>();

		for (SimulatorConfig sc : configs) {
			if (sc.checkExpiration())
				remove.add(sc);
		}
		configs.removeAll(remove);
		return configs;
	}


	public String getNewId() {
		String id = UuidAllocator.allocate();
		String[] parts = id.split(":");
		id = parts[2];
		//		id = id.replaceAll("-", "_");

		return id;
	}

	String mkEndpoint(SimulatorConfig asc, SimulatorConfigElement ele, boolean isTLS) {
		return mkEndpoint(asc, ele, asc.getType().toLowerCase(), isTLS);
	}

	protected String mkEndpoint(SimulatorConfig asc, SimulatorConfigElement ele, String actor, boolean isTLS) {
		String transtype = SimDb.getTransactionDirName(ele.transType);

		String contextName = Installation.installation().tkProps.get("toolkit.servlet.context", "xdstools2");

		return "http"
		+ ((isTLS) ? "s" : "")
		+ "://" 
		+ Installation.installation().propertyServiceManager().getToolkitHost() 
		+ ":" 
		+ ((isTLS) ? Installation.installation().propertyServiceManager().getToolkitTlsPort() : Installation.installation().propertyServiceManager().getToolkitPort()) 
		+ "/"  
		+ contextName  
		+ "/sim/" 
		+ asc.getId() 
		+ "/" +
		actor           //asc.getType().toLowerCase()
		+ "/" 
		+ transtype;
	}

	public void saveConfiguration(SimulatorConfig config) throws Exception {
		verifyActorConfigurationOptions(config);

		if (config.getType().equals(ActorType.INITIATING_GATEWAY.getName())) {
			// must load up XCQ and XCR endpoints for simulator to use
			config.remoteSites = new ArrayList<Site>();

			Sites sites = simManager.getAllSites();
			for (String remote : config.remoteSiteNames) {
				Site site = sites.getSite(remote);
				config.remoteSites.add(site);
			}
		}

		SimDb simdb = SimDb.mkSim(Installation.installation().simDbFile(), config.getId(), config.getType());
		File simCntlFile = simdb.getSimulatorControlFile();
		new SimulatorConfigIo().save(config, simCntlFile.toString());   //config.save(simCntlFile.toString());
	}


	public void deleteSimulator(SimulatorConfig config) throws IOException {
		SimDb simdb;
		try {
			simdb = new SimDb(config.getId());
		} catch (NoSimException e) {
			return;		
		}
		File simDir = simdb.getSimDir();
		simdb.delete(simDir);
	}

	public boolean simExists(SimulatorConfig config) throws IOException {
		SimDb simdb;
		try {
			simdb = new SimDb(config.getId());
		} catch (NoSimException e) {
			return false;
		}
		File simDir = simdb.getSimDir();
		return simDir.exists();
	}

	/**
	 * Load simulators - IOException if sim not found
	 * @param ids
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSimException 
	 */
	public List<SimulatorConfig> loadSimulators(List<String> ids) throws IOException, ClassNotFoundException, NoSimException {
		List<SimulatorConfig> configs = new ArrayList<SimulatorConfig>();

		for (String id : ids) {
			SimDb simdb = new SimDb(id);
			File simCntlFile = simdb.getSimulatorControlFile();
			SimulatorConfig config = restoreSimulator(simCntlFile.toString());
			configs.add(config);
		}

		return configs;
	}

	/**
	 * Load simulators - ignore sims not found (length(simlist) < length(idlist))
	 * @param ids
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<SimulatorConfig> loadAvailableSimulators(List<String> ids) throws IOException, ClassNotFoundException {
		List<SimulatorConfig> configs = new ArrayList<SimulatorConfig>();

		for (String id : ids) {
			try {
				SimDb simdb = new SimDb(id);
				File simCntlFile = simdb.getSimulatorControlFile();
				SimulatorConfig config = restoreSimulator(simCntlFile.toString());
				configs.add(config);
			} catch (NoSimException e) {
				continue;
			}
		}

		return configs;
	}

	SimulatorConfig restoreSimulator(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		SimulatorConfig config;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		config = (SimulatorConfig)in.readObject();
		in.close();

		return config;
	}

	public SimulatorConfig getSimulator(String simid) throws IOException, ClassNotFoundException, NoSimException {
		SimDb simdb = new SimDb(simid);
		File simCntlFile = simdb.getSimulatorControlFile();
		SimulatorConfig config = restoreSimulator(simCntlFile.toString());
		return config;
	}	

	public SimulatorConfig getSimConfig(File simDbFile, String simulatorId) throws IOException, ClassNotFoundException, NoSimException {
		SimDb simdb = new SimDb(simDbFile, simulatorId, null, null);
		File simCntlFile = simdb.getSimulatorControlFile();
		SimulatorConfig config = restoreSimulator(simCntlFile.toString());
		return config;
	}

	protected boolean isEndpointSecure(String endpoint) {
		return endpoint.startsWith("https");
	}

	protected List<SimulatorConfig> asList(SimulatorConfig asc) {
		List<SimulatorConfig> ascs = new ArrayList<SimulatorConfig>();
		ascs.add(asc);
		return ascs;
	}

	protected void addFixed(SimulatorConfig sc, SimulatorConfigElement ele) {
		ele.setEditable(false);
		sc.elements().add(ele);
	}

	void addUser(SimulatorConfig sc, SimulatorConfigElement ele) {
		ele.setEditable(true);
		sc.elements().add(ele);
	}

	public void addEditableConfig(SimulatorConfig sc, String name, ParamType type, Boolean value) {
		addUser(sc, new SimulatorConfigElement(name, type, value));
	}

	public void addEditableConfig(SimulatorConfig sc, String name, ParamType type, String value) {
		addUser(sc, new SimulatorConfigElement(name, type, value));
	}

	public void addFixedConfig(SimulatorConfig sc, String name, ParamType type, Boolean value) {
		addFixed(sc, new SimulatorConfigElement(name, type, value));
	}

	public void addFixedConfig(SimulatorConfig sc, String name, ParamType type, String value) {
		addFixed(sc, new SimulatorConfigElement(name, type, value));
	}

	public void addEditableEndpoint(SimulatorConfig sc, String endpointName, ActorType actorType, TransactionType transactionType, boolean tls) {
		SimulatorConfigElement ele = new SimulatorConfigElement();
		ele.name = endpointName;
		ele.type = ParamType.ENDPOINT;
		ele.transType = transactionType;
		ele.setValue(mkEndpoint(sc, ele, actorType.getShortName(), tls));
		addUser(sc, ele);
	}

	public void addFixedEndpoint(SimulatorConfig sc, String endpointName, ActorType actorType, TransactionType transactionType, boolean tls) {
		SimulatorConfigElement ele = new SimulatorConfigElement();
		ele.name = endpointName;
		ele.type = ParamType.ENDPOINT;
		ele.transType = transactionType;
		ele.setValue(mkEndpoint(sc, ele, actorType.getShortName(), tls));
		addFixed(sc, ele);
	}


}
