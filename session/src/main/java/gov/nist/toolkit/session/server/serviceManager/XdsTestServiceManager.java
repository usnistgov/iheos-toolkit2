package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.MetadataToMetadataCollectionParser;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.session.server.CodesConfigurationBuilder;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.services.TestLogCache;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.testengine.engine.RetrieveB;
import gov.nist.toolkit.testengine.engine.TestLogsBuilder;
import gov.nist.toolkit.testengine.engine.Xdstest2;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.LogMap;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class XdsTestServiceManager extends CommonService {
//	private final UtilityRunner utilityRunner = new UtilityRunner(this);
//	private final TestRunner testRunner = new TestRunner(this);
	CodesConfiguration codesConfiguration = null;
	// always reference through getTestKit()
	TestKit testKit = null;
	public Session session;

	static Logger logger = Logger.getLogger(XdsTestServiceManager.class);
	static boolean allCiphersEnabled = false;

	public XdsTestServiceManager(Session session)  {
        this.session = session;
        logger.info("XdsTestServiceManager: using session " + session.getId());
	}

	public static Logger getLogger() {
		return logger;
	}

	TestLogCache getTestLogCache() throws IOException {
		return new TestLogCache(Installation.installation().propertyServiceManager().getTestLogCache());
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
									Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) {
        if (session.getMesaSessionName() == null) session.setMesaSessionName(mesaTestSessionName);
        session.setCurrentEnvName(environmentName);
		return new TestRunner(this).run(session, mesaTestSessionName, siteSpec, testInstance, sections, params, params2, stopOnFirstFailure);
	}

	/**
	 * Original Xdstools2 function to retrieve test results based on the current Session by providing a list of
	 * TestInstance numbers / Test Ids.
	 * @param testInstances
	 * @param testSession
	 * @return
	 */
	public Map<String, Result> getTestResults(List<TestInstance> testInstances, String testSession) {
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
		logger.debug(session.id() + ": " + "getRawLogs for " + testInstance.describe());

        LogMap logMap;
        try {
            logMap = LogRepository.logIn(testInstance);
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
			TestLogs testLogs = TestLogsBuilder.build(logMap);
			testLogs.testInstance = testInstance;
            return testLogs;
		} catch (Exception e) {
            TestLogs testLogs = new TestLogs();
			testLogs.assertionResult = new AssertionResult(
					ExceptionUtil.exception_details(e), false);
            return testLogs;
		}
	}

	TestKit getTestKit() {
		if (testKit == null)
			testKit = new TestKit(Installation.installation().testkitFile());
		return testKit;
	}

	TestKit getTestKit(File testkit){
		return new TestKit(testkit);
	}

	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception  {
		logger.debug(session.id() + ": " + "getCollection " + collectionSetName + ":" + collectionName);
		try {
            System.out.println("ENVIRONMENT: "+session.getCurrentEnvName()+", SESSION: "+session.getMesaSessionName());
            Map<String,String> collection=new HashMap<String,String>();
            for (File testkitFile:Installation.installation().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName())){
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

	public String getTestReadme(String test) throws Exception {
		logger.debug(session.id() + ": " + "getTestReadme " + test);
		try {
            TestKit tk=new TestKit(Installation.installation().findTestkitFromTest(Installation.installation().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName()),test));
			TestDefinition tt = new TestDefinition(tk.getTestDir(test));
			return tt.getReadme();
		} catch (Exception e) {
			logger.error("getTestReadme", e);
			throw new Exception(e.getMessage());
		}
	}

	public List<String> getTestIndex(String test) throws Exception   {
		logger.debug(session.id() + ": " + "getTestIndex " + test);
		TestDefinition tt;
		try {
			tt = new TestDefinition(getTestKit().getTestDir(test));
		} catch (Exception e1) {
			logger.error("getTestIndex", e1);
			throw new Exception(e1.getMessage());
		}
		try {
			return tt.getSectionIndex();
		} catch (IOException e) {
			return null;
		}
	}

	public boolean isPrivateMesaTesting() {
		logger.debug(session.id() + ": " + "isPrivateMesaTesting");
		return Installation.installation().propertyServiceManager().isTestLogCachePrivate();
	}

	public String getTestplanAsText(TestInstance testInstance, String section) throws Exception {
		try {
			logger.debug(session.id() + ": " + "getTestplanAsText");
			TestDetails ts = getTestDetails(testInstance, section);

			File tsFile;

			try {
				tsFile = ts.getTestplanFile(section);
			} catch (Exception e) {
				if (section.indexOf("All") > -1) {
					// test may not contain sections
					try {
						tsFile = ts.getTestplanFile(null);
					} catch (Exception e1) {
						throw new Exception("Cannot load test plan " + testInstance + "#" + section);
					}
				} else
					throw new Exception("Cannot load test plan " + testInstance + "#" + section);
			}
			return new OMFormatter(tsFile).toString();
		} catch (Throwable t) {
			throw new Exception(t.getMessage() + "\n" + ExceptionUtil.exception_details(t));
		}
	}

	public TestDetails getTestDetails(TestInstance testInstance, String section) throws Exception {
		List<String> sections = new ArrayList<String>();
		sections.add(section);

		Xdstest2 xt2 = getNewXt();
		xt2.setTestkits(Installation.installation().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName()));
		xt2.addTest(testInstance, sections, null, false);
		return xt2.getTestSpec(testInstance);
	}

	public List<TestInstance> getTestlogListing(String sessionName) throws Exception {
		logger.debug(session.id() + ": " + "getTestlogListing(" + sessionName + ")");

		List<String> names = getMesaTestSessionNames();

		if (!names.contains(sessionName))
			throw new Exception("Don't understand session name " + sessionName);

		File sessionDir = new File(Installation.installation().propertyServiceManager().getTestLogCache() + File.separator + sessionName);

		List<TestInstance> testInstances = new ArrayList<TestInstance>();

		for (File area : sessionDir.listFiles()) {
			if (!area.isDirectory())
				continue;
			for (File test : area.listFiles()) {
				if (!test.isDirectory())
					continue;
				testInstances.add(new TestInstance(test.getName()));
			}
		}

		return testInstances;
	}

	/**
	 * Return the contents of all the log.xml files found under external_cache/TestLogCache/&lt;sessionName&gt;.  If there
	 * are multiple sections to the test then load them all. Each element of the
	 * returned list (Result object) represents the output of all steps in a single section of the test.
	 * @param sessionName - not the servlet session but instead the dir name
	 * under external_cache/TestLogCache identifying the user of the service
	 * @param testInstance like 12355
	 * @return
	 * @throws Exception
	 */
	public List<Result> getLogContent(String sessionName, TestInstance testInstance) throws Exception {
		logger.debug(session.id() + ": " + "getLogContent(" + testInstance + ")");

		File testDir = getTestLogCache().getTestDir(sessionName, testInstance);

		if (testDir == null)
			throw new Exception("Cannot find log file for test " + testInstance);

		LogMap lm = buildLogMap(testDir, testInstance);

		// this has a slightly different structure than the testkit
		// so pass the parent dir so the tests dir can be navigated
		File testkitDir = getTestKit().getTestsDir();
		File testkitD = testkitDir.getParentFile();

		TestDetails testDetails = new TestDetails(testkitD, testInstance);
		List<TestDetails> testDetailsList = new ArrayList<TestDetails>();
		testDetailsList.add(testDetails);

		for (String section : lm.getLogFileContentMap().keySet()) {
			LogFileContent ll = lm.getLogFileContentMap().get(section);
			testDetails.addTestPlanLog(section, ll);
		}

		// Save the created logs in the SessionCache (or testLogCache if this is a conformance test)
		TestInstance logid = newTestLogId();

        //  -  why is a method named getLogContent doing a WRITE???????
		session.transactionSettings.logRepository.logOut(logid, lm);
//		session.saveLogMapInSessionCache(lm, logid);

		Result result = buildResult(testDetailsList, logid);

		return asList(result);
	}

	public LogMap buildLogMap(File testDir, TestInstance testInstance) throws Exception {
		LogMap lm = new LogMap();

		// this has a slightly different structure than the testkit
		// so pass the parent dir so the tests dir can be navigated
		File testkitDir = getTestKit().getTestsDir();
		File testkitD = testkitDir.getParentFile();

		TestDetails testDetails = new TestDetails(testkitD, testInstance);

		List<String> sectionNames = testDetails.getSectionsFromTestDef(new File(testkitDir + File.separator + testInstance));

		if (sectionNames.size() == 0) {
			for (File f : testDir.listFiles()) {
				if (f.isFile() && f.getName().equals("log.xml")) {
					LogFileContent ll = new LogFileContent(f);
					lm.add(f.getName(), ll);
				}
			}

		} else {
			for (String sectionName : sectionNames ) {
				File lfx = new File(testDir + File.separator + sectionName + File.separator + "log.xml");
				LogFileContent ll = new LogFileContent(lfx);
				lm.add(sectionName, ll);
			}
		}

		return lm;
	}

	public CodesResult getCodesConfiguration() {
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



	Xdstest2 getNewXt() throws Exception {
		return new Xdstest2(session.getToolkitFile(), session);
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

	Result buildResult(List<TestDetails> testSpecs, TestInstance logId) throws Exception {
		TestInstance testInstance;
		if (testSpecs.size() == 1) {
			testInstance = testSpecs.get(0).getTestInstance();
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
		//		for (LogMapItem item : lm.items) {
		//			sc.addLogFile(item.log);
		//
		//		}

		// load metadata results into Result
		//		List<TestSpec> testSpecs = s.xt.getTestSpecs();
		for (TestDetails testSpec : testSpecs) {
			for (String section : testSpec.sectionLogMap.keySet()) {
				if (section.equals("THIS"))
					continue;
				LogFileContent testlog = testSpec.sectionLogMap.get(section);
				for (int i = 0; i < testlog.size(); i++) {
					StepResult stepResult = new StepResult();
					boolean stepPass = false;
					result.stepResults.add(stepResult);
					try {
						TestStepLogContent tsLog = testlog.getTestStepLog(i);
						stepResult.section = section;
						stepResult.stepName = tsLog.getName();
						stepResult.status = tsLog.getStatus();
						stepPass = stepResult.status;

                        logger.info("test section " + section + " has status " + stepPass);

						// a transaction can have metadata in the request OR
						// the response
						// look in both places and save
						// If this is a retrieve then no metadata will be
						// found
						boolean inRequest = false;
						try {
							OMElement input = tsLog.getRawInputMetadata();
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
								OMElement reslt = tsLog.getRawResult();
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
							OMElement response = null;
							try {
								response = tsLog.getRawResult();  // throws exception on Direct messages (no response)
							} catch (Exception e) {

							}
							if (response != null) {
								OMElement rdsr = response;
								if (!rdsr.getLocalName().equals(
										"RetrieveDocumentSetResponse"))
									rdsr = XmlUtil
											.firstDecendentWithLocalName(
                                                    response,
                                                    "RetrieveDocumentSetResponse");
								if (rdsr != null) {

									// Issue 103: We need to propogate the response status since the interpretation of a StepResult of "Pass" to "Success" is not detailed enough with the additional status of PartialSuccess. This fixes the issue of RetrieveDocs tool, displaying a "Success" when it is actually a PartialSuccess.
									try {
										String rrStatusValue = XmlUtil.firstDecendentWithLocalName(rdsr, "RegistryResponse").getAttributeValue(new QName("status"));
										stepResult.setRegistryResponseStatus(rrStatusValue);
									} catch (Throwable t) {
										logger.error(t.toString());
									}

									RetrieveB rb = new RetrieveB();
									Map<String, RetrievedDocumentModel> resMap = rb
											.parse_rep_response(response).getMap();
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
												+ getRepositoryCacheFileExtension(doc.mimeType);

										if (stepResult.documents == null)
											stepResult.documents = new ArrayList<Document>();
										stepResult.documents.add(doc);

										File localFile = new File(getRepositoryCache(), doc.uid.replace(":","") + getRepositoryCacheFileExtension(doc.mimeType));

//                                                new File(
//												Installation.installation().warHome() + File.separator +
//														"xdstools2" + File.separator + "DocumentCache" + File.separator
//														+ doc.uid
//														+ getRepositoryCacheFileExtension(doc.mimeType));

										Io.bytesToFile(localFile,
												ri.getContents());
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

	String getRepositoryCacheWebPrefix() {
		String toolkitHost = session.getServerIP();
		// context.getInitParameter("toolkit-host").trim();
		String toolkitPort = session.getServerPort();
		// context.getInitParameter("toolkit-port").trim();
//		return "http://" + toolkitHost + ":" + toolkitPort
//				+ Session.servletContextName + "/DocumentCache/";
		return  "DocumentCache/";
	}

    File getRepositoryCache() {
        File cache = new File(Installation.installation().warHome(), "DocumentCache");
        cache.mkdirs();
        return cache;
    }

	String getRepositoryCacheFileExtension(String mimetype) {
		if (mimetype == null)
			return "";
		else if (mimetype.equals("text/xml"))
			return ".xml";
		else if (mimetype.startsWith("text"))
			return ".txt";
		else if (mimetype.startsWith("application/pdf"))
			return ".pdf";
		return "";

	}


	public List<String> getMesaTestSessionNames() throws Exception  {
		logger.debug(session.id() + ": " + "getMesaTestSessionNames");
		List<String> names = new ArrayList<String>();
		File cache;
		try {
			cache = Installation.installation().propertyServiceManager().getTestLogCache();
		} catch (Exception e) {
			logger.error("getMesaTestSessionNames", e);
			throw new Exception(e.getMessage());
		}

		String[] namea = cache.list();

		for (int i=0; i<namea.length; i++) {
			if (!namea[i].startsWith("."))
				names.add(namea[i]);
		}

		logger.debug("testSession names are " + names);
		return names;
	}

	public boolean addMesaTestSession(String name) throws Exception  {
		File cache;
		try {
			cache = Installation.installation().propertyServiceManager().getTestLogCache();

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
			cache = Installation.installation().propertyServiceManager().getTestLogCache();

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
		logger.debug(session.id() + ": " + "setMesaTestSession(" + sessionName + ")");
		session.setMesaSessionName(sessionName);
	}

	public List<String> getTestdataSetListing(String environmentName,String testSessionName,String testdataSetName) {
		logger.debug(session.id() + ": " + "getTestdataSetListing:" + testdataSetName);
		Set<String> testdataSetListing = new HashSet<String>();
		for (File testkit:Installation.installation().testkitFiles(environmentName,testSessionName)) {
			testdataSetListing.addAll(getTestKit(testkit).getTestdataSetListing(testdataSetName));
		}
		testdataSetListing.addAll(getTestKit().getTestdataSetListing(testdataSetName));
		return new ArrayList<String>(testdataSetListing);
	}

	public List<String> getTestdataRegistryTests() {
		logger.debug(session.id() + ": " + "getTestdataRegistryTests");
		return getTestKit().getTestdataRegistryTests();
	}

	public List<String> getTestdataRepositoryTests() {
		logger.debug(session.id() + ": " + "getTestdataRepositoryTests");
		return getTestKit().getTestdataRepositoryTests();
	}

	public List<String> getTestdataList(String listname) {
		logger.debug(session.id() + ": " + "getTestdataList");
		return getTestKit().getTestdataSetListing(listname);
	}

	public String getNewPatientId(String assigningAuthority) {
		logger.debug(session.id() + ": " +
                "getNewPatientId()");
		return session.allocateNewPid(assigningAuthority).asString();
	}

	public Map<String, String> getCollectionNames(String collectionSetName) throws Exception  {
		logger.debug(session.id() + ": " + "getCollectionNames(" + collectionSetName + ")");
        Map<String,String> collectionNames=new HashMap<String,String>();
        List<File> testkitsFiles=Installation.installation().testkitFiles(session.getCurrentEnvName(),session.getMesaSessionName());
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
	 * A Test object includes a Test Id or Instance Number, a Short Description, a Time and a Status. This object is used
	 * for display purposes only. Build similar objects using the package Results, or replace Test with a similar
	 * existing object.
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

	/**
	 * Delete logs for a single test
	 * @param sessionName
	 * @param site
	 * @param testId
	 * @return
	 */
	public Test deleteSingleTestResult(String sessionName, Site site, int testId) {
		// Test data, status must be "NOT RUN"
		return new Test(testId, false, "test#", "test name", "test description", "10:20 PM EST", "not run");
	}


}
