package gov.nist.toolkit.xdstools2.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.ExternalCacheManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.simulators.support.od.TransactionUtil;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.testengine.scripts.CodesUpdater;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.client.NoServletSessionException;
import gov.nist.toolkit.xdstools2.client.RegistryStatus;
import gov.nist.toolkit.xdstools2.client.RepositoryStatus;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.server.serviceManager.DashboardServiceManager;
import gov.nist.toolkit.xdstools2.server.serviceManager.GazelleServiceManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ToolkitServiceImpl extends RemoteServiceServlet implements
		ToolkitService {
	static String schematronHome = null;
	ServletContext context = null;
//	static File warHome = null;

	static Logger logger = Logger.getLogger(ToolkitServiceImpl.class);

	// Individual service requests from browser are delegated to one of these

	// ServiceManagers for execution
	public QueryServiceManager queryServiceManager;
	public SiteServiceManager siteServiceManager;
	public DashboardServiceManager dashboardServiceManager;
	public GazelleServiceManager gazelleServiceManager;

	// Next two constructors exist to initialize MessageValidatorFactoryFactory which olds
	// a reference to an instance of this class. This is necessary to getRetrievedDocumentsModel around a circular
	// reference in the build tree

	public ToolkitServiceImpl() {
		siteServiceManager = SiteServiceManager.getSiteServiceManager();   // One copy shared between sessions
		System.out.println("MessageValidatorFactory()");
		if (MessageValidatorFactoryFactory.messageValidatorFactory2I == null) {
			MessageValidatorFactoryFactory.messageValidatorFactory2I = new MessageValidatorFactory("a");
		}
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Site Services
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public List<String> getSiteNames(boolean reload, boolean simAlso)  throws NoServletSessionException { return siteServiceManager.getSiteNames(session().getId(), reload, simAlso); }
	public Collection<Site> getAllSites() throws Exception { return siteServiceManager.getAllSites(session().getId()); }
	public List<String> reloadSites(boolean simAlso) throws FactoryConfigurationError, Exception { return siteServiceManager.reloadSites(session().getId(), simAlso); }
	public Site getSite(String siteName) throws Exception { return siteServiceManager.getSite(session().getId(), siteName); }
	public String saveSite(Site site) throws Exception { return siteServiceManager.saveSite(session().getId(), site); }
	public String deleteSite(String siteName) throws Exception { return siteServiceManager.deleteSite(session().getId(), siteName); }
	//	public String getHome() throws Exception { return session().getHome(); }
	public List<String> getUpdateNames()  throws NoServletSessionException { return siteServiceManager.getUpdateNames(session().getId()); }
	public TransactionOfferings getTransactionOfferings() throws Exception { return siteServiceManager.getTransactionOfferings(session().getId()); }
	public List<String> reloadExternalSites() throws FactoryConfigurationError, Exception { return siteServiceManager.reloadCommonSites(); }
	public List<String> getRegistryNames()  throws NoServletSessionException { return siteServiceManager.getRegistryNames(session().getId()); }
	public List<String> getRepositoryNames()  throws NoServletSessionException { return siteServiceManager.getRepositoryNames(session().getId()); }
	public List<String> getRGNames()  throws NoServletSessionException { return siteServiceManager.getRGNames(session().getId()); }
	public List<String> getIGNames()  throws NoServletSessionException { return siteServiceManager.getIGNames(session().getId()); }
	public List<String> getActorTypeNames()  throws NoServletSessionException { return siteServiceManager.getActorTypeNames(session().getId()); }
	public List<String> getSiteNamesWithRG() throws Exception { return siteServiceManager.getSiteNamesWithRG(session().getId()); }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Query Services
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public List<Result> registerAndQuery(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().registerAndQuery(site, pid); }
	public List<Result> lifecycleValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().lifecycleValidation(site, pid); }
	public List<Result> folderValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().folderValidation(site, pid); }
	public List<Result> submitRegistryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRegistryTestdata(testSessionName,site, datasetName, pid); }
	public List<Result> submitRepositoryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRepositoryTestdata(testSessionName,site, datasetName, pid); }
	public List<Result> submitXDRTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitXDRTestdata(testSessionName,site, datasetName, pid); }
	public List<Result> provideAndRetrieve(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().provideAndRetrieve(site, pid); }
	public List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) throws NoServletSessionException  { return session().queryServiceManager().findDocuments(site, pid, onDemand); }
	public List<Result> findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds) throws NoServletSessionException  { return session().queryServiceManager().findDocumentsByRefId(site, pid, refIds); }
	public List<Result> getDocuments(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getDocuments(site, aids); }
	public List<Result> findFolders(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().findFolders(site, pid); }
	public List<Result> getFolders(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolders(site, aids); }
	public List<Result> getFoldersForDocument(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFoldersForDocument(site, aids); }
	public List<Result> getFolderAndContents(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolderAndContents(site, aids); }
	public List<Result> getAssociations(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getAssociations(site, ids); }
	public List<Result> getObjects(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getObjects(site, ids); }
	public List<Result> getSubmissionSets(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getSubmissionSets(site, aids); }
	public List<Result> getSSandContents(SiteSpec site, String ssid) throws NoServletSessionException  { return session().queryServiceManager().getSSandContents(site, ssid); }
	public List<Result> srcStoresDocVal(SiteSpec site, String ssid) throws NoServletSessionException  { return session().queryServiceManager().srcStoresDocVal(site, ssid); }
	public List<Result> retrieveDocument(SiteSpec site, Uids uids) throws Exception { return session().queryServiceManager().retrieveDocument(site, uids); }
	public List<Result> retrieveImagingDocSet(SiteSpec site, Uids uids, String studyRequest, String transferSyntax) throws Exception { return session().queryServiceManager().retrieveImagingDocSet(site, uids, studyRequest, transferSyntax); }

	public List<Result> getRelated(SiteSpec site, ObjectRef or,	List<String> assocs) throws NoServletSessionException  { return session().queryServiceManager().getRelated(site, or, assocs); }
	public List<Result> getAll(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException  { return session().queryServiceManager().getAll(site, pid, codesSpec); }
	public List<Result> findDocuments2(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException  {
		System.out.println("Running findDocuments2 service");
		return session().queryServiceManager().findDocuments2(site, pid, codesSpec); }


	public List<Result> findPatient(SiteSpec site, String firstName,
									String secondName, String lastName, String suffix, String gender,
									String dob, String ssn, String pid, String homeAddress1,
									String homeAddress2, String homeCity, String homeState,
									String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName,
									String mothersLastName, String mothersSuffix, String homePhone,
									String workPhone, String principleCareProvider, String pob,
									String pobAddress1, String pobAddress2, String pobCity,
									String pobState, String pobZip, String pobCountry) {
		return queryServiceManager.findPatient(site, firstName, secondName, lastName, suffix, gender, dob, ssn, pid,
				homeAddress1, homeAddress2, homeCity, homeState, homeZip, homeCountry,
				mothersFirstName, mothersSecondName, mothersLastName, mothersSuffix,
				homePhone, workPhone, principleCareProvider,
				pob, pobAddress1, pobAddress2, pobCity, pobState, pobZip, pobCountry);
	}
	public List<Result> mpqFindDocuments(SiteSpec site, String pid,
										 List<String> classCodes, List<String> hcftCodes,
										 List<String> eventCodes) throws NoServletSessionException {
		return session().queryServiceManager().mpqFindDocuments(site, pid, classCodes, hcftCodes,
				eventCodes);
	}
	public List<Result> mpqFindDocuments(SiteSpec site, String pid,
										 Map<String, List<String>> codesSpec) throws NoServletSessionException {
		return session().queryServiceManager().mpqFindDocuments(site, pid, codesSpec);
	}

	public List<Result> getLastMetadata() { return queryServiceManager.getLastMetadata(); }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Test Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// New - Loads or reloads test data
	public List<Test> reloadAllTestResults(String sessionName) throws Exception { return session().xdsTestServiceManager().reloadAllTestResults(sessionName); }
	public List<TestInstance> getTestlogListing(String sessionName) throws Exception { return session().xdsTestServiceManager().getTestlogListing(sessionName); }
	public Map<String, Result> getTestResults(List<TestInstance> testIds, String testSession)  throws NoServletSessionException { return session().xdsTestServiceManager().getTestResults(testIds, testSession); }
	public String setMesaTestSession(String sessionName)  throws NoServletSessionException { session().xdsTestServiceManager().setMesaTestSession(sessionName); return sessionName;}
	public List<String> getMesaTestSessionNames() throws Exception { return session().xdsTestServiceManager().getMesaTestSessionNames(); }
	public boolean addMesaTestSession(String name) throws Exception { return session().xdsTestServiceManager().addMesaTestSession(name); }
	public boolean delMesaTestSession(String name) throws Exception { return session().xdsTestServiceManager().delMesaTestSession(name); }
	public String getNewPatientId(String assigningAuthority)  throws NoServletSessionException { return session().xdsTestServiceManager().getNewPatientId(assigningAuthority); }
	public String delTestResults(List<TestInstance> testInstances, String testSession )  throws NoServletSessionException { session().xdsTestServiceManager().delTestResults(testInstances, testSession); return ""; }
	public List<Test> deleteAllTestResults(Site site) throws NoServletSessionException { return session().xdsTestServiceManager().deleteAllTestResults(getSession().getMesaSessionName(), site); }
	public Test deleteSingleTestResult(Site site, int testId) throws NoServletSessionException { return session().xdsTestServiceManager().deleteSingleTestResult(getSession().getMesaSessionName(), site, testId); }
	public List<Test> runAllTests(Site site) throws NoServletSessionException { return session().xdsTestServiceManager().runAllTests(getSession().getMesaSessionName(), site); }
	public Test runSingleTest(Site site, int testId) throws NoServletSessionException { return session().xdsTestServiceManager().runSingleTest(getSession().getMesaSessionName(), site, testId); }

	public String getTestReadme(String testSession,String test) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestReadme(test);
    }
	public RawResponse buildIgTestOrchestration(IgOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildIgTestEnvironment(s, request);
	}
	public RawResponse buildRgTestOrchestration(RgOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildRgTestEnvironment(s, request);
	}

	/**
	 * Get list of section names defined for the test in the order they should be executed
     * @param testSession test session name (mesa session name)
	 * @param test test name
	 * @return list of sections
	 * @throws Exception if something goes wrong
	 */
	public List<String> getTestIndex(String testSession,String test) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestIndex(test);
    }

	/**
	 * Get map of (collection name, collection description) pairs contained in testkit
     * @param testSession test session name (mesa session name)
	 * @param collectionSetName the collection name
	 * @return the map
	 * @throws Exception is something goes wrong
	 */
	public Map<String, String> getCollectionNames(String testSession, String collectionSetName) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getCollectionNames(collectionSetName);
    }
	public List<Result> getLogContent(String sessionName, TestInstance testInstance) throws Exception { return session().xdsTestServiceManager().getLogContent(sessionName, testInstance); }
	public List<Result> runMesaTest(String environmentName,String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure)  throws NoServletSessionException {
		return session().xdsTestServiceManager().runMesaTest(environmentName,mesaTestSession, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
	}
	public TestLogs getRawLogs(TestInstance logId)  throws NoServletSessionException { return session().xdsTestServiceManager().getRawLogs(logId); }
	public List<String> getTestdataSetListing(String environmentName, String testSessionName, String testdataSetName)  throws NoServletSessionException {
        return session().xdsTestServiceManager().getTestdataSetListing(environmentName,testSessionName,testdataSetName);
    }
	public String getTestplanAsText(String testSession,TestInstance testInstance, String section) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestplanAsText(testInstance, section);
    }
	public CodesResult getCodesConfiguration()  throws NoServletSessionException { return session().xdsTestServiceManager().getCodesConfiguration(); }

	/**
	 * Get test names and descriptions from a named test collection
     * @param testsessionName test session name (mesa session name)
	 * @param collectionSetName name of directory holding tc files (collection definitions)
	 * @param collectionName collection name within the directory
	 * @return testname ==> description mapping
	 * @throws Exception if something goes wrong
	 */
	public Map<String, String> getCollection(String testsessionName,String collectionSetName, String collectionName) throws Exception {
        session().setMesaSessionName(testsessionName);
        return session().xdsTestServiceManager().getCollection(collectionSetName, collectionName);
    }
	public boolean isPrivateMesaTesting()  throws NoServletSessionException { return session().xdsTestServiceManager().isPrivateMesaTesting(); }
	public List<Result> sendPidToRegistry(SiteSpec site, Pid pid) throws NoServletSessionException { return session().xdsTestServiceManager().sendPidToRegistry(site, pid); }

	/**
	 * This method copies the default testkit to a selected environment and triggers a code update based on
	 * the affinity domain configuration file (codes.xml) located in the selected environment.
	 * @param selectedEnvironmentName target environment for the testkit.
	 * @return update output as a String
	 */
	@Override
	public String configureTestkit(String selectedEnvironmentName) {
		File environmentFile = Installation.installation().environmentFile(selectedEnvironmentName);
		File defaultTestkit = Installation.installation().testkitFile();
		CodesUpdater updater = new CodesUpdater();
		updater.run(environmentFile.getAbsolutePath(),defaultTestkit.getAbsolutePath());
		return updater.getOutput();
	}

	/**
	 * This method tests if there already is a testkit configured in a selected environment.
	 * @param selectedEnvironment name of the selected environment.
	 * @return boolean
	 */
	@Override
	public boolean doesTestkitExist(String selectedEnvironment) {
		File environmentFile = Installation.installation().environmentFile(selectedEnvironment);
		File testkit=new File(environmentFile,"testkit");
		return testkit.exists();
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Gazelle Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public String reloadSystemFromGazelle(String systemName) throws Exception { return new GazelleServiceManager(session()).reloadSystemFromGazelle(systemName); }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Environment management
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public List<String> getEnvironmentNames() throws NoServletSessionException { return session().getEnvironmentNames(); }
	public String setEnvironment(String name) throws NoServletSessionException { session().setEnvironment(name); return name; }
	public String getCurrentEnvironment() throws NoServletSessionException { return session().getCurrentEnvironment(); }
	public String getDefaultEnvironment()  throws NoServletSessionException  {
		String defaultEnvironment = Installation.installation().propertyServiceManager().getDefaultEnvironment();
		if (Session.environmentExists(defaultEnvironment))
			return defaultEnvironment;
		return Installation.DEFAULT_ENVIRONMENT_NAME;
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Session
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public Map<String, String> getSessionProperties() throws NoServletSessionException { return session().getSessionPropertiesAsMap(); }
	public void setSessionProperties(Map<String, String> props) throws NoServletSessionException { session().setSessionProperties(props); }
	public Pid createPid(String assigningAuthority) throws NoServletSessionException { return session().allocateNewPid(assigningAuthority); }
	public String getAssigningAuthority() throws Exception { return session().getAssigningAuthority(); }
	public List<String> getAssigningAuthorities() throws Exception { return session().getAssigningAuthorities(); }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Property Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public TkProps getTkProps() throws NoServletSessionException { return session().tkProps(); }
	public String getDefaultAssigningAuthority()  throws NoServletSessionException { return Installation.installation().propertyServiceManager().getDefaultAssigningAuthority(); }
	public String getImplementationVersion() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().getImplementationVersion(); }
	public Map<String, String> getToolkitProperties()  throws NoServletSessionException { return Installation.installation().propertyServiceManager().getToolkitProperties(); }
	public boolean isGazelleConfigFeedEnabled() throws NoServletSessionException  { return SiteServiceManager.getSiteServiceManager().useGazelleConfigFeed(); }
	//	public String getToolkitEnableNwHIN() { return propertyServiceManager.getToolkitEnableNwHIN(); }
	public String setToolkitProperties(Map<String, String> props) throws Exception { return setToolkitPropertiesImpl(props); }
	public String getAdminPassword() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().getAdminPassword(); }
	public boolean reloadPropertyFile() throws NoServletSessionException  { return Installation.installation().propertyServiceManager().reloadPropertyFile(); }
	public String getAttributeValue(String username, String attName) throws Exception { return Installation.installation().propertyServiceManager().getAttributeValue(username, attName); }
	public void setAttributeValue(String username, String attName, String attValue) throws Exception { Installation.installation().propertyServiceManager().setAttributeValue(username, attName, attValue); }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Simulator Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public String putSimConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).saveSimConfig(config); }
	// this deletes a simulator
	public String deleteConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).deleteConfig(config); }
	public void renameSimFile(String simFileSpec, String newSimFileSpec) throws Exception { new SimulatorServiceManager(session()).renameSimFile(simFileSpec, newSimFileSpec); }
	public String getSimulatorEndpoint() throws NoServletSessionException { return new SimulatorServiceManager(session()).getSimulatorEndpoint(); }
	public MessageValidationResults executeSimMessage(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).executeSimMessage(simFileSpec); }
	public List<TransactionInstance> getTransInstances(SimId simid, String xactor, String trans) throws Exception { return new SimulatorServiceManager(session()).getTransInstances(simid, xactor, trans); }
	public String getTransactionRequest(SimId simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionRequest(simid, actor, trans, event); }
	public String getTransactionResponse(SimId simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionResponse(simid, actor, trans, event); }
	public int removeOldSimulators() throws NoServletSessionException { return new SimulatorServiceManager(session()).removeOldSimulators(); }
	public List<Result> getSelectedMessage(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessage(simFileSpec); }
	public List<Result> getSelectedMessageResponse(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessageResponse(simFileSpec); }
	public Map<String, SimId> getActorSimulatorNameMap() throws NoServletSessionException { return new SimulatorServiceManager(session()).getSimulatorNameMap(); }
	public MessageValidationResults validateMessage(ValidationContext vc) throws NoServletSessionException, EnvironmentNotSelectedClientException { return new SimulatorServiceManager(session()).validateMessage(vc); }
	public List<SimulatorConfig> getSimConfigs(List<SimId> ids) throws Exception { return new SimulatorServiceManager(session()).getSimConfigs(ids); }
	public List<SimulatorConfig> getAllSimConfigs(String user) throws Exception { return new SimulatorServiceManager(session()).getAllSimConfigs(user); }
	public Simulator getNewSimulator(String actorTypeName, SimId simId) throws Exception { return new SimulatorServiceManager(session()).getNewSimulator(actorTypeName, simId); }
	public void deleteSimFile(String simFileSpec) throws Exception { new SimulatorServiceManager(session()).deleteSimFile(simFileSpec); }
	public List<String> getTransactionsForSimulator(SimId simid) throws Exception { return new SimulatorServiceManager(session()).getTransactionsForSimulator(simid); }
	public List<SimulatorStats> getSimulatorStats(List<SimId> simids) throws Exception { return new SimulatorServiceManager(session()).getSimulatorStats(simids); }
	public String getTransactionLog(SimId simid, String actor, String trans, String event) throws NoServletSessionException { return new SimulatorServiceManager(session()).getTransactionLog(simid, actor, trans, event); }

	public List<Pid> getPatientIds(SimId simId) throws Exception { return new SimulatorServiceManager(session()).getPatientIds(simId); }
	public String addPatientIds(SimId simId, List<Pid> pids) throws Exception { return new SimulatorServiceManager(session()).addPatientIds(simId, pids); }
	public boolean deletePatientIds(SimId simId, List<Pid> pids) throws Exception { return new SimulatorServiceManager(session()).deletePatientIds(simId, pids); }

	public Result getSimulatorEventRequest(TransactionInstance ti) throws Exception {
		return new SimulatorServiceManager(session()).getSimulatorEventRequestAsResult(ti);
	}
	public Result getSimulatorEventResponse(TransactionInstance ti) throws Exception {
		return new SimulatorServiceManager(session()).getSimulatorEventResponseAsResult(ti);
	}
	public List<String> getTransactionErrorCodeRefs(String transactionName, Severity severity) throws Exception {
		List<String> refs = TransactionErrorCodeDbLoader.LOAD().getRefsByTransaction(TransactionType.find(transactionName), severity);
		logger.info(": getTransactionErrorCodeRefs(" + transactionName + ") => " + refs.size() + " codes");
		return refs;
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Dashboard Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public List<RegistryStatus> getDashboardRegistryData() throws Exception { return new DashboardServiceManager(session()).getDashboardRegistryData(); }
	public List<RepositoryStatus> getDashboardRepositoryData() throws Exception { return new DashboardServiceManager(session()).getDashboardRepositoryData(); }


	// Other support calls

	public String setToolkitPropertiesImpl(Map<String, String> props)
			throws Exception {
		logger.debug(": " + "setToolkitProperties");
		logger.debug(describeProperties(props));
		try {
			// verify External_Cache points to a writable directory
			String eCache = props.get("External_Cache");
			File eCacheFile = new File(eCache);
			if (!eCacheFile.exists() || !eCacheFile.isDirectory())
				throw new IOException("Cannot save toolkit properties: property External_Cache does not point to an existing directory");
			if (!eCacheFile.canWrite())
				throw new IOException("Cannot save toolkit properties: property External_Cache points to a directory that is not writable");

//            File warhome = Installation.installation().warHome();
			new PropertyServiceManager().getPropertyManager().update(props);
			reloadPropertyFile();
//		Installation.installation().externalCache(eCacheFile);
			ExternalCacheManager.reinitialize(eCacheFile);
			try {
				TkLoader.tkProps(Installation.installation().getTkPropsFile());
			} catch (Throwable t) {

			}
		} catch (Exception e) {
			throw new Exception(ExceptionUtil.exception_details(e));
		}
		return "";
	}

	String describeProperties(Map<String, String> props) {
		StringBuilder buf = new StringBuilder();

		for (String key : props.keySet()) buf.append(key).append(" = ").append(props.get(key)).append("\n");

		return buf.toString();
	}


	public void discoverServletContextName() {
		try {
			if (context == null)
				context = getServletContext();
		} catch (Exception e) {
		}
		if (context == null) {
			logger.info("Context is null");
		}
		logger.info("Context Name is " + context.getServletContextName());
		logger.info("Context Path is " + context.getContextPath());
		Installation.installation().setServletContextName(context.getContextPath());
	}

	public ServletContext servletContext() {
		discoverServletContextName();
		// this gets called from the initialization section of SimServlet
		// for access to properties.  This code is not expected to work correct.
		// Just don't throw exceptions that are not helpful
		try {
			if (context == null)
				context = getServletContext();
		} catch (Exception e) {

		}
		if (context != null && Installation.installation().warHome() == null) {

			File warHome = new File(context.getRealPath("/"));
			System.setProperty("warHome", warHome.toString());
			logger.info("warHome [ToolkitServiceImpl]: " + warHome);
			Installation.installation().warHome(warHome);
		}
		return context;
	}

	// Used only for non-servlet use (Dashboard is good example)
	static public final String sessionVarName = "MySession";
	String sessionID = null;

	public String getSessionId() {
		if (sessionID != null)
			return sessionID;
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession hsession = request.getSession();
		return hsession.getId();
	}

	public String getSessionIdIfAvailable() {
		try {
			return getSessionId();
		} catch (Exception e) {
			return "";
		}
	}


	Session standAloneSession = null;  // needed for standalone use not part of servlet

	public void setStandAloneSession(Session s) {
		standAloneSession = s;
	}

	// This exception is passable to the GUI.  The server side exception
	// is NoSessionException
	public Session session() throws NoServletSessionException {
		Session s = getSession();
		if (s == null)
			throw new NoServletSessionException("");
		return s;
	}


	public Session getSession() {
		HttpServletRequest request = this.getThreadLocalRequest();
		return getSession(request);
	}

	public Session getSession(HttpServletRequest request) {
		if (request == null && standAloneSession != null) {
			// not running interactively - maybe part of Dashboard
			return standAloneSession;
		}

		Session s = null;
		HttpSession hsession = null;
		if (request != null) {
			hsession = request.getSession();
			s = (Session) hsession.getAttribute(sessionVarName);
			if (s != null)
				return s;
			servletContext();
		}

		// Force short session timeout for testing
//		hsession.setMaxInactiveInterval(60/4);    // one quarter minute

		//******************************************
		//
		// New session object to be created
		//
		//******************************************
		File warHome = null;
		if (s == null) {
			ServletContext sc = servletContext();
			warHome = Installation.installation().warHome();
			if (sc != null && warHome == null) {
				warHome = new File(sc.getRealPath("/"));
				Installation.installation().warHome(warHome);
				System.setProperty("warHome", warHome.toString());
				System.out.print("warHome [ToolkitServiceImp]: " + warHome);
				Installation.installation().warHome(warHome);
			}
			if (warHome != null)
				System.setProperty("warHome", warHome.toString());

			if (warHome != null) {
				s = new Session(warHome, getSessionId());
				if (hsession != null) {
					s.setSessionId(hsession.getId());
					s.addSession();
					hsession.setAttribute(sessionVarName, s);
				} else
					s.setSessionId("mysession");
			}
		}

		if (request != null) {
			if (s.getIpAddr() == null) {
				s.setIpAddr(request.getRemoteHost());
			}

			s.setServerSpec(request.getLocalName(),
					String.valueOf(request.getLocalPort()));
		}

		if (warHome != null) {
			if (SchemaValidation.toolkitSchemaLocation == null) {
				SchemaValidation.toolkitSchemaLocation = warHome + File.separator + "toolkitx" + File.separator + "schema";
			}
		}

		return s;
	}

	public String getLastFilename() {
		return getSession().getlastUploadFilename();
	}

	public String getTimeAndDate() {
		return new Date().toString();
	}

	@Deprecated
	public String getClientIPAddress() {
		return getSession().ipAddr;
	}

	public String getServletContextName() {
		return Installation.installation().getServletContextName();
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Background test plan running methods
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public Result register(String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params) {
		return TransactionUtil.register(getSession(),username,testInstance,registry,params, new ArrayList<String>());
	}
	public Map<String, String> registerWithLocalizedTrackingInODDS(String username, TestInstance testInstance, SiteSpec registry, SimId odds, Map<String, String> params) {
		try {
			return TransactionUtil.registerWithLocalizedTrackingInODDS(getSession(),username,testInstance,registry,odds, params);
		} catch (Exception ex) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error",ex.toString());
			return errorMap;
		}

	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// ODDE related methods
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	public List<String> getSiteNamesByTranType(String transactionType) throws Exception {
		return siteServiceManager.getSiteNamesByTran(transactionType, session().getId());
	}

	public List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(SimId oddsSimId) {
		return TransactionUtil.getOnDemandDocumentEntryDetails(oddsSimId);
	}

}
