package gov.nist.toolkit.xdstools2.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidSet;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.ExternalCacheManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import gov.nist.toolkit.interactionmapper.InteractionMapper;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.services.client.IdsOrchestrationRequest;
import gov.nist.toolkit.services.client.IgOrchestrationRequest;
import gov.nist.toolkit.services.client.IigOrchestrationRequest;
import gov.nist.toolkit.services.client.RSNAEdgeOrchestrationRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.client.RegOrchestrationRequest;
import gov.nist.toolkit.services.client.RepOrchestrationRequest;
import gov.nist.toolkit.services.client.RgOrchestrationRequest;
import gov.nist.toolkit.services.client.RigOrchestrationRequest;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager;
import gov.nist.toolkit.services.shared.SimulatorServiceManager;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simulators.support.od.TransactionUtil;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.testengine.scripts.BuildCollections;
import gov.nist.toolkit.testengine.scripts.CodesUpdater;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.xml.XmlFormatter;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.valregmsg.validation.factories.CommonMessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.util.ToolkitService;
import gov.nist.toolkit.xdstools2.server.serviceManager.DashboardServiceManager;
import gov.nist.toolkit.xdstools2.server.serviceManager.GazelleServiceManager;
import gov.nist.toolkit.xdstools2.shared.NoServletSessionException;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.shared.command.*;
import gov.nist.toolkit.xdstools2.shared.command.request.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
            MessageValidatorFactoryFactory.messageValidatorFactory2I = new CommonMessageValidatorFactory("a");
        }
    }

    private void installCommandContext(CommandContext commandContext) throws Exception {
        if (commandContext.getEnvironmentName() == null) {
            logger.error(ExceptionUtil.here("session: " + getSessionId() + " installCommandContext: environment name is null"));
            throw new Exception("installCommandContext: environment name is null");
        }
//		if (commandContext.getTestSessionName() == null) {
//			throw new Exception("installCommandContext: test session name is null");
//		}
//        session().setEnvironment(commandContext.getEnvironmentName());
        setEnvironment(commandContext.getEnvironmentName());
        setMesaTestSession(commandContext.getTestSessionName());
    }

    @Override
    public InitializationResponse getInitialization() throws Exception {
        InitializationResponse response = new InitializationResponse();
        response.setDefaultEnvironment(Installation.DEFAULT_ENVIRONMENT_NAME);
        response.setEnvironments(Session.getEnvironmentNames());
        response.setTestSessions(session().xdsTestServiceManager().getMesaTestSessionNames());
        response.setServletContextName(getServletContextName());
        return response;
    }

    @Override
    public String getAssignedSiteForTestSession(String testSession) throws Exception {
        return session().xdsTestServiceManager().getAssignedSiteForTestSession(testSession);
    }

    @Override
    public void setAssignedSiteForTestSession(SetAssignedSiteForTestSessionRequest request) throws Exception {
        installCommandContext(request);
        session().xdsTestServiceManager().setAssignedSiteForTestSession(request.getSelecetedTestSession(), request.getSelectedSite());
    }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Site Services
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
	public List<String> getSiteNames(GetSiteNamesRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.getSiteNames(session().getId(), request.getReload(), request.getSimAlso());
    }
	@Override
    public Collection<Site> getAllSites(CommandContext commandContext) throws Exception {
        installCommandContext(commandContext);
        return siteServiceManager.getAllSites(session().getId());
    }

	@Override
   public List<String> reloadSites(boolean simAlso) throws FactoryConfigurationError, Exception { return siteServiceManager.reloadSites(session().getId(), simAlso); }
	@Override
   public Site getSite(String siteName) throws Exception { return siteServiceManager.getSite(session().getId(), siteName); }
	@Override
   public String saveSite(SaveSiteRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.saveSite(session().getId(), request.getSite());
    }
	@Override
   public String deleteSite(String siteName) throws Exception { return siteServiceManager.deleteSite(session().getId(), siteName); }
	//	public String getHome() throws Exception { return session().getHome(); }
	@Override
   public List<String> getUpdateNames()  throws NoServletSessionException { return siteServiceManager.getUpdateNames(session().getId()); }
	@Override
   public TransactionOfferings getTransactionOfferings(CommandContext commandContext) throws Exception {
		installCommandContext(commandContext);
		return siteServiceManager.getTransactionOfferings(session().getId());
	}
	@Override
   public List<String> reloadExternalSites() throws FactoryConfigurationError, Exception { return siteServiceManager.reloadCommonSites(); }
	@Override
   public List<String> getRegistryNames()  throws NoServletSessionException { return siteServiceManager.getRegistryNames(session().getId()); }
	@Override
   public List<String> getRepositoryNames()  throws NoServletSessionException { return siteServiceManager.getRepositoryNames(session().getId()); }
	@Override
   public List<String> getRGNames()  throws NoServletSessionException { return siteServiceManager.getRGNames(session().getId()); }
	@Override
   public List<String> getIGNames()  throws NoServletSessionException { return siteServiceManager.getIGNames(session().getId()); }
	@Override
   public List<String> getActorTypeNames()  throws NoServletSessionException { return siteServiceManager.getActorTypeNames(session().getId()); }
	@Override
   public List<String> getSiteNamesWithRG() throws Exception { return siteServiceManager.getSiteNamesWithRG(session().getId()); }
   @Override
   public List<String> getSiteNamesWithRIG() throws Exception { return siteServiceManager.getSiteNamesWithRIG(session().getId()); }
   @Override
   public List<String> getSiteNamesWithIDS() throws Exception { return siteServiceManager.getSiteNamesWithIDS(session().getId()); }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Query Services
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public List<Result> registerAndQuery(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().registerAndQuery(site, pid); }
	@Override
   public List<Result> lifecycleValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().lifecycleValidation(site, pid); }
	@Override
   public List<Result> folderValidation(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().folderValidation(site, pid); }
	@Override
   public List<Result> submitRegistryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRegistryTestdata(testSessionName,site, datasetName, pid); }
	@Override
   public List<Result> submitRepositoryTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitRepositoryTestdata(testSessionName,site, datasetName, pid); }
	@Override
   public List<Result> submitXDRTestdata(String testSessionName,SiteSpec site, String datasetName, String pid) throws NoServletSessionException  { return session().queryServiceManager().submitXDRTestdata(testSessionName,site, datasetName, pid); }
	@Override
   public List<Result> provideAndRetrieve(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().provideAndRetrieve(site, pid); }
	@Override
   public List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) throws NoServletSessionException  { return session().queryServiceManager().findDocuments(site, pid, onDemand); }
	@Override
   public List<Result> findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds) throws NoServletSessionException  { return session().queryServiceManager().findDocumentsByRefId(site, pid, refIds); }
	@Override
   public List<Result> getDocuments(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getDocuments(site, aids); }
	@Override
   public List<Result> findFolders(SiteSpec site, String pid) throws NoServletSessionException  { return session().queryServiceManager().findFolders(site, pid); }
	@Override
   public List<Result> getFolders(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolders(site, aids); }
	@Override
   public List<Result> getFoldersForDocument(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFoldersForDocument(site, aids); }
	@Override
   public List<Result> getFolderAndContents(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getFolderAndContents(site, aids); }
	@Override
   public List<Result> getAssociations(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getAssociations(site, ids); }
	@Override
   public List<Result> getObjects(SiteSpec site, ObjectRefs ids) throws NoServletSessionException  { return session().queryServiceManager().getObjects(site, ids); }
	@Override
   public List<Result> getSubmissionSets(SiteSpec site, AnyIds aids) throws NoServletSessionException  { return session().queryServiceManager().getSubmissionSets(site, aids); }
	@Override
   public List<Result> getSSandContents(SiteSpec site, String ssid, Map<String, List<String>> codeSpec) throws NoServletSessionException  { return session().queryServiceManager().getSSandContents(site, ssid, codeSpec); }
	@Override
   public List<Result> srcStoresDocVal(SiteSpec site, String ssid) throws NoServletSessionException  { return session().queryServiceManager().srcStoresDocVal(site, ssid); }
	@Override
   public List<Result> retrieveDocument(SiteSpec site, Uids uids) throws Exception { return session().queryServiceManager().retrieveDocument(site, uids); }
	@Override
   public List<Result> retrieveImagingDocSet(SiteSpec site, Uids uids, String studyRequest, String transferSyntax) throws Exception { return session().queryServiceManager().retrieveImagingDocSet(site, uids, studyRequest, transferSyntax); }

	@Override
   public List<Result> getRelated(SiteSpec site, ObjectRef or,	List<String> assocs) throws NoServletSessionException  { return session().queryServiceManager().getRelated(site, or, assocs); }
	@Override
   public List<Result> getAll(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException  { return session().queryServiceManager().getAll(site, pid, codesSpec); }
	@Override
   public List<Result> findDocuments2(SiteSpec site, String pid, Map<String, List<String>> codesSpec) throws NoServletSessionException  {
		System.out.println("Running findDocuments2 service");
		return session().queryServiceManager().findDocuments2(site, pid, codesSpec); }


	@Override
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
	@Override
   public List<Result> mpqFindDocuments(SiteSpec site, String pid,
										 Map<String, List<String>> codesSpec) throws NoServletSessionException {
		return session().queryServiceManager().mpqFindDocuments(site, pid, codesSpec);
	}

	@Override
   public List<Result> getLastMetadata() { return queryServiceManager.getLastMetadata(); }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Test Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// New - Loads or reloads test data
	@Override
   public List<Test> reloadAllTestResults(String sessionName) throws Exception { return session().xdsTestServiceManager().reloadAllTestResults(sessionName); }
	@Override
   public List<TestInstance> getTestlogListing(String sessionName) throws Exception { return session().xdsTestServiceManager().getTestlogListing(sessionName); }
	@Override
   public Map<String, Result> getTestResults(List<TestInstance> testIds, String testSession)  throws NoServletSessionException { return session().xdsTestServiceManager().getTestResults(testIds, testSession); }
	@Override
   public String setMesaTestSession(String sessionName)  throws NoServletSessionException { session().xdsTestServiceManager().setMesaTestSession(sessionName); return sessionName;}
	@Override
   public List<String> getMesaTestSessionNames(CommandContext request) throws Exception {
		installCommandContext(request);
		return session().xdsTestServiceManager().getMesaTestSessionNames();
	}
	@Override
   public boolean addMesaTestSession(String name) throws Exception { return session().xdsTestServiceManager().addMesaTestSession(name); }
	@Override
   public boolean delMesaTestSession(String name) throws Exception { return session().xdsTestServiceManager().delMesaTestSession(name); }
	@Override
   public String getNewPatientId(String assigningAuthority)  throws NoServletSessionException { return session().xdsTestServiceManager().getNewPatientId(assigningAuthority); }
	public String delTestResults(List<TestInstance> testInstances, String testSession )  throws NoServletSessionException { session().xdsTestServiceManager().delTestResults(testInstances, testSession); return ""; }
	@Override
   public List<Test> deleteAllTestResults(Site site) throws NoServletSessionException { return session().xdsTestServiceManager().deleteAllTestResults(getSession().getMesaSessionName(), site); }
	@Override
   public TestOverviewDTO deleteSingleTestResult(String testSession, TestInstance testInstance) throws Exception {
		testInstance.setUser(testSession);
		return session().xdsTestServiceManager().deleteSingleTestResult(testInstance);
	}
	@Override
   public List<Test> runAllTests(Site site) throws NoServletSessionException { return session().xdsTestServiceManager().runAllTests(getSession().getMesaSessionName(), site); }
	@Override
   public Test runSingleTest(Site site, int testId) throws NoServletSessionException { return session().xdsTestServiceManager().runSingleTest(getSession().getMesaSessionName(), site, testId); }

    public String getTestReadme(String testSession,String test) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestReadme(test);
    }
    @Override
    public RawResponse buildRepTestOrchestration(RepOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRepTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildRegTestOrchestration(RegOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRegTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildIgTestOrchestration(IgOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIgTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildIigTestOrchestration(IigOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIigTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildRgTestOrchestration(RgOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRgTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildRigTestOrchestration(RigOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRigTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildIdsTestOrchestration(IdsOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIdsTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildRSNAEdgeTestOrchestration(RSNAEdgeOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRSNAEdgeTestEnvironment(s, request);
    }
    /*
	@Override
   public RawResponse buildRepTestOrchestration(RepOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildRepTestEnvironment(s, request);
	}
	@Override
   public RawResponse buildRegTestOrchestration(RegOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildRegTestEnvironment(s, request);
	}
	@Override
   public RawResponse buildIgTestOrchestration(IgOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildIgTestEnvironment(s, request);
	}
   @Override
   public RawResponse buildIigTestOrchestration(IigOrchestrationRequest request) {
      Session s = getSession();
      if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
      return new OrchestrationManager().buildIigTestEnvironment(s, request);
   }
	@Override
   public RawResponse buildRgTestOrchestration(RgOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildRgTestEnvironment(s, request);
	}
   @Override
   public RawResponse buildRigTestOrchestration(RigOrchestrationRequest request) {
      Session s = getSession();
      if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
      return new OrchestrationManager().buildRigTestEnvironment(s, request);
   }
	@Override
   public RawResponse buildIdsTestOrchestration(IdsOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildIdsTestEnvironment(s, request);
	}
	@Override
   public RawResponse buildRSNAEdgeTestOrchestration(RSNAEdgeOrchestrationRequest request) {
		Session s = getSession();
		if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
		return new OrchestrationManager().buildRSNAEdgeTestEnvironment(s, request);
	}
*/
	/**
	 * Get list of section names defined for the test in the order they should be executed
     * @param testSession test session name (mesa session name)
     * @param test test name
     * @return list of sections
     * @throws Exception if something goes wrong
     */
    public List<String> getTestIndex(String testSession,String test) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestSections(test);
    }

    //	public List<Result> getLogContent(String sessionName, TestInstance testInstance) throws Exception { return session().xdsTestServiceManager().getLogContent(sessionName, testInstance); }
    public List<Result> runMesaTest(String environmentName,String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure)  throws Exception {
        return session().xdsTestServiceManager().runMesaTest(environmentName, mesaTestSession, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
    }
    /**
     * Get list of section names defined for the test in the order they should be executed
     * @param test test name
     * @return list of sections
     * @throws Exception if something goes wrong
     */
	@Override
   public List<String> getTestIndex(String test) throws Exception { return session().xdsTestServiceManager().getTestSections(test); }
    /**
     * Get map of (collection name, collection description) pairs contained in testkit
     * @param collectionSetName the collection name
     * @return the map
     * @throws Exception is something goes wrong
     */
    @Override
    public Map<String, String> getCollectionNames(String collectionSetName) throws Exception { return session().xdsTestServiceManager().getCollectionNames(collectionSetName); }
    @Override
    public List<TestInstance> getCollectionMembers(String collectionSetName, String collectionName) throws Exception { return session().xdsTestServiceManager().getCollectionMembers(collectionSetName, collectionName); }
    @Override
    public List<TestOverviewDTO> getTestsOverview(GetTestsOverviewRequest request) throws Exception {
        installCommandContext(request);
        List<TestOverviewDTO> o = session().xdsTestServiceManager().getTestsOverview(request.getTestSessionName(), request.getTestInstances());
        return o;
    }
    public List<SectionDefinitionDAO> getTestSectionsDAOs(String mesaTestSession, TestInstance testInstance) throws Exception {
        Session session = session().xdsTestServiceManager().session;
        session.setMesaSessionName(mesaTestSession);
        return session().xdsTestServiceManager().getTestSectionsDAOs(testInstance);
    }
    @Override
    public LogFileContentDTO getTestLogDetails(String sessionName, TestInstance testInstance) throws Exception {
        LogFileContentDTO o = session().xdsTestServiceManager().getTestLogDetails(sessionName, testInstance);
        return o;
    }
    @Override
    public List<TestCollectionDefinitionDAO> getTestCollections(String collectionSetName) throws Exception { return session().xdsTestServiceManager().getTestCollections(collectionSetName); }

    @Override
    public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception {
        return session().xdsTestServiceManager().getCollection(collectionSetName, collectionName);
    }

    @Override
    public String getTestReadme(String test) throws Exception {
        return session().xdsTestServiceManager().getTestReadme(test);
    }

	@Override
   public List<Result> runMesaTest(String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure)  throws Exception {
		return session().xdsTestServiceManager().runMesaTest(getCurrentEnvironment(), mesaTestSession, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
	}
	@Override
   public TestOverviewDTO runTest(String environmentName, String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, Map<String, String> params, boolean stopOnFirstFailure) throws Exception {
		List<String> sections = new ArrayList<>();
		if (testInstance.getSection() != null) sections.add(testInstance.getSection());
		setEnvironment(environmentName);
		Session session = session().xdsTestServiceManager().session;
		session.setCurrentEnvName(environmentName);
		session.setMesaSessionName(mesaTestSession);
		if (siteSpec == null)
			throw new Exception("No site selected");
		if (!new SimManager(mesaTestSession).exists(siteSpec.name))
			throw new Exception("Site " + siteSpec.name + " does not exist");
		TestOverviewDTO testOverviewDTO = session().xdsTestServiceManager().runTest(environmentName, mesaTestSession, siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
		return testOverviewDTO;
	}
	// TODO remove this once command pattern is implemented for every single call
    private void setEnvironment(String environmentName) throws NoServletSessionException {
        session().setEnvironment(environmentName);
    }
	@Override
   public TestLogs getRawLogs(TestInstance logId)  throws NoServletSessionException { return session().xdsTestServiceManager().getRawLogs(logId); }
	@Override
   public List<String> getTestdataSetListing(String environmentName, String testSessionName, String testdataSetName)  throws NoServletSessionException {
        return session().xdsTestServiceManager().getTestdataSetListing(environmentName,testSessionName,testdataSetName);
    }
	@Override
   public String getTestplanAsText(String testSession,TestInstance testInstance, String section) throws Exception {
        session().setMesaSessionName(testSession);
        return session().xdsTestServiceManager().getTestplanAsText(testInstance, section);
    }
	@Override
   public TestPartFileDTO getSectionTestPartFile(String testSession, TestInstance testInstance, String section) throws Exception {
		session().setMesaSessionName(testSession);
		return session().xdsTestServiceManager().getSectionTestPartFile(testInstance, section);
	}
	@Override
   public TestPartFileDTO loadTestPartContent(TestPartFileDTO testPartFileDTO) throws Exception {
		return XdsTestServiceManager.loadTestPartContent(testPartFileDTO);
	}
	@Override
   public String getHtmlizedString(String xml) { // This is different than the Htmlize class in the client code works (see its isHtml method)
		return XmlFormatter.htmlize(xml)
				.replace("<br/>", "\r\n");
	}
	@Override
   public CodesResult getCodesConfiguration(String environmentName)  throws NoServletSessionException {
		setEnvironment(environmentName);
		return session().xdsTestServiceManager().getCodesConfiguration();
	}

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
	@Override
   public boolean isPrivateMesaTesting()  throws NoServletSessionException { return session().xdsTestServiceManager().isPrivateMesaTesting(); }
	@Override
   public List<Result> sendPidToRegistry(SendPidToRegistryRequest request) throws Exception {
		installCommandContext(request);
		return session().xdsTestServiceManager().sendPidToRegistry(request.getSiteSpec(), request.getPid());
	}

    @Override
    public List<Pid> retrieveConfiguredFavoritesPid(CommandContext commandContext) throws Exception {
        installCommandContext(commandContext);
        List<Pid> pids = new ArrayList<Pid>();
        String environmentName=commandContext.getEnvironmentName();
        File environmentFile;
        if (environmentName!=null) {
            environmentFile=Installation.instance().environmentFile(environmentName);
        }else{
            environmentFile=Installation.instance().environmentFile(Installation.DEFAULT_ENVIRONMENT_NAME);
        }
        File favPidsFile = new File(environmentFile,"pids.txt");
        if (favPidsFile.exists()) {
            byte[] pidBytes = Files.readAllBytes(favPidsFile.toPath());
            String str = new String(pidBytes, Charset.defaultCharset());
            PidSet pidSet = new PidSet(str.replace("\\n",""));
            pids.addAll(pidSet.get());
        }
        return pids;
    }

    /**
     * This method copies the default testkit to a selected environment and triggers a code update based on
     * the affinity domain configuration file (codes.xml) located in the selected environment.
     * @param context
     * @return update output as a String
     */
    @Override
    public String configureTestkit(CommandContext context) throws Exception {
        installCommandContext(context);
        File environmentFile = Installation.instance().environmentFile(context.getEnvironmentName());
        File defaultTestkit = Installation.instance().internalTestkitFile();
        CodesUpdater updater = new CodesUpdater();
        updater.run(environmentFile.getAbsolutePath(),defaultTestkit.getAbsolutePath());
        return updater.getOutput();
    }

    /**
     * This method tests if there already is a testkit configured in a selected environment.
     * @param context
     * @return boolean
     */
    @Override
    public boolean doesTestkitExist(CommandContext context) throws Exception {
        installCommandContext(context);
        File environmentFile = Installation.instance().environmentFile(context.getEnvironmentName());
        File testkit=new File(environmentFile,"testkits");
        return testkit.exists();
    }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Gazelle Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public String reloadSystemFromGazelle(ReloadSystemFromGazelleRequest request) throws Exception {
        installCommandContext(request);
        return new GazelleServiceManager(session()).reloadSystemFromGazelle(request.getSystem());
    }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Environment management
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public List<String> getEnvironmentNames(CommandContext context) throws Exception {
//		installCommandContext(context);  // not needed - may not be initialized
		return session().getEnvironmentNames();
	}
	@Override
   public String setEnvironment(CommandContext context) throws Exception {
        logger.info("set environment - " + context.getEnvironmentName());
        installCommandContext(context);
        return context.getEnvironmentName();
    }
	@Override
   public String getCurrentEnvironment() throws NoServletSessionException { return session().getCurrentEnvironment(); }
	@Override
   public String getDefaultEnvironment(CommandContext context) throws Exception {
        installCommandContext(context);
        String defaultEnvironment = Installation.instance().propertyServiceManager().getDefaultEnvironment();
        String name;
        if (Session.environmentExists(defaultEnvironment))
            name = defaultEnvironment;
        name = Installation.DEFAULT_ENVIRONMENT_NAME;
        logger.info("getDefaultEnvironment - " + name);
        return name;
    }

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Session
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public Map<String, String> getSessionProperties() throws NoServletSessionException { return session().getSessionPropertiesAsMap(); }
	@Override
   public void setSessionProperties(Map<String, String> props) throws NoServletSessionException { session().setSessionProperties(props); }
	@Override
   public Pid createPid(GeneratePidRequest generatePidRequest) throws Exception {
		installCommandContext(generatePidRequest);
		return session().allocateNewPid(generatePidRequest.getAssigningAuthority());
	}
	@Override
   public String getAssigningAuthority(CommandContext commandContext) throws Exception {
		installCommandContext(commandContext);
		return session().getAssigningAuthority();
	}
	@Override
   public List<String> getAssigningAuthorities(CommandContext commandContext) throws Exception {
		installCommandContext(commandContext);
		return session().getAssigningAuthorities();
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Property Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public TkProps getTkProps() throws NoServletSessionException { return session().tkProps(); }

    @Override
    public ConformanceSessionValidationStatus validateConformanceSession(String testSession, String siteName) throws Exception {
        return session().xdsTestServiceManager().validateConformanceSession(testSession, siteName);
    }

	@Override
	public Collection<String> getSitesForTestSession(CommandContext context) throws Exception {
        installCommandContext(context);
		if (context.getTestSessionName()== null)
			return new ArrayList<>();
		return session().xdsTestServiceManager().getSitesForTestSession(context.getTestSessionName());
	}

	@Override
   public String getDefaultAssigningAuthority(CommandContext context) throws Exception {
        installCommandContext(context);
        return Installation.instance().propertyServiceManager().getDefaultAssigningAuthority();
    }
	@Override
   public String getImplementationVersion() throws NoServletSessionException  { return Installation.instance().propertyServiceManager().getImplementationVersion(); }
	@Override
   public Map<String, String> getToolkitProperties()  throws NoServletSessionException { return Installation.instance().propertyServiceManager().getToolkitProperties(); }
	@Override
   public boolean isGazelleConfigFeedEnabled(CommandContext context) throws Exception {
        installCommandContext(context);
        return SiteServiceManager.getSiteServiceManager().useGazelleConfigFeed();
    }
	//	public String getToolkitEnableNwHIN() { return propertyServiceManager.getToolkitEnableNwHIN(); }
	@Override
   public String setToolkitProperties(Map<String, String> props) throws Exception { return setToolkitPropertiesImpl(props); }
	@Override
   public String getAdminPassword() throws NoServletSessionException  { return Installation.instance().propertyServiceManager().getAdminPassword(); }
	@Override
   public boolean reloadPropertyFile() throws NoServletSessionException  { return Installation.instance().propertyServiceManager().reloadPropertyFile(); }
	@Override
   public String getAttributeValue(String username, String attName) throws Exception { return Installation.instance().propertyServiceManager().getAttributeValue(username, attName); }
	@Override
   public void setAttributeValue(String username, String attName, String attValue) throws Exception { Installation.instance().propertyServiceManager().setAttributeValue(username, attName, attValue); }


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Simulator Service
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public String putSimConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).saveSimConfig(config); }
	// this deletes a simulator
	@Override
   public String deleteConfig(SimulatorConfig config) throws Exception { return new SimulatorServiceManager(session()).deleteConfig(config); }
	@Override
   public void renameSimFile(String simFileSpec, String newSimFileSpec) throws Exception { new SimulatorServiceManager(session()).renameSimFile(simFileSpec, newSimFileSpec); }
	@Override
   public String getSimulatorEndpoint() throws NoServletSessionException { return new SimulatorServiceManager(session()).getSimulatorEndpoint(); }
	@Override
   public MessageValidationResults executeSimMessage(ExecuteSimMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).executeSimMessage(request.getFileName());
    }
	@Override
   public List<TransactionInstance> getTransInstances(SimId simid, String xactor, String trans) throws Exception { return new SimulatorServiceManager(session()).getTransInstances(simid, xactor, trans); }
	@Override
   public String getTransactionRequest(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionRequest(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }
	@Override
   public String getTransactionResponse(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionResponse(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }
	@Override
   public int removeOldSimulators() throws NoServletSessionException { return new SimulatorServiceManager(session()).removeOldSimulators(); }
	@Override
   public List<Result> getSelectedMessage(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessage(simFileSpec); }
	@Override
   public List<Result> getSelectedMessageResponse(String simFileSpec) throws NoServletSessionException { return new SimulatorServiceManager(session()).getSelectedMessageResponse(simFileSpec); }
	@Override
   public Map<String, SimId> getActorSimulatorNameMap(CommandContext context) throws Exception {
        installCommandContext(context);
        return new SimulatorServiceManager(session()).getSimulatorNameMap();
    }
	@Override
   public MessageValidationResults validateMessage(ValidateMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).validateMessage(request.getValidationContext());
    }
	@Override
   public List<SimulatorConfig> getSimConfigs(List<SimId> ids) throws Exception { return new SimulatorServiceManager(session()).getSimConfigs(ids); }
	@Override
   public List<SimulatorConfig> getAllSimConfigs(GetAllSimConfigsRequest request) throws Exception {
		installCommandContext(request);
		return new SimulatorServiceManager(session()).getAllSimConfigs(request.getUser());
	}
	@Override
   public Simulator getNewSimulator(String actorTypeName, SimId simId) throws Exception { return new SimulatorServiceManager(session()).getNewSimulator(actorTypeName, simId); }
	@Override
   public void deleteSimFile(String simFileSpec) throws Exception { new SimulatorServiceManager(session()).deleteSimFile(simFileSpec); }
	@Override
   public List<String> getTransactionsForSimulator(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionsForSimulator(request.getSimid());
    }
	@Override
   public List<SimulatorStats> getSimulatorStats(List<SimId> simids) throws Exception { return new SimulatorServiceManager(session()).getSimulatorStats(simids); }
	@Override
   public String getTransactionLog(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionLog(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }

	@Override
   public List<Pid> getPatientIds(SimId simId) throws Exception { return new SimulatorServiceManager(session()).getPatientIds(simId); }
	@Override
   public String addPatientIds(SimId simId, List<Pid> pids) throws Exception { return new SimulatorServiceManager(session()).addPatientIds(simId, pids); }
	@Override
   public boolean deletePatientIds(SimId simId, List<Pid> pids) throws Exception { return new SimulatorServiceManager(session()).deletePatientIds(simId, pids); }

	@Override
   public Result getSimulatorEventRequest(TransactionInstance ti) throws Exception {
		return new SimulatorServiceManager(session()).getSimulatorEventRequestAsResult(ti);
	}
	@Override
   public Result getSimulatorEventResponse(TransactionInstance ti) throws Exception {
		return new SimulatorServiceManager(session()).getSimulatorEventResponseAsResult(ti);
	}
	@Override
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
	@Override
   public List<RegistryStatus> getDashboardRegistryData(CommandContext context) throws Exception {
        installCommandContext(context);
        return new DashboardServiceManager(session()).getDashboardRegistryData();
    }
	@Override
   public List<RepositoryStatus> getDashboardRepositoryData(CommandContext context) throws Exception {
        installCommandContext(context);
        return new DashboardServiceManager(session()).getDashboardRepositoryData();
    }


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

//            File warhome = Installation.instance().warHome();
            new PropertyServiceManager().getPropertyManager().update(props);
            reloadPropertyFile();
//		Installation.instance().externalCache(eCacheFile);
            ExternalCacheManager.reinitialize(eCacheFile);
            try {
                TkLoader.tkProps(Installation.instance().getTkPropsFile());
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
        Installation.instance().setServletContextName(context.getContextPath());
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
        if (context != null && Installation.instance().warHome() == null) {

            File warHome = new File(context.getRealPath("/"));
            System.setProperty("warHome", warHome.toString());
            logger.info("warHome [ToolkitServiceImpl]: " + warHome);
            Installation.instance().warHome(warHome);
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
        logHere("On Session " + s.getId());
//		String msg = ExceptionUtil.here("On Session " + s.getId());
//		Scanner scanner = new Scanner(msg);
//		while(scanner.hasNextLine()) {
//			String line = scanner.nextLine();
//			logger.info(line);
//		}
//		logger.info(msg);
        return s;
    }

    private void logHere(String themsg) {
        String msg = ExceptionUtil.here(themsg);
        Scanner scanner = new Scanner(msg);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            logger.info(line);
        }
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
            warHome = Installation.instance().warHome();
            if (sc != null && warHome == null) {
                warHome = new File(sc.getRealPath("/"));
                Installation.instance().warHome(warHome);
                System.setProperty("warHome", warHome.toString());
                System.out.print("warHome [ToolkitServiceImp]: " + warHome);
                Installation.instance().warHome(warHome);
            }
            if (warHome != null)
                System.setProperty("warHome", warHome.toString());

            if (warHome != null) {
                s = new Session(warHome, getSessionId());
                if (hsession != null) {
                    s.setSessionId(hsession.getId());
//					logger.info("New Session ID " + hsession.getId());
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

	@Override
   public String getLastFilename() {
		return getSession().getlastUploadFilename();
	}

	@Override
   public String getTimeAndDate() {
		return new Date().toString();
	}

	@Override
   @Deprecated
	public String getClientIPAddress() {
		return getSession().ipAddr;
	}

	@Override
   public String getServletContextName() {
		return Installation.instance().getServletContextName();
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Background test plan running methods
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public Result register(String username, TestInstance testInstance, SiteSpec registry, Map<String, String> params) {
		return TransactionUtil.register(getSession(),username,testInstance,registry,params, new ArrayList<String>());
	}
	@Override
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
	@Override
   public List<String> getSiteNamesByTranType(GetSiteNamesByTranTypeRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.getSiteNamesByTran(request.getTransactionTypeName(), session().getId());
    }

	@Override
   public List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(SimId oddsSimId) {
		return TransactionUtil.getOnDemandDocumentEntryDetails(oddsSimId);
	}

	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Interaction methods
	// TODO: this mapping method is to be replaced by the test log map method.
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	@Override
   public InteractingEntity getInteractionFromModel(InteractingEntity model) throws Exception {
		return new InteractionMapper().map(model);
	}


    public String getStsSamlAssertion(String username, TestInstance testInstance, SiteSpec stsSite, Map<String, String> params) throws Exception {

        XdsTestServiceManager xtsm = session().getXdsTestServiceManager();
        String sessionName = session().getMesaSessionName();
        String step = "issue";
        String query = testInstance.getSection();
		List<Result> results = xtsm.querySts("GazelleSts",sessionName,query,params, false);

        if (results!=null) {
            if (results.size() == 1) {
                if (!results.get(0).passed()) {
                    List<String> soapFaults = results.get(0).getStepResults().get(0).getSoapFaults();
                    if (soapFaults != null && soapFaults.size() > 0) {
                        throw new ToolkitRuntimeException("getStsSamlAssertion SOAP Fault: " + soapFaults.toString());
                    }
                } else {
                    LogFileContentDTO logFileContentDTO = xtsm.getTestLogDetails(sessionName,testInstance);
                    TestStepLogContentDTO testStepLogContentDTO = logFileContentDTO.getStep(step);
                    List<ReportDTO> reportDTOs = testStepLogContentDTO.getReportDTOs();
                    String assertionResultId = "saml-assertion";
                    for (ReportDTO report : reportDTOs)  {
                        if (assertionResultId.equals(report.getName())) {
                           return report.getValue();
                        }
                    }
                    throw new ToolkitRuntimeException(assertionResultId + " result key not found.");

                }
            }
            throw new ToolkitRuntimeException("Result size: " + results.size());
        } else {
            throw new ToolkitRuntimeException("No result.");
        }
    }

    @Override
    public String clearTestSession(CommandContext context) throws Exception {
        installCommandContext(context);
        return session().xdsTestServiceManager().clearTestSession(context.getTestSessionName());
    }

    @Override
    public boolean getAutoInitConformanceTesting() {
        return Installation.instance().propertyServiceManager().getAutoInitializeConformanceTool();
    }

    @Override
    public boolean indexTestKits() {
        new BuildCollections().run();
        // FIXME why does this have to return true? should we change for a void method?
        return true;
    }

}
