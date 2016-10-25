package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymsg.repository.RetrieveResponseParser;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.MetadataToMetadataCollectionParser;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.session.client.ConformanceSessionValidationStatus;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import gov.nist.toolkit.session.client.TestPartFileDTO;
import gov.nist.toolkit.session.server.CodesConfigurationBuilder;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestOverviewBuilder;
import gov.nist.toolkit.session.server.services.TestLogCache;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.testengine.engine.TestLogsBuilder;
import gov.nist.toolkit.testengine.engine.Xdstest2;
import gov.nist.toolkit.testenginelogging.LogFileContentBuilder;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.testkitutilities.client.TestCollectionDefinitionDAO;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Parse;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlFormatter;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class XdsTestServiceManager extends CommonService {
	private CodesConfiguration codesConfiguration = null;
	public Session session;
	static Logger logger = Logger.getLogger(XdsTestServiceManager.class);
	static boolean allCiphersEnabled = false;

	public XdsTestServiceManager(Session session)  {
		this.session = session;
		if (session != null)
			logger.info("XdsTestServiceManager: using session " + session.getId());
	}

	public static Logger getLogger() {
		return logger;
	}



	TestLogCache getTestLogCache() throws IOException {
		return new TestLogCache(Installation.instance().propertyServiceManager().getTestLogCache());
	}

	/**
	 * Wrapper around run to be used for using the test-client as a utility.  The difference between using
	 * it as a utility or as a test is how and where the logs are stored.  For tests they are stored in
	 * the external cache under TestLogCache and for utilities they are stored in war/SessionCache.  Logs in
	 * TestLogCache are permanent (manually deleted) and in SessionCache they are delete when the session
	 * times out.
	 * @param testInstance
	 * @param sections
	 * @param params
	 * @param areas
	 * @param stopOnFirstFailure
	 * @return
	 */
	public Result xdstest(TestInstance testInstance, List<String> sections,
						  Map<String, String> params, Map<String, Object> params2, String[] areas,
						  boolean stopOnFirstFailure) {

		TestKitSearchPath searchPath = session.getTestkitSearchPath();
		try {
			session.xt = new Xdstest2(Installation.instance().toolkitxFile(), searchPath, session);
		} catch (Exception e) {
			Result result = new Result();
			result.addAssertion(e.getMessage(), false);
			return result;
		}
		return new UtilityRunner(this, TestRunType.UTILITY).run(session, params, params2, sections, testInstance, areas,
				stopOnFirstFailure);
	}

	/**
	 * Run a testplan(s) as a utility within a session.  This is different from
	 * run in that run stores logs in the external_cache and
	 * this call stores the logs within the session so they go away at the
	 * end of the end of the session.  Hence the label 'utility'.
	 * @param params
	 * @param sections
	 * @param testInstance
	 * @param stopOnFirstFailure
	 * @return
	 */
//	public Result runUtilityTest(Map<String, String> params, Map<String, Object> params2, List<String> sections,
//								 String testId, String[] areas, boolean stopOnFirstFailure) {
//
//		return utilityRunner.run(session, params, params2, sections, testId, areas, stopOnFirstFailure);
//	}
	public List<Result> runMesaTest(String environmentName,String mesaTestSessionName, SiteSpec siteSpec, TestInstance testInstance, List<String> sections,
									Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) throws Exception {
        if (session.getMesaSessionName() == null) session.setMesaSessionName(mesaTestSessionName);
        session.setCurrentEnvName(environmentName);
		TestKitSearchPath searchPath = new TestKitSearchPath(environmentName, mesaTestSessionName);
		session.xt = new Xdstest2(Installation.instance().toolkitxFile(), searchPath, session);
		return new TestRunner(this).run(session, mesaTestSessionName, siteSpec, testInstance, sections, params, params2, stopOnFirstFailure);
	}

	public TestOverviewDTO runTest(String environmentName, String mesaTestSession, SiteSpec siteSpec, TestInstance testInstance, List<String> sections,
									Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) throws Exception {
		TestKitSearchPath searchPath = new TestKitSearchPath(environmentName, mesaTestSession);
		session.xt = new Xdstest2(Installation.instance().toolkitxFile(), searchPath, session);
		new TestRunner(this).run(session, mesaTestSession, siteSpec, testInstance, sections, params, params2, stopOnFirstFailure);
		return getTestOverview(mesaTestSession, testInstance);
	}

	static public List<Result> runTestplan(String environment, String sessionName, SiteSpec siteSpec, TestInstance testId, List<String> sections, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, boolean persistResult) {

		List<Result> results; // This wrapper does two important things of interest: 1) Set patient id if it is available in the Params map and 2) Eventually calls the UtilityRunner
		try {
			results = xdsTestServiceManager.runMesaTest(environment, sessionName, siteSpec, testId, sections, params, null, stopOnFirstError);
		} catch (Exception e) {
			results = new ArrayList<>();
			Result result = new Result();
			result.assertions.add(ExceptionUtil.exception_details(e), false);
			results.add(result);
			return results;
		}

		// Save results to external_cache.
		// Supports getTestResults tookit api call
		if (persistResult) {
			ResultPersistence rPer = new ResultPersistence();

			for (Result result : results) {
				try {
					rPer.write(result, sessionName);
				} catch (Exception e) {
					result.assertions.add(ExceptionUtil.exception_details(e), false);
				}
			}
		}

		return results;
	}

	public void setGazelleTruststore() {
		String tsSysProp =
				System.getProperty("javax.net.ssl.trustStore");

		if (tsSysProp == null) {
			String tsFileName = "/gazelle/gazelle_sts_cert_truststore.jks";
			URL tsURL = getClass().getResource(tsFileName); // Should this be a toolkit system property variable?
			if (tsURL != null) {
				File tsFile = new File(tsURL.getFile());
				System.setProperty("javax.net.ssl.trustStore", tsFile.toString());
				System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
				System.setProperty("javax.net.ssl.trustStoreType", "JKS");
			} else {
				throw new ToolkitRuntimeException("Cannot find truststore by URL: " + tsURL);
			}

		}
	}

	public List<Result> querySts(String siteName, String sessionName, String query, Map<String, String> params, boolean persistResult) {
		setGazelleTruststore();

		String environmentName = "default";
		Session mySession = new Session(Installation.instance().warHome(), sessionName);
		mySession.setEnvironment(environmentName);

		// This must exist in the EC Dir. ex. GazelleSts
		SiteSpec stsSpec =  new SiteSpec(siteName);
		if (mySession.getMesaSessionName() == null)
			mySession.setMesaSessionName(sessionName);
		mySession.setSiteSpec(stsSpec);
		mySession.setTls(true); // Required for Gazelle

		TestInstance testInstance = new TestInstance("GazelleSts");

		List<String> sections = new ArrayList<String>();
		sections.add(query);

		XdsTestServiceManager xtsm = new XdsTestServiceManager(mySession);
		List<Result> results =  runTestplan(environmentName,sessionName,stsSpec,testInstance,sections,params,true,mySession,xtsm, persistResult);

		return results;
	}

	/**
	 * Original Xdstools2 function to retrieve test results based on the current Session by providing a list of
	 * TestInstance numbers / Test Ids.
	 * @param testInstances
	 * @param testSession
	 * @return
	 */
	public Map<String, Result> getTestResults(List<TestInstance> testInstances, String testSession) {
		if (session != null)
			logger.debug(session.id() + ": " + "getTestResults() ids=" + testInstances + " testSession=" + testSession);

		Map<String, Result> map = new HashMap<String, Result>();

		ResultPersistence rp = new ResultPersistence();

		for (TestInstance testInstance : testInstances) {
			try {
				Result result = rp.read(testInstance, testSession);
				map.put(testInstance.getId(), result);
			}
			catch (Exception e) {}
		}
		return map;
	}

	public void delTestResults(List<TestInstance> testInstances, String testSession) {
		if (session != null)
			logger.debug(session.id() + ": " + "delTestResults() ids=" + testInstances + " testSession=" + testSession);
		ResultPersistence rp = new ResultPersistence();
		for (TestInstance testInstance : testInstances) {
			try {
				rp.delete(testInstance, testSession);
			}
			catch (Exception e) {}
		}

	}

	// this translates from the xds-common version of AssertionResults
	// to the xdstools2.client version which is serializable and can be passed
	// to the gui front end
	void scanLogs(Xdstest2 xt, AssertionResults res, Collection<String> sections) throws Exception {

		gov.nist.toolkit.testengine.errormgr.AssertionResults car = xt.scanLogs(sections);

		for (gov.nist.toolkit.testengine.errormgr.AssertionResult cara : car.assertions) {
			res.add(cara.assertion, cara.info, cara.status);
		}
	}

	/**
	 * Fetch the log for a particular testId. The testId comes from a Result class
	 * instance. It is an identifier that is generated when the result is
	 * created so that the log details can be cached in the server and the
	 * client can ask for them later.
	 *
	 * This call only works for test logs created as part of the session
	 * that correspond to utilities based on the test engine. Raw
	 * test engine output cannot be accessed this way as they are stored
	 * separate from the current GUI session.
	 */
	public TestLogs getRawLogs(TestInstance testInstance) {
		if (session != null)
			logger.debug(session.id() + ": " + "getRawLogs for " + testInstance.describe());

		LogMapDTO logMapDTO;
		try {
			logMapDTO = LogRepository.logIn(testInstance);
		} catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e, "Logs not available for " + testInstance));
			TestLogs testLogs = new TestLogs();
			testLogs.testInstance = testInstance;
			testLogs.assertionResult = new AssertionResult(
					String.format("Internal Server Error: Cannot find logs for Test %s", testInstance),
					false);
			return testLogs;
		}

		try {
			TestLogs testLogs = TestLogsBuilder.build(logMapDTO);
			testLogs.testInstance = testInstance;
			return testLogs;
		} catch (Exception e) {
			TestLogs testLogs = new TestLogs();
			testLogs.assertionResult = new AssertionResult(
					ExceptionUtil.exception_details(e), false);
			return testLogs;
		}
	}

	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception  {
		if (session != null)
			logger.debug(session.id() + ": " + "getCollection " + collectionSetName + ":" + collectionName);
		try {
            System.out.println("ENVIRONMENT: "+session.getCurrentEnvName()+", SESSION: "+session.getMesaSessionName());
            Map<String,String> collection=new HashMap<String,String>();
            for (File testkitFile:Installation.instance().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName())){
                TestKit tk=new TestKit(testkitFile);
                Map<String,String> c=tk.getCollection(collectionSetName,collectionName);
                for (String key:c.keySet()) {
                    if (!collection.containsKey(key)) {
                        collection.put(key,c.get(key));
                    }
                }
            }
            return collection;
		} catch (Exception e) {
			logger.error("getCollection", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * For test collections like collections and actorcollections, return the test ids.
	 * @param collectionSetName - collections or actorcollections
	 * @param collectionName - name of specific collection
	 * @return
	 * @throws Exception
	 */
	public List<String> getCollectionMembers(String collectionSetName, String collectionName) throws Exception {
		if (session != null)
			logger.debug(session.id() + ": " + "getCollectionMembers " + collectionSetName + ":" + collectionName);
		TestKitSearchPath searchPath = session.getTestkitSearchPath();
		Collection<String> collec =  searchPath.getCollectionMembers(collectionSetName, collectionName);
		return new ArrayList<String>(collec);
	}

	public String getTestReadme(String test) throws Exception {
		logger.debug(session.id() + ": " + "getTestReadme " + test);
		try {
			TestDefinition tt = session.getTestkitSearchPath().getTestDefinition(test);
			return tt.getFullTestReadme();
		} catch (Exception e) {
			logger.error("getTestReadme", e);
			throw new Exception(e.getMessage());
		}
	}

	public List<String> getTestSections(String test) throws Exception   {
		if (session != null)
			logger.debug(session.id() + ": " + "getTestSectionsReferencedInUseReports " + test);
		TestKitSearchPath searchPath = session.getTestkitSearchPath();
		TestDefinition def = session.getTestkitSearchPath().getTestDefinition(test);
		return def.getSectionIndex();
	}

	/**
	 * Collect ID and title for each test collection
	 * @param collectionSetName
	 * @return List<TestCollectionDefinitionDAO>
	 * @throws Exception if cannot scan testkit
	 */
	public List<TestCollectionDefinitionDAO> getTestCollections(String collectionSetName) throws Exception {
		List<TestCollectionDefinitionDAO> daos = new ArrayList<>();
		TestKitSearchPath searchPath = session.getTestkitSearchPath();
		for (TestKit testkit : searchPath.getTestkits()) {
			for (TestCollectionDefinitionDAO dao : testkit.getTestCollections(collectionSetName)) {
				if (!hasTestCollection(daos, dao.getCollectionID()))
					daos.add(dao);
			}
		}
		return daos;
	}

	private boolean hasTestCollection(List<TestCollectionDefinitionDAO> daos, String collectionName) {
		for (TestCollectionDefinitionDAO dao : daos) {
			if (dao.getCollectionID().equals(collectionName))
				return true;
		}
		return false;
	}

	public boolean isPrivateMesaTesting() {
		if (session != null)
			logger.debug(session.id() + ": " + "isPrivateMesaTesting");
		return Installation.instance().propertyServiceManager().isTestLogCachePrivate();
	}

	/**
	 *
	 * @param testInstance
	 * @param section
	 * @return
	 * @throws Exception
     */
	public String getTestplanAsText(TestInstance testInstance, String section) throws Exception {
		TestKitSearchPath searchPath = session.getTestkitSearchPath();
		TestDefinition testDefinition = searchPath.getTestDefinition(testInstance.getId());
		return testDefinition.getTestPlanText(section);
	}

	/**
	 *
	 * @param testInstance
	 * @param section
	 * @return
	 * @throws Exception
     */
	public TestPartFileDTO getTestplanDTO(TestInstance testInstance, String section) throws Exception {
		try {
			if (session != null)
				logger.debug(session.id() + ": " + "getTestplanAsText");

			File tsFile;

			try {
				TestDefinition testDefinition = session.getTestkitSearchPath().getTestDefinition(testInstance.getId());
				tsFile = testDefinition.getTestplanFile(section);
			} catch (Exception e) {
				throw new Exception("Cannot load test plan " + testInstance + "#" + section);
			}
			TestPartFileDTO testplanDTO = new TestPartFileDTO(TestPartFileDTO.TestPartFileType.SECTION_TESTPLAN_FILE);
			String content = new OMFormatter(tsFile).toString();
			testplanDTO.setPartName(section);
			testplanDTO.setFile(tsFile.toString());
			testplanDTO.setContent(content);
			testplanDTO.setHtlmizedContent(XmlFormatter.htmlize(content));
			return testplanDTO;
		} catch (Throwable t) {
			throw new Exception(t.getMessage() + "\n" + ExceptionUtil.exception_details(t));
		}
	}

	public TestPartFileDTO popStepMetadataFile(TestPartFileDTO sectionTpf) throws Exception{
		String testplanFileString = sectionTpf.getFile();
		File testplanFile = new File(testplanFileString);
		String testplanXmlString = sectionTpf.getContent();

		try {
			OMElement testplanEle = Parse.parse_xml_string(testplanXmlString);
			List<OMElement> testSteps = XmlUtil.decendentsWithLocalName(testplanEle, "TestStep");
			for (OMElement testStep : testSteps) {
				String stepName = testStep.getAttributeValue(MetadataSupport.id_qname);
				OMElement metadataFileEle = XmlUtil.firstDecendentWithLocalName(testStep, "MetadataFile");
				if (metadataFileEle == null) continue;
				String metadataFileName = metadataFileEle.getText();
				if (metadataFileName == null || metadataFileName.equals("")) continue;
				File metadataFile = new File(testplanFile.getParent(), metadataFileName);
				if (!(metadataFile.exists())) continue;
				TestPartFileDTO stepTpf = new TestPartFileDTO(TestPartFileDTO.TestPartFileType.STEP_METADATA_FILE);
				stepTpf.setPartName(stepName);
				stepTpf.setFile(metadataFile.toString());
				sectionTpf.getStepList().add(stepName);
				sectionTpf.getStepTpfMap().put(stepName,stepTpf);
			}
		} catch (Throwable t) {
            throw new Exception("Error traversing metadataFile elements:" + t.toString() + " testplan file: " + testplanFileString + " xmlString: " + testplanXmlString);
		}
		return sectionTpf;
	}

	public TestPartFileDTO getSectionTestPartFile(TestInstance testInstance, String section) throws Exception {

		TestPartFileDTO sectionTpf = getTestplanDTO(testInstance, section);

		if (sectionTpf!=null) {
			popStepMetadataFile(sectionTpf);

			// See if this section has a ContentBundle
			File contentBundle = new File(new File(sectionTpf.getFile()).getParentFile(),"ContentBundle");
			if (contentBundle.exists() && contentBundle.isDirectory()) {
				List<TestPartFileDTO> cbSections = new ArrayList<>();
				List<String> contentBundleSections = getTestSections(contentBundle.toString());
				if (contentBundleSections.size()>0) {
					for (String cbSectionName : contentBundleSections) {
						TestPartFileDTO cbSection = getTestplanDTO(testInstance, section + File.separator + "ContentBundle" + File.separator + cbSectionName);
						popStepMetadataFile(cbSection);
						cbSections.add(cbSection);
					}
					sectionTpf.setContentBundle(cbSections);
				}

			}
		}
		return sectionTpf;
	}

	public static TestPartFileDTO loadTestPartContent(TestPartFileDTO testPartFileDTO) throws Exception {
		File f = new File(testPartFileDTO.getFile());
		if (f.exists()) {
			String content = new OMFormatter(f).toString();
			testPartFileDTO.setContent(content);
			testPartFileDTO.setHtlmizedContent(XmlFormatter.htmlize(content));
		}
		return testPartFileDTO;
	}

	public List<TestInstance> getTestlogListing(String sessionName) throws Exception {
		if (session != null)
			logger.debug(session.id() + ": " + "getTestlogListing(" + sessionName + ")");

		List<String> sessionNames = getMesaTestSessionNames();

		if (!sessionNames.contains(sessionName))
			throw new Exception("Don't understand session name " + sessionName);

		File sessionDir = new File(Installation.instance().propertyServiceManager().getTestLogCache() +
				File.separator + sessionName);

		List<String> names = new ArrayList<>();

		for (File test : sessionDir.listFiles()) {
			if (!test.isDirectory() || test.getName().equals("Results"))
				continue;
			names.add(test.getName());
		}

		List<TestInstance> testInstances = new ArrayList<TestInstance>();
		for (String name : names) testInstances.add(new TestInstance(name));

		return testInstances;
	}

	public List<TestOverviewDTO> getTestsOverview(String sessionName, List<TestInstance> testInstances) throws Exception {
		List<TestOverviewDTO> results = new ArrayList<>();
		try {
			for (TestInstance testInstance : testInstances) {
				results.add(getTestOverview(sessionName, testInstance));
			}
		} catch (Exception e) {
			throw e;
		}
		return results;
	}

	/**
	 * Return the contents of all the log.xml files found under external_cache/TestLogCache/&lt;sessionName&gt;.  If there
	 * are multiple sections to the test then load them all. Each element of the
	 * returned list (Result model) represents the output of all steps in a single section of the test.
	 * @param sessionName - not the servlet session but instead the dir name
	 * under external_cache/TestLogCache identifying the user of the service
	 * @param testInstance like 12355
	 * @return
	 * @throws Exception
	 */
	public TestOverviewDTO getTestOverview(String sessionName, TestInstance testInstance) throws Exception {
		try {
			if (session != null)
				logger.debug(session.id() + ": " + "getTestOverview(" + testInstance + ")");

			testInstance.setUser(sessionName);

			File testDir = getTestLogCache().getTestDir(sessionName, testInstance);

			LogMapDTO lm = null;
			if (testDir != null)
				lm = buildLogMap(testDir, testInstance);

			TestLogDetails testLogDetails = new TestLogDetails(session.getTestkitSearchPath().getTestDefinition(testInstance.getId()), testInstance);
			List<TestLogDetails> testLogDetailsList = new ArrayList<TestLogDetails>();
			testLogDetailsList.add(testLogDetails);

			if (testDir != null) {
				for (String section : lm.getLogFileContentMap().keySet()) {
					LogFileContentDTO ll = lm.getLogFileContentMap().get(section);
					testLogDetails.addTestPlanLog(section, ll);
				}

				// Save the created logs in the SessionCache (or testLogCache if this is a conformance test)
				TestInstance logid = newTestLogId();

				//  -  why is a method named getTestOverview doing a WRITE???????
				if (session.transactionSettings.logRepository != null)
					session.transactionSettings.logRepository.logOut(logid, lm);
			}

			TestOverviewDTO dto = new TestOverviewBuilder(session, testLogDetails).build();
			if (testDir == null)
				dto.setRun(false);
			else
				dto.setLogMapDTO(lm);
			return dto;
		} catch (Exception e) {
			if (e.getMessage() != null && !e.getMessage().equals("")) throw e;
			throw new Exception(ExceptionUtil.exception_details(e));
		}
	}

	// testInstance.user must be set or null will be returned
	private File getTestLogDir(TestInstance testInstance) throws IOException {
		return getTestLogCache().getTestDir(testInstance);
	}

	public LogFileContentDTO getTestLogDetails(String sessionName, TestInstance testInstance) throws Exception {
		try {
			if (session != null)
				logger.debug(session.id() + ": " + "getTestOverview(" + testInstance + ")");

			testInstance.setUser(sessionName);

			File testDir = getTestLogCache().getTestDir(sessionName, testInstance);

			String sectionName = testInstance.getSection();
			File logFile;

			if (sectionName == null) {
				logFile = new File(testDir, "log.xml");
			} else {
				logFile = new File(new File(testDir, sectionName), "log.xml");
			}

			return new LogFileContentBuilder().build(logFile);
		} catch (Exception e) {
			if (e.getMessage() != null) throw e;
			throw new Exception(ExceptionUtil.exception_details(e));
		}

	}

	private LogMapDTO buildLogMap(File testDir, TestInstance testInstance) throws Exception {
		LogMapDTO lm = new LogMapDTO();

		TestDefinition testDefinition = session.getTestkitSearchPath().getTestDefinition(testInstance.getId());
		if (testDefinition == null)
			return lm;
		List<String> sectionNames = testDefinition.getSectionIndex();

		if (sectionNames.size() == 0) {   // now illegal
			File[] files = testDir.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isFile() && f.getName().equals("log.xml")) {
						LogFileContentDTO ll = new LogFileContentBuilder().build(f);
						lm.add(f.getName(), ll);
					} else if (f.isDirectory()) {
						File logfile = new File(f, "log.xml");
						if (logfile.exists()) {
							LogFileContentDTO ll = new LogFileContentBuilder().build(logfile);
							lm.add(f.getName(), ll);
						}
					}
				}
			}

		} else {
			for (String sectionName : sectionNames ) {
				File lfx = new File(testDir + File.separator + sectionName + File.separator + "log.xml");
				try {
					LogFileContentDTO ll = new LogFileContentBuilder().build(lfx);
					lm.add(sectionName, ll);
				} catch (Exception e)
				{
					continue;
				}
			}
		}

		return lm;
	}

	private List<File> testLogDirsInTestSession(String testSession) throws IOException {
		List<File> testLogDirs = new ArrayList<>();
		TestLogCache testLogCache = getTestLogCache();
		File sessionDir = testLogCache.getSessionDir(testSession);
		File[] files = sessionDir.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) continue;
			testLogDirs.add(file);
		}
		return testLogDirs;
	}

	private List<LogMapDTO> getLogsForTestSession(String testSession) throws Exception {
		List<LogMapDTO> logs = new ArrayList<>();

		for (File testLogDir : testLogDirsInTestSession(testSession)) {
			String testId = testLogDir.getName();
			LogMapDTO logMapDTO = buildLogMap(testLogDir, new TestInstance(testId));
			logs.add(logMapDTO);
		}

		return logs;
	}

	/**
	 * Validate testSession and site exist and that either the testSession is empty or it contains
	 * only test results for that site.
	 * @param testSession
	 * @param siteName
	 * @return status
	 */
	public ConformanceSessionValidationStatus validateConformanceSession(String testSession, String siteName) throws Exception {
		List<LogMapDTO> logMapDTOs = getLogsForTestSession(testSession);
		if (siteName == null || siteName.equals("")) return new ConformanceSessionValidationStatus();
		Set<String> badSites = new HashSet<>();
		for (LogMapDTO logMapDTO : logMapDTOs) {
			Map<String, LogFileContentDTO> map = logMapDTO.getLogFileContentMap();
			for (LogFileContentDTO logFileContentDTO : map.values()) {
				String site = logFileContentDTO.getSiteName();
				if (site == null || site.equals("")) continue;
				if (!site.equals(siteName)) {
					badSites.add(site);
				}
			}
		}
		if (badSites.size() == 0) return new ConformanceSessionValidationStatus();
		StringBuilder buf = new StringBuilder();
		buf.append("Test Session ").append(testSession).append(" already has results for these sites: ").append(badSites.toString() +
		" you cannot use it to test " + siteName);
		return new ConformanceSessionValidationStatus(false, buf.toString());
	}

	public Collection<String> getSitesForTestSession(String testSession) throws Exception {
		Set<String> sites = new HashSet<>();
		List<LogMapDTO> logMapDTOs = getLogsForTestSession(testSession);
		for (LogMapDTO logMapDTO : logMapDTOs) {
			Map<String, LogFileContentDTO> map = logMapDTO.getLogFileContentMap();
			for (LogFileContentDTO logFileContentDTO : map.values()) {
				String site = logFileContentDTO.getSiteName();
				if (site != null && !site.equals(""))
					sites.add(site);
			}
		}
		return sites;
	}

	public CodesResult getCodesConfiguration() {
		if (session != null)
			logger.debug(session.id() + ": " + "currentCodesConfiguration");

		CodesResult codesResult = new CodesResult();

		try {
			codesResult.codesConfiguration = codesConfiguration();
		} catch (Exception e) {
			codesResult.result = buildResult(e);
		} finally {
			if (codesResult.result == null)
				codesResult.result = buildResult();
		}
		return codesResult;
	}

	CodesConfiguration codesConfiguration() throws XdsInternalException,
			FactoryConfigurationError, EnvironmentNotSelectedException {
		if (codesConfiguration == null)
			codesConfiguration = new CodesConfigurationBuilder(
					//					new File(warHome + "/toolkitx/codes/codes.xml")
					session.getCodesFile()
			)
					.get();
		return codesConfiguration;
	}



	void cleanupParams(Map<String, String> params) {
		for (String key : params.keySet()) {
			if (params.get(key) == null)
				params.put(key, "");
		}
	}

	//	Result mkResult(Throwable t) {
	//		Result r = mkResult();
	//		r.addAssertion(ExceptionUtil.exception_details(t), false);
	//		return r;
	//	}

	//	Result mkResult() {
	//		Result r = new Result();
	//		Calendar calendar = Calendar.getInstance();
	//		r.timestamp = calendar.getTimestamp().toString();
	//
	//		return r;
	//	}

	//	Result mkResult(XdstestLogId id) {
	//		Result result = mkResult();
	//		result.testId = id;
	//		return result;
	//	}

	TestInstance newTestLogId() {
		return new TestInstance(UuidAllocator.allocate().replaceAll(":", "_"));
	}

	Result buildResult(List<TestLogDetails> testLogDetailses, TestInstance logId) throws Exception {
		TestInstance testInstance;
		if (testLogDetailses.size() == 1) {
			testInstance = testLogDetailses.get(0).getTestInstance();
		} else {
			testInstance = new TestInstance("Combined_Test");
		}
		Result result = ResultBuilder.RESULT(testInstance);
		result.logId = logId;
		//		Result result = mkResult(testId);

		//		// Also save the log file organized by sessionID, siteName, testNumber
		//		// This allows xdstest2 to use sessionID/siteNae dir as the LOGDIR
		//		// for referencing old log file. May also lead to downloadable log files
		//		// for Pre-Connectathon test results
		//
		//		SessionCache sc = new SessionCache(s, getTestLogCache());
		//		for (LogMapItemDTO item : lm.items) {
		//			sc.addLogFile(item.log);
		//
		//		}

		// load metadata results into Result
		//		List<TestSpec> testLogDetailses = s.xt.getTestSpecs();
		for (TestLogDetails testLogDetails : testLogDetailses) {
			for (String section : testLogDetails.sectionLogMapDTO.keySet()) {
				if (section.equals("THIS"))
					continue;
				LogFileContentDTO logFileContentDTO = testLogDetails.sectionLogMapDTO.get(section);
				for (int i = 0; i < logFileContentDTO.size(); i++) {
					StepResult stepResult = new StepResult();
					boolean stepPass = false;
					result.stepResults.add(stepResult);
					try {
						TestStepLogContentDTO testStepLogContentDTO = logFileContentDTO.getTestStepLog(i);
						stepResult.section = section;
						stepResult.stepName = testStepLogContentDTO.getId();
						stepResult.status = testStepLogContentDTO.getStatus();
						stepResult.setSoapFaults(testStepLogContentDTO.getSoapFaults());
						stepPass = stepResult.status;

						logger.info("test section " + section + " has status " + stepPass);

						// a transaction can have metadata in the request OR
						// the response
						// look in both places and save
						// If this is a retrieve then no metadata will be
						// found
						boolean inRequest = false;
						try {
							String input = testStepLogContentDTO.getInputMetadata();
							Metadata m = MetadataParser
									.parseNonSubmission(input);
							if (m.getAllObjects().size() > 0) {
								MetadataToMetadataCollectionParser mcp = new MetadataToMetadataCollectionParser(
										m, stepResult.stepName);
								stepResult.setMetadata(mcp.get());
								inRequest = true;
							}
						} catch (Exception e) {
						}

						boolean inResponse = false;
						if (inRequest == false) {
							try {
								String reslt = testStepLogContentDTO.getResult();
								Metadata m = MetadataParser
										.parseNonSubmission(reslt);
								MetadataToMetadataCollectionParser mcp = new MetadataToMetadataCollectionParser(
										m, stepResult.stepName);
								stepResult.setMetadata(mcp.get());
								inResponse = true;
							} catch (Exception e) {
							}
						}

						if (inRequest || inResponse)
							result.includesMetadata = true;

						// look for document contents
						if (stepPass) {
							String response = null;
							try {
								response = testStepLogContentDTO.getResult();  // throws exception on Direct messages (no response)
							} catch (Exception e) {

							}
							if (response != null) {
								OMElement rdsr = Util.parse_xml(response);
								if (!rdsr.getLocalName().equals(
										"RetrieveDocumentSetResponse"))
									rdsr = XmlUtil
											.firstDecendentWithLocalName(
													rdsr,
													"RetrieveDocumentSetResponse");
								if (rdsr != null) {

									// Issue 103: We need to propogate the response status since the interpretation of a StepResult of "Pass" to "Success" is not detailed enough with the additional status of PartialSuccess. This fixes the issue of RetrieveDocs tool, displaying a "Success" when it is actually a PartialSuccess.
									try {
										String rrStatusValue = XmlUtil.firstDecendentWithLocalName(rdsr, "RegistryResponse").getAttributeValue(new QName("status"));
										stepResult.setRegistryResponseStatus(rrStatusValue);
									} catch (Throwable t) {
										logger.error(t.toString());
									}

									RetrievedDocumentsModel rdm = new RetrieveResponseParser(rdsr).get();

									Map<String, RetrievedDocumentModel> resMap = rdm.getMap();
									for (String docUid : resMap.keySet()) {
										RetrievedDocumentModel ri = resMap.get(docUid);
										Document doc = new Document();
										doc.uid = ri.getDocUid();
										doc.repositoryUniqueId = ri
												.getRepUid();
										doc.newUid = ri.getNewDoc_uid();
										doc.newRepositoryUniqueId = ri.getNewRep_uid();
										doc.mimeType = ri.getContent_type();
										doc.homeCommunityId = ri.getHome();
										doc.cacheURL = getRepositoryCacheWebPrefix()
												+ doc.uid
												+ LogFileContentBuilder.getRepositoryCacheFileExtension(doc.mimeType);

										if (stepResult.documents == null)
											stepResult.documents = new ArrayList<Document>();
										stepResult.documents.add(doc);
									}
								}
							}
						}
					} catch (Exception e) {
						result.assertions.add(
								ExceptionUtil.exception_details(e), false);
					}
				}

			}
		}

		return result;

	}

	private String getRepositoryCacheWebPrefix() {
		String toolkitHost = session.getServerIP();
		// context.getInitParameter("toolkit-host").trim();
		String toolkitPort = session.getServerPort();
		// context.getInitParameter("toolkit-port").trim();
//		return "http://" + toolkitHost + ":" + toolkitPort
//				+ Session.servletContextName + "/DocumentCache/";
		return  "DocumentCache/";
	}

	public List<String> getMesaTestSessionNames() throws Exception  {
		if (session != null)
			logger.debug(session.id() + ": " + "getMesaTestSessionNames");
		List<String> names = new ArrayList<String>();
		File cache;
		try {
			cache = Installation.instance().propertyServiceManager().getTestLogCache();
		} catch (Exception e) {
			logger.error("getMesaTestSessionNames", e);
			throw new Exception(e.getMessage());
		}

		String[] namea = cache.list();

		for (int i=0; i<namea.length; i++) {
			File dir = new File(cache, namea[i]);
			if (!dir.isDirectory()) continue;
			if (!namea[i].startsWith("."))
				names.add(namea[i]);

		}

		if (names.size() == 0) {
			names.add("default");
			File def = new File(cache, "default");
			def.mkdirs();
		}

		logger.debug("testSession names are " + names);
		return names;
	}

	public boolean addMesaTestSession(String name) throws Exception  {
		File cache;
		try {
			cache = Installation.instance().propertyServiceManager().getTestLogCache();

			if (name == null || name.equals(""))
				throw new Exception("Cannot add test session with no name");
			if (name.contains("__"))
				throw new Exception("Cannot contain a double underscore (__)");
			if (name.contains(" "))
				throw new Exception("Cannot contain spaces");
			if (name.contains("\t"))
				throw new Exception("Cannot contain tabs");
		} catch (Exception e) {
			logger.error("addMesaTestSession", e);
			throw new Exception(e.getMessage());
		}
		File dir = new File(cache.toString() + File.separator + name);
		dir.mkdir();
		return true;
	}

	public boolean delMesaTestSession(String name) throws Exception  {
		File cache;
		try {
			cache = Installation.instance().propertyServiceManager().getTestLogCache();

			if (name == null || name.equals(""))
				throw new Exception("Cannot add test session with no name");
		} catch (Exception e) {
			logger.error("delMesaTestSession", e);
			throw new Exception(e.getMessage());
		}
		File dir = new File(cache.toString() + File.separator + name);
		Io.delete(dir);

		// also delete simulators owned by this test session

		SimDb.deleteSims(new SimDb().getSimIdsForUser(name));
		SimCache.clear();
		return true;
	}


	/******************************************************************
	 *
	 * Expose these methods to the ToolkitService
	 *
	 ******************************************************************/

	public void setMesaTestSession(String sessionName) {
		if (session != null)
			logger.debug(session.id() + ": " + "setMesaTestSession(" + sessionName + ")");
		session.setMesaSessionName(sessionName);
	}

	public List<String> getTestdataSetListing(String environmentName,String testSessionName,String testdataSetName) {
		logger.debug(session.id() + ": " + "getTestdataSetListing:" + testdataSetName);
		TestKitSearchPath searchPath = new TestKitSearchPath(environmentName, testSessionName);
		Collection<String> listing = searchPath.getTestdataSetListing(testdataSetName);
		return new ArrayList<String>(listing);
	}

