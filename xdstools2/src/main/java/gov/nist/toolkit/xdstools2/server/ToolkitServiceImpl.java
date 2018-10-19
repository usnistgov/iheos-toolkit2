package gov.nist.toolkit.xdstools2.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.wustl.mir.erl.ihe.xdsi.util.PrsSimLogs;
import gov.nist.toolkit.MessageValidatorFactory2.MessageValidatorFactoryFactory;
import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader;
import gov.nist.toolkit.actortransaction.shared.ActorOption;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.shared.IheItiProfile;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidSet;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.datasets.server.DatasetFactory;
import gov.nist.toolkit.datasets.shared.DatasetModel;
import gov.nist.toolkit.fhir.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.fhir.simulators.support.StoredDocument;
import gov.nist.toolkit.fhir.simulators.support.od.TransactionUtil;
import gov.nist.toolkit.installation.server.ExternalCacheManager;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.PropertyServiceManager;
import gov.nist.toolkit.installation.shared.TestCollectionCode;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.interactionmapper.InteractionMapper;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest;
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse;
import gov.nist.toolkit.services.client.IdcOrchestrationRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.server.RawResponseBuilder;
import gov.nist.toolkit.services.server.SimulatorServiceManager;
import gov.nist.toolkit.services.server.orchestration.OrchestrationManager;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;
import gov.nist.toolkit.session.client.TestSessionStats;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestPartFileDTO;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.FhirServiceManager;
import gov.nist.toolkit.session.server.serviceManager.QueryServiceManager;
import gov.nist.toolkit.session.server.serviceManager.TestSessionServiceManager;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.session.server.testlog.QuickScanLog;
import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.simcommon.server.SiteServiceManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.testengine.engine.RegistryUtility;
import gov.nist.toolkit.testengine.scripts.BuildCollections;
import gov.nist.toolkit.testengine.scripts.CodesUpdater;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.xml.XmlFormatter;
import gov.nist.toolkit.valregmsg.message.SchemaValidation;
import gov.nist.toolkit.valregmsg.validation.factories.CommonMessageValidatorFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.GazelleXuaUsername;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.ActorOptionConfig;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.UserTestCollection;
import gov.nist.toolkit.xdstools2.client.util.ToolkitService;
import gov.nist.toolkit.xdstools2.server.serviceManager.DashboardServiceManager;
import gov.nist.toolkit.xdstools2.server.serviceManager.GazelleServiceManager;
import gov.nist.toolkit.xdstools2.shared.NoServletSessionException;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.InitializationResponse;
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
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("serial")
public class ToolkitServiceImpl extends RemoteServiceServlet implements
        ToolkitService {
    static String schematronHome = null;
    ServletContext context = null;

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
            return;
//            logger.error(ExceptionUtil.here("session: " + getSessionId() + " installCommandContext: environment name is null"));
//            throw new Exception("installCommandContext: environment name is null");
        }
        setEnvironment(commandContext.getEnvironmentName());
        if (session().queryServiceManager() != null)
            session().queryServiceManager().setTestSession(commandContext.getTestSession());

//        if (Installation.instance().propertyServiceManager().isSingleUserMode()
//                && "default".equalsIgnoreCase(commandContext.getTestSessionName())) {
            setTestSession(commandContext.getTestSessionName());
//        }
    }

    @Override
    public InitializationResponse getInitialization(CommandContext context) throws Exception {
        if (Installation.instance().externalCache() == null) {
            throw new Exception("External Cache does not exist at " + Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache());
        }
        if (!Installation.instance().externalCache().exists()) {
            throw new Exception("Configured External Cache location " + Installation.instance().externalCache() + " does not exist");
        }
        installCommandContext(context);
        InitializationResponse response = new InitializationResponse();
        String defaultEnv = Installation.instance().defaultEnvironmentName();
        if (defaultEnv == null || defaultEnv.equals(""))
                defaultEnv = Installation.DEFAULT_ENVIRONMENT_NAME;
        if (Installation.instance().propertyServiceManager().isCasMode()) {
              defaultEnv = Installation.instance().propertyServiceManager().getDefaultEnvironment();
              if (defaultEnv==null || "".equals(defaultEnv)) {
                  throw new Exception("In CAS mode, a null or empty default environment value from toolkit.properties is not valid.");
              }
        }
        response.setDefaultEnvironment(defaultEnv);
        response.setEnvironments(Session.getEnvironmentNames());
        response.setTestSessions(TestSessionServiceManager.INSTANCE.getNames());
        response.setServletContextName(getServletContextName());
        PropertyServiceManager props = Installation.instance().propertyServiceManager();
        String contextName = Installation.instance().getServletContextName();
        logger.info("contextName is " + contextName);
        if (contextName == null)
            throw new Exception("Servlet Context Name is null");
//        response.setToolkitBaseUrl("http://" + props.getToolkitHost()
//                + ":" + props.getToolkitPort()  + servletContext().getServletContextName() +"Xdstools2.html");
//        response.setToolkitBaseUrl("http://" + props.getToolkitHost()
//                + ":" + props.getToolkitPort()  + contextName +"/Xdstools2.html");
        response.setToolkitBaseUrl(Installation.instance().getToolkitBaseUrl());
        logger.info("Toolkit Base URL is " + response.getToolkitBaseUrl());
        response.setWikiBaseUrl(Installation.instance().wikiBaseAddress());
        response.setTkPropMap(props.getToolkitProperties());
        return response;
    }

    @Override
    public String getAssignedSiteForTestSession(CommandContext context) throws Exception {
        installCommandContext(context);
        return session().xdsTestServiceManager().getAssignedSiteForTestSession(context.getTestSession());
    }

    @Override
    public void setAssignedSiteForTestSession(SetAssignedSiteForTestSessionRequest request) throws Exception {
        installCommandContext(request);
        session().xdsTestServiceManager().setAssignedSiteForTestSession(new TestSession(request.getSelecetedTestSession()), request.getSelectedSite());
    }


    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Site Services
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    @Override
    public List<String> getSiteNames(GetSiteNamesRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.getSiteNames(session().getId(), request.getReload(), request.getSimAlso(), request.getTestSession(), request.isQualified());
    }
    @Override
    public Collection<Site> getAllSites(CommandContext commandContext) throws Exception {
        installCommandContext(commandContext);
        return siteServiceManager.getAllSites(session().getId(), commandContext.getTestSession());
    }

    @Override
    public List<String> reloadSites(boolean simAlso) throws FactoryConfigurationError, Exception {
        return siteServiceManager.reloadSites(session().getId(), simAlso, session().getTestSession());
    }

    @Override
    public Site getSite(GetSiteRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.getSite(session().getId(), request.getSiteName(), request.getTestSession());
    }
    @Override
    public String saveSite(SaveSiteRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.saveSite(session().getId(), request.getSite(), request.getTestSession());
    }
    @Override
    public String deleteSite(DeleteSiteRequest request) throws Exception {
        installCommandContext(request);
        return siteServiceManager.deleteSite(session().getId(), request.getSiteName(), request.getTestSession());
    }
    //	public String getHome() throws Exception { return session().getHome(); }
    @Override
    public List<String> getUpdateNames()  throws NoServletSessionException { return siteServiceManager.getUpdateNames(session().getId(), session().getTestSession()); }
    @Override
    public TransactionOfferings getTransactionOfferings(CommandContext commandContext) throws Exception {
        installCommandContext(commandContext);
        TransactionOfferings to = null;
        try {
            to = siteServiceManager.getTransactionOfferings(session().getId(), commandContext.getTestSession());
        } catch (Throwable e) {
            // this can happen because UI initializes faster than AbstractActorFactory on startup
            // only ever noticed when launching from IntelliJ.  In production tomcat launch happens
            // way before browser launch
            return new TransactionOfferings();
        }
        return to;
    }
    @Override
    public List<String> reloadExternalSites(CommandContext context) throws FactoryConfigurationError, Exception {
        installCommandContext(context);
        return siteServiceManager.reloadCommonSites(context.getTestSession());
    }
    @Override
    public List<String> getRegistryNames()  throws NoServletSessionException { return siteServiceManager.getRegistryNames(session().getId(), session().getTestSession()); }
    @Override
    public List<String> getRepositoryNames()  throws NoServletSessionException, Exception { return siteServiceManager.getRepositoryNames(session().getId(), session().getTestSession()); }
    @Override
    public List<String> getRGNames()  throws NoServletSessionException { return siteServiceManager.getRGNames(session().getId(), session().getTestSession()); }
    @Override
    public List<String> getIGNames()  throws NoServletSessionException { return siteServiceManager.getIGNames(session().getId(), session().getTestSession()); }
    @Override
    public List<String> getActorTypeNames(CommandContext context)  throws Exception {
        installCommandContext(context);
        return siteServiceManager.getActorTypeNames(session().getId());
    }
    @Override
    public List<String> getSiteNamesWithRG(CommandContext context) throws Exception {
        installCommandContext(context);
        return siteServiceManager.getSiteNamesWithRG(session().getId(), context.getTestSession());
    }
    @Override
    public List<String> getSiteNamesWithRepository(CommandContext context) throws Exception {
        installCommandContext(context);
        return siteServiceManager.getSiteNamesWithRepository(session().getId(), context.getTestSession());
    }
    @Override
    public List<String> getSiteNamesWithRIG(CommandContext context) throws Exception {
        installCommandContext(context);
        return siteServiceManager.getSiteNamesWithRIG(session().getId(), context.getTestSession());
    }
    @Override
    public List<String> getSiteNamesWithIDS(CommandContext context) throws Exception {
        installCommandContext(context);
        return siteServiceManager.getSiteNamesWithIDS(session().getId(), context.getTestSession());
    }

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Query Services
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    @Override
    public List<Result> registerAndQuery(RegisterAndQueryRequest request) throws Exception  {
        installCommandContext(request);
        QueryServiceManager mgr = session().queryServiceManager();
        List<Result> results = new ArrayList<>();
        session().isTls = request.getSite().isTls;

        for (Submission submission : request.getSubmissions()) {
            List<Result> myResults = mgr.registerAndQuery(submission.getTestInstance(), request.getSite(), submission.getPid());
            results.addAll(myResults);
        }

        return results;
    }
    @Override
    public List<Result> lifecycleValidation(LifecycleValidationRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().lifecycleValidation(request.getSite(), request.getPid());
    }
    @Override
    public List<Result> folderValidation(FoldersRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().folderValidation(request.getSite(), request.getPid());
    }
    @Override
    public List<Result> submitRegistryTestdata(SubmitTestdataRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().submitRegistryTestdata(request.getSite(), request.getDataSetName(), request.getPid());
    }
    @Override
    public List<Result> submitRepositoryTestdata(SubmitTestdataRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().submitRepositoryTestdata(request.getSite(), request.getDataSetName(), request.getPid());
    }
    @Override
    public List<Result> submitXDRTestdata(SubmitTestdataRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().submitXDRTestdata(request.getSite(),request.getDataSetName(), request.getPid());
    }
    @Override
    public List<Result> provideAndRetrieve(ProvideAndRetrieveRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().provideAndRetrieve(request.getSite(), request.getPid());
    }
    @Override
    public List<Result> findDocuments(FindDocumentsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSiteSpec().isTls;
        return session().queryServiceManager().findDocuments(request.getSiteSpec(), request.getPid(), request.isOnDemand());
    }
    @Override
    public List<Result> findDocumentsByRefId(FindDocumentsRequest request) throws Exception  {
        session().isTls = request.getSiteSpec().isTls;
        return session().queryServiceManager().findDocumentsByRefId(request.getSiteSpec(), request.getPid(), request.getRefIds());
    }
    @Override
    public List<Result> getDocuments(GetDocumentsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getDocuments(request.getSite(), request.getIds());
    }
    @Override
    public List<Result> findFolders(FoldersRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().findFolders(request.getSite(), request.getPid()); }
    @Override
    public List<Result> getFolders(GetFoldersRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getFolders(request.getSite(), request.getAnyIds());
    }
    @Override
    public List<Result> getFoldersForDocument(GetFoldersRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getFoldersForDocument(request.getSite(), request.getAnyIds());
    }
    @Override
    public List<Result> getFolderAndContents(GetFoldersRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getFolderAndContents(request.getSite(), request.getAnyIds()); }
    @Override
    public List<Result> getAssociations(GetAssociationsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getAssociations(request.getSite(), request.getIds());
    }
    @Override
    public List<Result> getObjects(GetObjectsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getObjects(request.getSite(), request.getIds());
    }
    @Override
    public List<Result> getSubmissionSets(GetSubmissionSetsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getSubmissionSets(request.getSite(), request.getIds());
    }
    @Override
    public List<Result> getSSandContents(GetSubmissionSetAndContentsRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSiteSpec().isTls;
        return session().queryServiceManager().getSSandContents(request.getSiteSpec(), request.getSsid(), request.getCodeSpec());
    }
    @Override
    public List<Result> srcStoresDocVal(GetSrcStoresDocValRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSiteSpec().isTls;
        return session().queryServiceManager().srcStoresDocVal(request.getSiteSpec(), request.getSsid());
    }
    @Override
    public List<Result> retrieveDocument(RetrieveDocumentRequest request) throws Exception {
        try {
            installCommandContext(request);
            session().isTls = request.getSite().isTls;
            String uid = request.getUids().uids.get(0).repositoryUniqueId;
            if (uid==null)  // For XDS tools, the repository UID is set in the RetrieveDocument#run method's setSiteSpec call.
                return session().queryServiceManager().retrieveDocument(request.getSite(), request.getUids());
            if (uid.startsWith("http")) {
                    // fhir read
                return session().fhirServiceManager().read(request.getSite(), uid);
            } else if (uid.startsWith("[http")) {
                uid = uid.substring(1, uid.length()-1);
                return session().fhirServiceManager().read(request.getSite(), uid);
            } else {
                return session().queryServiceManager().retrieveDocument(request.getSite(), request.getUids());
            }
        } catch (Exception e) {
            throw new Exception("retrieveDocument failed: " + e.toString());
        }
        //return null;
    }
    @Override
    public List<Result> retrieveImagingDocSet(RetrieveImagingDocSetRequest request) throws Exception {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().retrieveImagingDocSet(request.getSite(), request.getUids(), request.getStudyRequest(), request.getTransferSyntax());
    }

    @Override
    public List<Result> getRelated(GetRelatedRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getRelated(request.getSite(),request.getObjectRef(), request.getAssocs());
    }
    @Override
    public List<Result> getAll(GetAllRequest request) throws Exception  {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().getAll(request.getSite(), request.getPid(), request.getCodesSpec());
    }
    @Override
    public List<Result> findDocuments2(FindDocuments2Request request) throws Exception  {
        installCommandContext(request);
        System.out.println("Running findDocuments2 service");
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().findDocuments2(request.getSite(), request.getPid(), request.getCodesSpec());
    }


    public List<Result> mpqFindDocuments(SiteSpec site, String pid,
                                         List<String> classCodes, List<String> hcftCodes,
                                         List<String> eventCodes) throws NoServletSessionException {
        session().isTls = site.isTls;
        return session().queryServiceManager().mpqFindDocuments(site, pid, classCodes, hcftCodes,
                eventCodes);
    }
    @Override
    public List<Result> mpqFindDocuments(MpqFindDocumentsRequest request) throws Exception {
        installCommandContext(request);
        session().isTls = request.getSite().isTls;
        return session().queryServiceManager().mpqFindDocuments(request.getSite(), request.getPid(), request.getSelectedCodes());
    }

    @Override
    public List<Result> getLastMetadata(CommandContext context) throws Exception {
        installCommandContext(context);
        return queryServiceManager.getLastMetadata(); }

    private void logCall(String callName) {
        logger.info(sessionID + ": " + callName);
    }

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // TestSession
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------

    @Override
    public boolean testSessionExists(CommandContext request) throws Exception {
        installCommandContext(request);
        logCall("testSessionExists");
        return TestSessionServiceManager.exists(request.getTestSessionName());
    }

    @Override
    public TestSession buildTestSession() throws Exception {
        logCall("buildTestSession");
        return TestSessionServiceManager.create();
    }

    @Override
    public String setTestSession(String sessionName)  throws NoServletSessionException {
        TestSession testSession = new TestSession(sessionName);
        if (sessionName != null && !testSession.equals(TestSessionServiceManager.INSTANCE.getTestSession(session())))
            logCall("setTestSession " + sessionName);
        TestSessionServiceManager.setTestSession(session(), testSession);
        return sessionName;
    }
    @Override
    public List<String> getTestSessionNames(CommandContext request) throws Exception {
        installCommandContext(request);
//        if (Installation.instance().propertyServiceManager().isMultiuserMode())
//            throw new ToolkitRuntimeException("Function getTestSessionNames() not available in MulitUserMode");
        return TestSessionServiceManager.getNames();
    }

    @Override
    public boolean addTestSession(CommandContext context) throws Exception {
        logCall("addTestSession " + context.getTestSessionName());
        return TestSessionServiceManager.create(context.getTestSession());
    }

    @Override
    public boolean deleteTestSession(CommandContext context) throws Exception {
        logCall("deleteTestSession " + context.getTestSessionName());
        return TestSessionServiceManager.delete(context.getTestSession());
    }


    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Test Service
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // New - Loads or reloads test data
    @Override
    public List<Test> reloadAllTestResults(CommandContext context) throws Exception {
        installCommandContext(context);
        return session().xdsTestServiceManager().reloadAllTestResults(context.getTestSessionName());
    }
    @Override
    public List<TestInstance> getTestlogListing(String sessionName) throws Exception { return session().xdsTestServiceManager().getTestlogListing(sessionName); }
    @Override
    public Map<String, Result> getTestResults(GetTestResultsRequest request)  throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getTestResults(request.getTestIds(), request.getEnvironmentName(), request.getTestSession());
    }

    @Override
    public List<Test> deleteAllTestResults(AllTestRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().deleteAllTestResults(request.getTestSessionName(), request.getSite());
    }
    @Override
    public TestOverviewDTO deleteSingleTestResult(DeleteSingleTestRequest request) throws Exception {
        installCommandContext(request);
        request.getTestInstance().setTestSession(request.getTestSession());
        return session().xdsTestServiceManager().deleteSingleTestResult(request.getEnvironmentName(), session().getTestSession(),request.getTestInstance());
    }
    @Override
    public List<Test> runAllTests(AllTestRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().runAllTests(request.getTestSessionName(), request.getSite()); }
    @Override
    public Test runSingleTest(RunSingleTestRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().runSingleTest(request.getTestSessionName(), request.getSite(), request.getTestId());
    }

    public String getTestReadme(String testSession,String test) throws Exception {
        session().setTestSession(new TestSession(testSession));
        return session().xdsTestServiceManager().getTestReadme(test);
    }
    @Override
    public RawResponse buildRepTestOrchestration(BuildRepTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRepTestEnvironment(s, request.getRepOrchestrationRequest());
    }
    @Override
    public RawResponse buildRegTestOrchestration(BuildRegTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRegTestEnvironment(s, request.getRegOrchestrationRequest());
    }
    @Override
    public RawResponse buildRecTestOrchestration(BuildRecTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRecTestEnvironment(s, request.getRecOrchestrationRequest());
    }

    @Override
    public RawResponse buildIgTestOrchestration(BuildIgTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIgTestEnvironment(s, request.getIgOrchestrationRequest());
    }
    @Override
    public RawResponse buildIigTestOrchestration(BuildIigTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIigTestEnvironment(s, request.getIigOrchestrationRequest());
    }
    @Override
    public RawResponse buildRgTestOrchestration(BuildRgTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRgTestEnvironment(s, request.getRgOrchestrationRequest());
    }
    @Override
    public RawResponse buildRigTestOrchestration(BuildRigTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRigTestEnvironment(s, request.getRigOrchestrationRequest());
    }
    @Override
    public RawResponse buildIdsTestOrchestration(BuildIdsTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIdsTestEnvironment(s, request.getIdsOrchestrationRequest());
    }
    @Override
    public RawResponse buildIdcTestOrchestration(IdcOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIdcTestEnvironment(s, request);
    }
    @Override
    public RawResponse buildEsTestOrchestration(BuildEsTestOrchestrationRequest request) {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildEstTestEnvironment(s, request.getEsOrchestrationRequest());
    }
    @Override
    public RawResponse buildRSNAEdgeTestOrchestration(BuildRSNAEdgeTestOrchestrationRequest request) throws Exception{
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildRSNAEdgeTestEnvironment(s, request.getRsnaEdgeOrchestrationRequest());
    }

    @Override
    public RawResponse buildDocAdminTestOrchestration(BuildDocAdminTestOrchestrationRequest request) throws Exception {
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildDocAdminTestEnvironment(s, request.getDocAdminOrchestrationRequest());
    }

    @Override
    public RawResponse buildSrcTestOrchestration(BuildSrcTestOrchestrationRequest request) throws Exception {
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildSrcTestEnvironment(s, request.getSrcOrchestrationRequest());
    }

    @Override
    public RawResponse buildIsrTestOrchestration(BuildIsrTestOrchestrationRequest request) throws Exception {
        installCommandContext(request);
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        return new OrchestrationManager().buildIsrTestEnvironment(s, request.getIsrOrchestrationRequest());
    }


    @Override
    public RawResponse buildFhirSupportOrchestration(FhirSupportOrchestrationRequest request) throws Exception {
        Session s = getSession();
        if (s == null) return RawResponseBuilder.build(new NoServletSessionException(""));
        RawResponse response = new OrchestrationManager().buildFhirSupportEnvironment(s, request);
        FhirSupportOrchestrationResponse theResponse = (FhirSupportOrchestrationResponse) response;
        return theResponse;
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
        session().setTestSession(new TestSession(testSession));
        return session().xdsTestServiceManager().getTestSections(test);
    }

    //	public List<Result> getLogContent(String sessionName, TestInstance testInstance) throws Exception { return session().xdsTestServiceManager().getLogContent(sessionName, testInstance); }
    public List<Result> runMesaTest(String environmentName,String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections, Map<String, String> params, boolean stopOnFirstFailure)  throws Exception {
        return session().xdsTestServiceManager().runMesaTest(environmentName, new TestSession(mesaTestSession), siteSpec, testInstance, sections, params, null, stopOnFirstFailure);
    }
    /**
     * Get list of section names defined for the test in the order they should be executed
     * @return list of sections
     * @throws Exception if something goes wrong
     */
    @Override
    public List<String> getTestIndex(GetTestDetailsRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getTestSections(request.getTest());
    }
    /**
     * Get map of (collection name, collection description) pairs contained in testkit
     * @return the map
     * @throws Exception is something goes wrong
     */
    @Override
    public Map<String, String> getCollectionNames(GetCollectionRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getCollectionNames(request.getCollectionSetName());
    }
    @Override
    public List<TestInstance> getCollectionMembers(GetCollectionRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getCollectionMembers(request.getCollectionSetName(), request.getTcId());
    }

    @Override
    public List<TestOverviewDTO> getTestsOverview(GetTestsOverviewRequest request) throws Exception {
        installCommandContext(request);
        List<TestOverviewDTO> o = session().xdsTestServiceManager().getTestsOverview(request.getTestSession(), request.getTestInstances());
        return o;
    }

    @Override
    public List<TestOverviewDTO> getActorTestProgress(GetTestsOverviewRequest request) throws Exception {
        installCommandContext(request);
        QuickScanLog quickScanLog = new QuickScanLog(session());

        List<TestOverviewDTO> o = quickScanLog.quickScanLogs(request.getTestSession(), request.getTestInstances(), request.getQuickScanAttributes());
        return o;
    }

    public List<SectionDefinitionDAO> getTestSectionsDAOs(GetTestSectionsDAOsRequest request) throws Exception {
        installCommandContext(request);
        Session session = session().xdsTestServiceManager().session;
        session.setTestSession(request.getTestSession());
        return session().xdsTestServiceManager().getTestSectionsDAOs(request.getTestInstance());
    }
    @Override
    public LogFileContentDTO getTestLogDetails(GetTestLogDetailsRequest request) throws Exception {
        installCommandContext(request);
        LogFileContentDTO o = session().xdsTestServiceManager().getTestLogDetails(request.getTestSession(), request.getTestInstance());
        return o;
    }
    @Override
    public List<TestCollectionDefinitionDAO> getTestCollections(GetCollectionRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getTestCollections(request.getCollectionSetName());
    }

    @Override
    public TabConfig getToolTabConfig(GetTabConfigRequest request) throws Exception {
        String toolId = request.getToolId();
        TabConfigLoader.init(Installation.instance().getToolTabConfigFile(toolId));
        TabConfig tabConfigRoot = TabConfigLoader.getTabConfig(toolId);
        return tabConfigRoot;
    }

    @Override
    public UserTestCollection getPrunedToolTabConfig(GetTabConfigRequest request) throws Exception {
        installCommandContext(request);

        String toolId = request.getToolId();
        TabConfigLoader.init(Installation.instance().getToolTabConfigFile(toolId));
        TabConfig tabConfigRoot = TabConfigLoader.getTabConfig(toolId);

        GetCollectionRequest getCollectionRequest = new GetCollectionRequest(request, "actorcollections");

        // Sort according to the user defined tab config
        List<TestCollectionDefinitionDAO> tcDefs = getTestCollections(getCollectionRequest);
        UserTestCollection userTestCollection = new UserTestCollection();
        userTestCollection.setTabConfig(tabConfigRoot);

        for (TabConfig tabConfig : tabConfigRoot.getChildTabConfigs()) {
            for (TestCollectionDefinitionDAO tcd : tcDefs) {
                if (tabConfig.getTcCode().equals(new ActorOption(tcd.getCollectionID()).actorTypeId)) {
                    userTestCollection.getTestCollectionDefinitionDAOs().add(tcd);
                    break;
                }
            }
        }

        for (final TestCollectionDefinitionDAO tcd : userTestCollection.getTestCollectionDefinitionDAOs()) {
            for (TabConfig tabConfig : tabConfigRoot.getChildTabConfigs()) {
                ActorOption actorOption = new ActorOption(tcd.getCollectionID());
                if (tabConfig.getTcCode().equals(actorOption.actorTypeId)) {
                    tabConfig.setLabel(tcd.getCollectionTitle());
                    // Prune empty options
                    TabConfig profiles = tabConfig.getFirstChildTabConfig();
                    if ("Profiles".equals(profiles.getLabel())) {
                        for (final TabConfig profileTCfg : profiles.getChildTabConfigs()) {
                            TabConfig options = profileTCfg.getFirstChildTabConfig();
                            if ("Options".equals(options.getLabel())) {
                                for (final TabConfig optionTCfg : options.getChildTabConfigs()) {
                                    ActorOptionConfig actorOptionConfig =
                                            new ActorOptionConfig(tabConfig.getTcCode(), IheItiProfile.find(profileTCfg.getTcCode()), optionTCfg.getTcCode());

                                    GetCollectionRequest actorCollectionsRequest = new GetCollectionRequest(request, "collections", actorOptionConfig.getTestCollectionCode());
                                    List<TestInstance> testInstances = getCollectionMembers(actorCollectionsRequest);

                                    if (testInstances != null && !testInstances.isEmpty()) {
                                        tabConfig.setVisible(true);
                                        profileTCfg.setVisible(true);
                                        optionTCfg.setVisible(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return userTestCollection;
    }

    @Override
    public Map<String, String> getCollection(GetCollectionRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getCollection(request.getCollectionSetName(), request.getTcId());
    }

    @Override
    public String getTestReadme(GetTestDetailsRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getTestReadme(request.getTest());
    }

    @Override
    public List<TestSessionStats> getTestSessionStats(CommandContext commandContext) throws Exception {
        installCommandContext(commandContext);
        return TestSessionServiceManager.getTestSessionStats();
    }

    @Override
    public List<Result> runMesaTest(RunTestRequest request)  throws Exception {
        installCommandContext(request);
        session().isTls = request.getSiteSpec().isTls;
        return session().xdsTestServiceManager().runMesaTest(request.getEnvironmentName(), request.getTestSession(), request.getSiteSpec(), request.getTestInstance(), request.getSections(), request.getParams(), null, request.isStopOnFirstFailure());
    }
    @Override
    public TestOverviewDTO runTest(RunTestRequest request) throws Exception {
        installCommandContext(request);
        session().isTls = request.getSiteSpec().isTls;
        List<String> sections = new ArrayList<>();
        if (request.getTestInstance().getSection() != null) sections.add(request.getTestInstance().getSection());
        setEnvironment(request.getEnvironmentName());
        Session session = session().xdsTestServiceManager().session;
        session.setCurrentEnvName(request.getEnvironmentName());
        session.setTestSession(request.getTestSession());
        if (request.getSiteSpec() == null)
            throw new Exception("No site selected");
        if (!new SimManager(request.getTestSessionName()).exists(request.getSiteSpec().name, request.getTestSession()))
            throw new Exception("Site " + request.getSiteSpec().name + " does not exist");
        TestOverviewDTO testOverviewDTO = session().xdsTestServiceManager().runTest(request.getEnvironmentName(), request.getTestSession(), request.getSiteSpec(), request.getTestInstance(), sections, request.getParams(), null, request.isStopOnFirstFailure());
        return testOverviewDTO;
    }
    // TODO remove this once command pattern is implemented for every single call
    private void setEnvironment(String environmentName) throws NoServletSessionException {
        session().setEnvironment(environmentName);
    }
    @Override
    public TestLogs getRawLogs(GetRawLogsRequest request)  throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getRawLogs(request.getLogId());
    }

    @Override
    public List<Message> getFhirResult(GetRawLogsRequest request)  throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getFhirResult(request.getLogId());
    }

    @Override
    public List<String> getTestdataSetListing(GetTestdataSetListingRequest request)  throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().getTestdataSetListing(request.getEnvironmentName(),request.getTestSession(),request.getTestdataSetName());
    }
    @Override
    public String getTestplanAsText(GetTestplanAsTextRequest request) throws Exception {
        installCommandContext(request);
        session().setTestSession(request.getTestSession());
        return session().xdsTestServiceManager().getTestplanAsText(request.getTestInstance(), request.getSection());
    }
    @Override
    public TestPartFileDTO getSectionTestPartFile(GetSectionTestPartFileRequest request) throws Exception {
        installCommandContext(request);
        session().setTestSession(request.getTestSession());
        return session().xdsTestServiceManager().getSectionTestPartFile(request.getTestInstance(), request.getSection());
    }
    @Override
    public TestPartFileDTO loadTestPartContent(LoadTestPartContentRequest request) throws Exception {
        installCommandContext(request);
        return XdsTestServiceManager.loadTestPartContent(request.getTestPartFileDTO());
    }
    @Override
    public String getHtmlizedString(String xml) { // This is different than the Htmlize class in the client code works (see its isHtml method)
        return XmlFormatter.htmlize(xml)
                .replace("<br/>", "\r\n");
    }
    @Override
    public CodesResult getCodesConfiguration(CommandContext context)  throws Exception {
        installCommandContext(context);
        setEnvironment(context.getEnvironmentName());
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
    public Map<String, String> getCollection(String testsessionName,String collectionSetName, TestCollectionCode collectionName) throws Exception {
        session().setTestSession(new TestSession(testsessionName));
        return session().xdsTestServiceManager().getCollection(collectionSetName, collectionName);
    }
    @Override
    public boolean isPrivateMesaTesting()  throws NoServletSessionException { return session().xdsTestServiceManager().isPrivateMesaTesting(); }
    @Override
    public List<Result> sendPidToRegistry(SendPidToRegistryRequest request) throws Exception {
        installCommandContext(request);
        return session().xdsTestServiceManager().sendPidToRegistry(request.getSiteSpec(), request.getPid(), request.getEnvironmentName(), request.getTestSession());
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
        updater.run(environmentFile.getAbsolutePath(),defaultTestkit.getAbsolutePath(), context.getTestSession());
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
        return TestKit.exists(context.getEnvironmentName(), context.getTestSession());
//        File environmentFile = Installation.instance().environmentFile(context.getEnvironmentName());
//        return doesTestkitExist(environmentFile);
    }

    /**
     * This method generate the folder structure for the testkit in all the environments available.
     * @param request
     */
    @Override
    public void generateTestkitStructure(CommandContext request) /*throws Exception*/{
        TestKit.generateStructure(request.getEnvironmentName(), request.getTestSession());
//        File environmentFile = Installation.instance().environmentFile();
//        for (File environment : environmentFile.listFiles()) {
//            File testkitsFile = new File(environment, "testkits");
//            if (!testkitsFile.exists()) {
//                new File(testkitsFile, "default").mkdirs();
//            }
//            for (Sections section : Sections.values()) {
//                File sectionFile = new File(new File(testkitsFile,"default"), section.getSection());
//                if (!sectionFile.exists()){
//                    sectionFile.mkdir();
//                }
//            }
//        }
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
        return session().xdsTestServiceManager().validateConformanceSession(new TestSession(testSession), siteName);
    }

    @Override
    public Collection<String> getSitesForTestSession(CommandContext context) throws Exception {
        installCommandContext(context);
        if (context.getTestSessionName()== null)
            return new ArrayList<>();
        return session().xdsTestServiceManager().getSitesForTestSession(context.getTestSession());
    }

    @Override
    public String getDefaultAssigningAuthority(CommandContext context) throws Exception {
//        installCommandContext(context);
        return Installation.instance().propertyServiceManager().getDefaultAssigningAuthority();
    }
    @Override
    public String getImplementationVersion(CommandContext context) throws Exception  {
//        installCommandContext(context);
        return Installation.instance().propertyServiceManager().getImplementationVersion();
    }
    @Override
    public Map<String, String> getToolkitProperties(CommandContext context)  throws Exception {
//        installCommandContext(context);
        try {
            return Installation.instance().propertyServiceManager().getToolkitProperties();
        } catch (Throwable t) {
            throw new Exception(t.getMessage());
        }
    }
    @Override
    public boolean isGazelleConfigFeedEnabled(CommandContext context) throws Exception {
        installCommandContext(context);
        return SiteServiceManager.getSiteServiceManager().useGazelleConfigFeed();
    }
    //	public String getToolkitEnableNwHIN() { return propertyServiceManager.getToolkitEnableNwHIN(); }
    @Override
    public String setToolkitProperties(SetToolkitPropertiesRequest request) throws Exception {
//        installCommandContext(request);
        return setToolkitPropertiesImpl(request.getProperties());
    }
    @Override
    public String getAdminPassword(CommandContext context) throws Exception  {
//        installCommandContext(context);
        return Installation.instance().propertyServiceManager().getAdminPassword();
    }
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
    public String putSimConfig(SimConfigRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).saveSimConfig(request.getConfig());
    }
    // this deletes a simulator
    @Override
    public String deleteConfig(SimConfigRequest request) throws Exception {
        installCommandContext(request);
        if (request.getConfig()!=null)
            return new SimulatorServiceManager(session()).delete(request.getConfig());
        else if (request.getConfigList()!=null)
            return new SimulatorServiceManager(session()).deleteConfigs(request.getConfigList());
        return "";
    }
    @Override
    public void renameSimFile(RenameSimFileRequest request) throws Exception {
        installCommandContext(request);
        new SimulatorServiceManager(session()).renameSimFile(request.getOldSimFileName(), request.getNewSimFileName());
    }
    @Override
    public String getSimulatorEndpoint(CommandContext context) throws Exception {
        installCommandContext(context);
        return new SimulatorServiceManager(session()).getSimulatorEndpoint();
    }
    @Override
    public MessageValidationResults executeSimMessage(ExecuteSimMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).executeSimMessage(request.getFileName());
    }
    @Override
    public List<TransactionInstance> getTransInstances(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransInstances(request.getSimid(), request.getActor(), request.getTrans());
    }
    @Override
    public List<List<TransactionInstance>> getTransInstancesLists(GetTransactionListsRequest request) throws Exception {
        try {
            installCommandContext(request);
            SimulatorServiceManager ssm = new SimulatorServiceManager(session());
            List<List<TransactionInstance>> lists = new ArrayList<List<TransactionInstance>>();
            for (SimId simId : request.getSimIds()) {
                lists.add(ssm.getTransInstances(simId, "", null));
            }
            return lists;
        } catch (Exception ex) {
            return null;
        }
    }
    @Override
    public Message getTransactionRequest(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionRequest(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }
    @Override
    public Message getTransactionResponse(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionResponse(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }
    @Override
    public int removeOldSimulators(CommandContext context) throws Exception {
        installCommandContext(context);
        return new SimulatorServiceManager(session()).removeOldSimulators(context.getTestSession());
    }
    @Override
    public List<Result> getSelectedMessage(GetSelectedMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getSelectedMessage(request.getFilename());
    }
    @Override
    public List<Result> getSelectedMessageResponse(GetSelectedMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getSelectedMessageResponse(request.getFilename());
    }
    @Override
    public List<SimId> getSimIdsForUser(GetSimIdsForUserRequest context) throws Exception {
        installCommandContext(context);
        return new SimulatorServiceManager(session()).getSimIds(context.getTestSession().getValue());
    }

    @Override
    public SimId getFullSimId(GetFullSimIdRequest request) throws Exception {
        installCommandContext(request);
        try {
            return SimDb.getFullSimId(request.getSimId());
        } catch (Exception e) {
            logger.error("getFullSimId - error - " + e.getMessage());
            throw e;
        }
    }

    /**
     * This method will compare the original MetedataCollection (from originalGetDocsTestInstance) to the MetadataCollection that was updated.
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public Result updateDocumentEntry(UpdateDocumentEntryRequest request) throws Exception {
        installCommandContext(request);
        Result result = new MetadataUpdate(session()).updateDocumentEntry(request);

        logger.info("Update result was: " + result.passed());
        return result;
    }

    @Override
    public MessageValidationResults validateDocumentEntry(ValidateDocumentEntryRequest request) throws Exception {
        MessageValidationResults mvr = new MessageValidationResults();
        installCommandContext(request);
        RegistryErrorListGenerator regErrorListGen  = null;

        ValidationContext vc = DefaultValidationContextFactory.validationContext();
//        if ("r".equals(tname)) vc.isR = true;
//        vc.isStableOrODDE
        vc.isMU = true;

        try {
            logger.info("Codes file is " + session().getCodesFile());
            vc.setCodesFilename(session().getCodesFile().toString());
        } catch (Exception e) {
           // What to do here?
        }
        try {
            MetadataCollection mc = new MetadataCollection();
            mc.docEntries.add(request.getDe());
            Metadata m2 = MetadataCollectionToMetadata.buildMetadata(mc, true);
            regErrorListGen = RegistryUtility.metadata_validator(m2, vc);
        } catch (Exception e) {
            // not all responses contain metadata
            regErrorListGen = new RegistryErrorListGenerator(RegistryErrorListGenerator.version_3);
        }

        // THis is useful?
//        regErrorListGen.has_errors()
//        regErrorListGen.getErrMsgs()
//        regErrorListGen.getErrorsAndWarnings()

        ArrayList<String> validatorErrors = new RegistryResponseParser(regErrorListGen.getRegistryErrorList()).get_error_code_contexts();

        if (validatorErrors.size() != 0) {
            StringBuilder msg = new StringBuilder();
            for (int i=0; i<validatorErrors.size(); i++) {
                msg.append(validatorErrors.get(i));
                msg.append("\n");
            }
            mvr.addHtmlResults(msg.toString());
        }

        return mvr;
    }



    @Override
    public MessageValidationResults validateMessage(ValidateMessageRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).validateMessage(request.getValidationContext());
    }
    @Override
    public List<SimulatorConfig> getSimConfigs(GetSimConfigsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getSimConfigs(request.getIds());
    }
    @Override
    public List<SimulatorConfig> getAllSimConfigs(GetAllSimConfigsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getAllSimConfigs(request.getTestSession());
    }
    @Override
    public Simulator getNewSimulator(GetNewSimulatorRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getNewSimulator(request.getActorTypeName(), request.getSimId(), getDefaultEnvironment(request));
    }
    @Override
    public void deleteSimFile(DeleteSimFileRequest request) throws Exception {
        installCommandContext(request);
        new SimulatorServiceManager(session()).deleteSimFile(request.getSimFileSpec());
    }
    @Override
    public List<String> getTransactionsForSimulator(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionsForSimulator(request.getSimid());
    }
    @Override
    public List<SimulatorStats> getSimulatorStats(GetSimulatorStatsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getSimulatorStats(request.getSimid());
    }
    @Override
    public String getTransactionLog(GetTransactionRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getTransactionLog(request.getSimid(), request.getActor(), request.getTrans(), request.getMessageId());
    }

    @Override
    public TransactionInstance getTransactionLogDirectoryPath(GetTransactionLogDirectoryPathRequest r) throws Exception {
        Path p = PrsSimLogs.getTransactionLogDirectoryPath(r.getSimId(),r.getTransactionCode(),r.getPid(),r.getHl7timeOfSectionRun(), false);

       if (p!=null) {
            String transactionId = p.getFileName().toString();

        TransactionInstance ti = new TransactionInstance();
        ti.simId = r.getSimId().toString();
        ti.actorType = ActorType.findActor(r.getSimId().getActorType());
        ti.labelInterpretedAsDate = transactionId;
        ti.nameInterpretedAsTransactionType = TransactionType.find(r.getTransactionCode());
        ti.messageId = transactionId;
        ti.trans = r.getTransactionCode();

        return ti;
       }

        return null;
    }

    /**
     * This is our best-guess. If there are multiple SUT-initiated transactions per second as recorded in the log.xml, we will need more precision on the log.xml timestamp.
     * @param stiRequest
     * @return
     * @throws Exception
     */
    @Override
    public List<InteractingEntity> setSutInitiatedTransactionInstance(SetSutInitiatedTransactionInstanceRequest stiRequest) throws Exception {

        List<InteractingEntity> src = stiRequest.getInteractingEntityList();
        List<String> messageIdsInUse = new ArrayList<>();
        for (InteractingEntity ie : src) {
            setInitiatorTransactions(ie, stiRequest.getTranDestinationSimId(), stiRequest.getPatienId(), messageIdsInUse);
        }
        return src;
    }

    private void setInitiatorTransactions(InteractingEntity parent, SimId simId, String patientId, final List<String> messageIdsInUse) throws Exception {
        if (parent == null) return;

        boolean hasInteractions = parent.getInteractions() != null && !parent.getInteractions().isEmpty();

        if (hasInteractions) {
            if (InteractingEntity.SYSTEM_UNDER_TEST.equals(parent.getProvider())) {
                List<InteractingEntity> interactions = parent.getInteractions();
                for (final InteractingEntity ie : interactions) {
                    if (InteractingEntity.SIMULATOR.equals(ie.getProvider())) {
                        final GetTransactionLogDirectoryPathRequest request = new GetTransactionLogDirectoryPathRequest();
                        simId.setActorType(ActorType.findActor(ie.getRole()).getShortName());
                        request.setSimId(simId);
                        request.setPid(patientId);
                        request.setTransactionCode(ie.getSourceInteractionLabel());
                        request.setHl7timeOfSectionRun(parent.getBegin());

                        TransactionInstance tranInstance = getTransactionLogDirectoryPath(request);
                        if (tranInstance!=null) {
                            if (!messageIdsInUse.contains(tranInstance.messageId) ) {
                                messageIdsInUse.add(tranInstance.messageId);
                                ie.setStatus(InteractingEntity.INTERACTIONSTATUS.COMPLETED);
                                ie.setTransactionInstance(tranInstance);
                            } else {
                                /* Oops, transaction already taken by another section/step !?!. */
                                ie.setStatus(InteractingEntity.INTERACTIONSTATUS.UNKNOWN);
                            }
                        } else {
                            ie.setStatus(InteractingEntity.INTERACTIONSTATUS.UNKNOWN);
                        }
                    }
                }
            } else {
                for (InteractingEntity ie : parent.getInteractions()) {
                    setInitiatorTransactions(ie, simId, patientId,messageIdsInUse);
                }
            }
        }
    }


        @Override
    public List<Pid> getPatientIds(PatientIdsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).getPatientIds(request.getSimId());
    }
    @Override
    public String addPatientIds(PatientIdsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).addPatientIds(request.getSimId(), request.getPids());
    }
    @Override
    public boolean deletePatientIds(PatientIdsRequest request) throws Exception {
        installCommandContext(request);
        return new SimulatorServiceManager(session()).deletePatientIds(request.getSimId(), request.getPids());
    }
    @Override
    public Result getSimulatorEventRequest(GetSimulatorEventRequest request) throws Exception {
        installCommandContext(request);
        try {
            return new SimulatorServiceManager(session()).getSimulatorEventRequestAsResult(request.getTransactionInstance());
        } catch (Throwable e) {
            logger.error(ExceptionUtil.exception_details(e));
            throw e;
        }
    }
    @Override
    public Result getSimulatorEventResponse(GetSimulatorEventRequest request) throws Exception {
        installCommandContext(request);
        try {
            return new SimulatorServiceManager(session()).getSimulatorEventResponseAsResult(request.getTransactionInstance());
        } catch (Throwable e) {
            logger.error(ExceptionUtil.exception_details(e));
            throw e;
        }
    }
    @Override
    public List<String> getTransactionErrorCodeRefs(GetTransactionErrorCodeRefsRequest request) throws Exception {
        installCommandContext(request);
        List<String> refs = TransactionErrorCodeDbLoader.LOAD().getRefsByTransaction(TransactionType.find(request.getTransactionName()), request.getSeverity());
        logger.info(": getTransactionErrorCodeRefs(" + request.getTransactionName() + ") => " + refs.size() + " codes");
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
//        logger.info("Context Name is " + context.getServletContextName());
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
//        logHere("On Session " + s.getId());
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
    public String getLastFilename(CommandContext context) throws Exception {
        installCommandContext(context);
        return getSession().getlastUploadFilename();
    }

    @Override
    public String getTimeAndDate(CommandContext context) throws Exception {
        installCommandContext(context);
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
    public Result register(RegisterRequest request) throws Exception{
        installCommandContext(request);
        return TransactionUtil.register(getSession(),request.getTestSession(),request.getTestInstance(),
                request.getRegistry(),request.getParams(), new ArrayList<String>());
    }
    @Override
    public Map<String, String> registerWithLocalizedTrackingInODDS(RegisterRequest request) throws Exception{
        installCommandContext(request);
        try {
            return TransactionUtil.registerWithLocalizedTrackingInODDS(getSession(),request.getTestSession(),
                    request.getTestInstance(),request.getRegistry(),request.getOddsSimId(), request.getParams());
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
        return siteServiceManager.getSiteNamesByTran(request.getTransactionTypeName(), session().getId(), request.getTestSession());
    }

    @Override
    public List<DocumentEntryDetail> getOnDemandDocumentEntryDetails(GetOnDemandDocumentEntryDetailsRequest request) throws Exception{
        installCommandContext(request);
        return TransactionUtil.getOnDemandDocumentEntryDetails(request.getSimId());
    }


    @Override
    public boolean setOdSupplyStateIndex(SetOdSupplyStateIndexRequest request) throws Exception {
        installCommandContext(request);

        SimId oddsSimId = request.getOddsSimId();
        SimDb simDb = new SimDb(oddsSimId);
        RepIndex repIndex = new RepIndex(simDb.getRepositoryIndexFile().toString(), oddsSimId);
        StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(request.getDed().getUniqueId());
        if (sd!=null) {
            sd.getEntryDetail().setSupplyStateIndex(request.getNewIdx());
            repIndex.getDocumentCollection().update(sd);
            repIndex.save();
            return true;
        } else {
            logger.error("setOdSupplyStateIndex Error: StoredDocument is null for the requested DocumentEntry UniqueId:" + request.getDed().getUniqueId());
        }

       return false;
    }

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Interaction methods
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    @Override
    public InteractingEntity getInteractionFromModel(GetInteractionFromModelRequest request) throws Exception {
        installCommandContext(request);
        return new InteractionMapper().map(request.getModel());
    }


    public String getStsSamlAssertion(GetStsSamlAssertionRequest request) throws Exception {
        installCommandContext(request);
        XdsTestServiceManager xtsm = session().getXdsTestServiceManager();
        String step = "issue";
        String query = request.getTestInstance().getSection();
        String stsActor = Installation.instance().propertyServiceManager().getStsActorName();
        List<Result> results = xtsm.querySts(stsActor,query,request.getParams(), false, request.getTestSession());

        if (results!=null) {
            if (results.size() == 1) {
                Result mainResult =  results.get(0);
                if (!mainResult.passed()) {
                    if (results.get(0).getStepResults()!=null && results.get(0).getStepResults().size()>0) {
                        List<String> soapFaults = results.get(0).getStepResults().get(0).getSoapFaults();
                        if (soapFaults != null && soapFaults.size() > 0) {
                            throw new ToolkitRuntimeException("getStsSamlAssertion SOAP Fault: " + soapFaults.toString());
                        }
                    }  else {
                        throw new ToolkitRuntimeException("getStsSamlAssertion: " + mainResult.toString());
                    }

                } else {
                    LogFileContentDTO logFileContentDTO = xtsm.getTestLogDetails(request.getTestSession(),request.getTestInstance());
                    TestStepLogContentDTO testStepLogContentDTO = logFileContentDTO.getStep(step);
                    List<ReportDTO> reportDTOs = testStepLogContentDTO.getReportDTOs();
                    String assertionResultId = "saml-assertion";
                    for (ReportDTO report : reportDTOs)  {
                        if (assertionResultId.equals(report.getName())) {
                            return report.getValue().replace("&","&amp;");
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


    public Map<String,String> getStsSamlAssertionsMap(GetStsSamlAssertionMapRequest request) throws Exception {
        Map<String,String> assertionMap = null;
        for (GazelleXuaUsername username : GazelleXuaUsername.values()) {
            String usernameStr = username.name();
            if (request.getParams()!=null) {
                request.getParams().clear();
                request.getParams().put("$saml-username$", usernameStr);
            }
            try {
                GetStsSamlAssertionRequest getStsSamlAssertionRequest = new GetStsSamlAssertionRequest(request,usernameStr,request.getTestInstance(),request.getSiteSpec(),request.getParams());
                String samlAssertion = getStsSamlAssertion(getStsSamlAssertionRequest);
                if (samlAssertion!=null) {
                    if (assertionMap == null) {
                        assertionMap = new HashMap<String,String>();
                    }
                    assertionMap.put(usernameStr, samlAssertion);
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        return assertionMap;
    }



    @Override
    public String clearTestSession(CommandContext context) throws Exception {
        installCommandContext(context);
        return session().xdsTestServiceManager().clearTestSession(context.getTestSession());
    }

    @Override
    public boolean getAutoInitConformanceTesting(CommandContext context) {
        return Installation.instance().propertyServiceManager().getAutoInitializeConformanceTool();
    }

    @Override
    public boolean indexTestKits(CommandContext context) {
        new BuildCollections().run();
        // FIXME why does this have to always return true? should we change for a void method?
        return true;
    }

    @Override
    public List<DatasetModel> getAllDatasets(CommandContext context) throws Exception {
        installCommandContext(context);
        logger.debug(sessionID + ": getAllDatasets()");
        return DatasetFactory.getAllDatasets();
    }

    @Override
    public List<Result> fhirCreate(FhirCreateRequest request) throws Exception {
        installCommandContext(request);
        logger.debug(sessionID + ": fhirCreate()");
        request.getSite().testSession = request.getTestSession();
        List<Result> results = new FhirServiceManager(session()).create(request.getSite(), request.getDatasetElement());
        return results;
    }

    @Override
    public List<Result> fhirTransaction(FhirTransactionRequest request) throws Exception {
        installCommandContext(request);
        logger.debug(sessionID + ": fhirTransaction()");
        request.getSite().testSession = request.getTestSession();
        List<Result> results = new FhirServiceManager(session()).transaction(request.getSite(), request.getDatasetElement());
        return results;
    }

    @Override
    public List<Result> fhirSearch(FhirSearchRequest request) throws Exception {
        installCommandContext(request);
        logger.debug(sessionID + ": fhirSearch()");
        request.getSite().testSession = request.getTestSession();
        List<Result> results = new FhirServiceManager(session()).search(request.getSite(), request.getResourceTypeName(), request.getCodesSpec());
        return results;
    }

    @Override
    public List<Result> fhirRead(FhirReadRequest request) throws Exception {
        installCommandContext(request);
        logger.debug(sessionID + ": fhirRead()");
        request.getSite().testSession = request.getTestSession();
        List<Result> results = new FhirServiceManager(session()).read(request.getSite(), request.getReference());
        return results;
    }

    @Override
    public String getDatasetContent(GetDatasetElementContentRequest request)  {
        try {
            return DatasetFactory.getContentForDisplay(request.getDatasetElement());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String promote(PromoteRequest request) {
        try {
            siteServiceManager.promoteSiteToDefault(request.getSite().name, request.getTestSession());
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

}