//	public List<String> getTestdataRegistryTests() {
//		if (session != null)
//			logger.debug(session.id() + ": " + "getTestdataRegistryTests");
//		return getTestKit().getTestdataRegistryTests();
//	}
//
//	public List<String> getTestdataRepositoryTests() {
//		if (session != null)
//			logger.debug(session.id() + ": " + "getTestdataRepositoryTests");
//		return getTestKit().getTestdataRepositoryTests();
//	}
//
//	public List<String> getTestdataList(String listname) {
//		if (session != null)
//			logger.debug(session.id() + ": " + "getTestdataList");
//		return getTestKit().getTestdataSetListing(listname);
//	}

	public String getNewPatientId(String assigningAuthority) {
		if (session != null)
			logger.debug(session.id() + ": " + "getNewPatientId()");
		return session.allocateNewPid(assigningAuthority).asString();
	}

	public Map<String, String> getCollectionNames(String collectionSetName) throws Exception  {
		logger.debug(session.id() + ": " + "getCollectionNames(" + collectionSetName + ")");
		Map<String,String> collectionNames=new HashMap<String,String>();
		List<File> testkitsFiles=Installation.instance().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName());
		for (File testkitFile:testkitsFiles){
			TestKit tk=new TestKit(testkitFile);
			Map<String, String> tmpCollectionNames=tk.getCollectionNames(collectionSetName);
			for (String key:tmpCollectionNames.keySet()) {
				if (!collectionNames.containsKey(key)) {
					collectionNames.put(key, tmpCollectionNames.get(key));
				}
			}
		}
		return collectionNames;
	}

	public List<Result> sendPidToRegistry(SiteSpec site, Pid pid) {
		if (session != null)
			logger.debug(session.id() + ": " + "sendPidToRegistry(" + pid + ")");
		session.setSiteSpec(site);
		Map<String, String> params = new HashMap<>();
		params.put("$pid$", pid.asString());
		TestInstance testInstance = new TestInstance("PidFeed");
		return asList(new UtilityRunner(this, TestRunType.UTILITY).run(session, params, null, null, testInstance, null, true));
	}


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// Tests Overview Tab
	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	// TODO To complete

	/**
	 * A Test model includes a Test Id or Instance Number, a Short Description, a Time and a Status. This model is used
	 * for display purposes only. Build similar objects using the package Results, or replace Test with a similar
	 * existing model.
	 */
	public List<Test> reloadAllTestResults(String sessionName) throws Exception {
//		List<TestInstance> testList = null;
//		Map<String, Result> results = null;
//		List<Test> display = new ArrayList<Test>();
//
//		System.out.println("test session name: "+sessionName);
//
//		// ----- Retrieve list of test instance numbers -----
//		// TODO is there a case where sessionName might not be found in the system (bug?)
//		if (sessionName == null) {
//			logger.error("Could not retrieve the list of test instance numbers because the user session is null");
//			// TODO throw new TestRetrievalException
//		}
//		else { testList = getTestlogListing(sessionName); }
//
//		// ----- Retrieve test log results for each test instance -----
//		if (testList == null){
//			logger.error("Could not retrieve the log results");
//			// TODO throw new TestRetrievalException
//			}
//		else {
//			results = getTestResults(testList, sessionName);
//			String testId;
//			Result res;
//			List<StepResult> sectionList;
//			boolean hasSections = false;
//
//			System.out.println("building data for display");
//			// Use the set of Results to build the data for display
//			for (Map.Entry<String, Result> entry: results.entrySet()){
//				testId = entry.getKey();
//				res = entry.getValue();
//				sectionList = res.getStepResults();
//
//				// Check whether the test has sections
//				if (sectionList == null || (sectionList.size() == 0)) { hasSections = true; }
//
//				// TODO not sure what the test status is
//				display.add(new Test(10500, false, z"", "", res.getText(), res.getTimestamp(), "pass"));
//			}
//		}
//		return display;

		// Test data
		return Arrays.asList(
				new Test(10891, false, "10891", "10891", "test 1", "04:10 PM EST", "failed"),
				new Test(10891, true, "10891", "section a", "test 1", "04:10 PM EST", "failed"),
				new Test(10891, true, "10891", "section b", "test 1", "04:12 PM EST", "pass"),
				new Test(17685, false, "17685", "17685", "test 2", "04:10 PM EST", "not run"),
				new Test(17688, false, "17688", "17688", "test 3", "04:15 PM EST", "run with warnings")
		);
	}

	public List<Test> runAllTests(String sessionName, Site site){
		// Test data
		return Arrays.asList(
				new Test(10891, false, "10891", "10891", "re-run test 1", "04:10 PM EST", "pass"),
				new Test(10891, true, "10891a", "section a", "re-run test 1", "04:10 PM EST", "pass"),
				new Test(10891, true, "10891b", "section b", "re-run test 1", "04:12 PM EST", "pass"),
				new Test(17685, false, "17685", "17685", "re-run test 2", "04:10 PM EST", "failed")
		);
		//    public Test(int _id, boolean _isSection, String _idWithSection, String _name, String _description, String _timestamp, String _status){
	}

	public List<Test> deleteAllTestResults(String sessionName, Site site){
		// Test data
		return Arrays.asList(
				new Test(10891, false, "10891", "10891", "test 1", "--", "not run"),
				new Test(10891, true, "10891a", "section a", "test 1", "--", "not run"),
				new Test(10891, true, "10891b", "section b", "test 1", "--", "not run"),
				new Test(17685, false, "17685", "17685", "test 2", "--", "not run")
		);
	}

	public Test runSingleTest(String sessionName, Site site, int testId) {
		// Test data
		return new Test(testId, false, "test#", "test name", "returned result test", "05:23 PM EST", "failed");
	}

	public TestOverviewDTO deleteSingleTestResult(TestInstance testInstance) throws Exception {
		try {
			File dir = getTestLogDir(testInstance);
			if (dir != null)
				Io.delete(dir);
		} catch (Exception e) {
			// oh well
		}
		return getTestOverview(testInstance.getUser(), testInstance);

	}

	private static final String SITEFILE = "site.txt";

	public String getAssignedSiteForTestSession(String testSession) throws IOException {
		TestLogCache testLogCache = getTestLogCache();
		File testSessionDir = testLogCache.getSessionDir(testSession);
		if (!testSessionDir.exists() || !testSessionDir.isDirectory()) return null;
		try {
			return Io.stringFromFile(new File(testSessionDir, SITEFILE)).trim();
		} catch (IOException e) {
			// none assigned
			return null;
		}
	}

	public void setAssignedSiteForTestSession(String testSession, String siteName) throws IOException {
		TestLogCache testLogCache = getTestLogCache();
		File testSessionDir = testLogCache.getSessionDir(testSession);
		if (!testSessionDir.exists() || !testSessionDir.isDirectory())
			throw new IOException("Test Session " + testSession + " does not exist");
		if (siteName == null) {
			Io.delete(new File(testSessionDir, SITEFILE));
		} else {
			Io.stringToFile(new File(testSessionDir, SITEFILE), siteName);
		}
	}

	private void clearAssignedSiteForTestSession(String testSession) throws IOException {
		TestLogCache testLogCache = getTestLogCache();
		File testSessionDir = testLogCache.getSessionDir(testSession);
		if (!testSessionDir.exists() || !testSessionDir.isDirectory())
			return;
		Io.delete(new File(testSessionDir, SITEFILE));
	}

	public String clearTestSession(String testSession) throws IOException {
		TestLogCache testLogCache = getTestLogCache();
		File testSessionDir = testLogCache.getSessionDir(testSession);
		Io.deleteContents(testSessionDir);
		return null;
	}


}
