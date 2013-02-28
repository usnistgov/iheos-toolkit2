package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.direct.logger.MessageLog;
import gov.nist.direct.logger.UserLog;
import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.CodesResult;
import gov.nist.toolkit.results.client.MetadataToMetadataCollectionParser;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestLogs;
import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.session.server.CodesConfigurationBuilder;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.TestSessionNotSelectedException;
import gov.nist.toolkit.session.server.services.TestLogCache;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.LogMap;
import gov.nist.toolkit.testengine.ResultPersistence;
import gov.nist.toolkit.testengine.RetInfo;
import gov.nist.toolkit.testengine.RetrieveB;
import gov.nist.toolkit.testengine.TestLogsBuilder;
import gov.nist.toolkit.testengine.TransactionSettings;
import gov.nist.toolkit.testengine.Xdstest2;
import gov.nist.toolkit.testengine.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.testengine.logrepository.SessionRepository;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class XdsTestServiceManager extends CommonServiceManager {
	CodesConfiguration codesConfiguration = null;
	// always reference through getTestKit()
	TestKit testKit = null;
	Session session;

	static Logger logger = Logger.getLogger(XdsTestServiceManager.class);
	static boolean allCiphersEnabled = false;

	public XdsTestServiceManager(Session session)  {
		this.session = session;
	}

	TestLogCache getTestLogCache() throws IOException {
		return new TestLogCache(Installation.installation().propertyServiceManager().getTestLogCache());
	}

	/**
	 * Wrapper around runUtilityTest
	 * @param testName
	 * @param sections
	 * @param params
	 * @param areas
	 * @param stopOnFirstFailure
	 * @return
	 */
	public Result xdstest(String testName, List<String> sections,
			Map<String, String> params, Map<String, Object> params2, String[] areas,
			boolean stopOnFirstFailure) {

		return runUtilityTest(params, params2, sections, testName, areas,
				stopOnFirstFailure);

		//		if ("true".equals(Installation.installation().propertyServiceManager().getToolkitEnableAllCiphers()) && allCiphersEnabled == false) {
		//			Xdstest2.enableAllCiphers();
		//			allCiphersEnabled = true;
		//		}
		//		else if ("false".equals(Installation.installation().propertyServiceManager().getToolkitEnableAllCiphers()) && allCiphersEnabled == true) {
		//			Xdstest2.enableNormalCiphers();
		//			allCiphersEnabled = false;
		//		}
		//
		//		session.res = new AssertionResults();
		//
		//		cleanupParams(params);
		//
		//		Result result = null;
		//
		//		if (session.res == null)
		//			session.res = new AssertionResults();
		//		
		//		AssertionResults res = session.res;
		//		try {
		//			session.xt = getNewXt();
		//			// This sets the result.logId so it looks like a utility test (cached within the session).
		//			// This needs to be overwritten so the logs can later be loaded from the 
		//			// external_cache
		//			// It also saves the results within the SessionCache
		//			result = runUtilityTest(params, params2, sections, testName, areas,
		//					stopOnFirstFailure);
		//			result.testName = testName;
		//			return result;
		//		} catch (EnvironmentNotSelectedException e) {
		//			return Result.RESULT(testName, res, new AssertionResult("Must select Environment to use TLS", "", false), null);
		//		} catch (Exception e) {
		//			logger.error(ExceptionUtil.exception_details(e));
		//			return Result.RESULT(testName, res, null, e);
		//		} 

	}

	/**
	 * Run a testplan(s) as a utility within a session.  This is different from
	 * runMesaTest in that runMesaTest stores logs in the external_cache and 
	 * this call stores the logs within the session so they go away at the
	 * end of the end of the session.  Hence the label 'utility'.
	 * @param params
	 * @param sections
	 * @param testName
	 * @param areas
	 * @param stopOnFirstFailure
	 * @return
	 */
	public Result runUtilityTest(Map<String, String> params, Map<String, Object> params2, List<String> sections,
			String testName, String[] areas, boolean stopOnFirstFailure) {

		cleanupParams(params);

		try {
			if (session.transactionSettings == null)
				session.transactionSettings = new TransactionSettings();

			if (session.xt == null)
				session.xt = getNewXt();

			if (session.res == null) 
				session.res = new AssertionResults();

			if (session.transactionSettings.logRepository == null)
				session.transactionSettings.logRepository = new LogRepositoryFactory().
				getRepository(Installation.installation().sessionCache(), session.getId(), LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
			session.xt.setLogRepository(session.transactionSettings.logRepository);
			
			try {
				if (testName.startsWith("tc:")) {
					String collectionName = testName.split(":")[1];
					session.xt.setTestCollection(collectionName);
				} else {
					session.xt.setTest(testName, sections, areas);
				}

				if (SiteServiceManager.getSiteServiceManager().sites == null)
					SiteServiceManager.getSiteServiceManager().loadSites(session.getId());
			} catch (Exception e) {
				logger.error(ExceptionUtil.exception_details(e));
				session.res.add(ExceptionUtil.exception_details(e), false);
				return ResultBuilder.RESULT(testName, session.res, null, null);
			}
			try {
				Sites theSites = SiteServiceManager.getSiteServiceManager().getSites();
				theSites.add(new Sites(SimManager.getSites(session.getSimConfigs())));
				//				session.getSimBaseEndpoint();
				// Only for SOAP messages will siteSpec.name be filled in.  For Direct it is not expected
				if (session.siteSpec != null && session.siteSpec.name != null && !session.siteSpec.name.equals("")) {
					Site site = theSites.getSite(session.siteSpec.name);
					if (site == null)
						throw new Exception("Cannot find site " + session.siteSpec.name);
					session.xt.setSite(site);
					session.xt.setSites(theSites);
				} else if (session.repUid != null) {
					Site site = theSites.getSite("allRepositories");
					if (site == null)
						throw new Exception("Cannot find site 'allRepositories'");
					session.xt.setSite(site);
					session.xt.setSites(theSites);
				}
			} catch (Exception e) {
				logger.error(ExceptionUtil.exception_details(e));
				session.res.add(ExceptionUtil.exception_details(e), false);
				return ResultBuilder.RESULT(testName, session.res, null, null);
			}
			session.xt.setSecure(session.isTls()) ;

			if (session.isSoap) {
				if (session.isTls())
					session.res.add("Using TLS");
				else
					session.res.add("Not using TLS");

				session.xt.setWssec(session.isSaml());
				if (session.isSaml())
					session.res.add("Using SAML");
				else
					session.res.add("Not using SAML");


				if (session.siteSpec != null)
					session.res.add("Site: " + session.siteSpec.name);
			}

			session.res.add("Parameters:");
			for (String param : params.keySet()) {
				session.res.add("..." + param + ": " + params.get(param));
			}

			if (session.siteSpec != null)
				System.out.println("Site is " + session.siteSpec.name);

			try {
				session.res.add("Starting");
				// s.res.add("Log Cache: " + s.getLogCount() + " entries");
				session.transactionSettings.securityParams = session;
				session.xt.run(params, params2, stopOnFirstFailure, session.transactionSettings);

				session.res.add(session.transactionSettings.res);

				// Save the created logs in the SessionCache
				XdstestLogId logid = newTestLogId();
				session.transactionSettings.logRepository.logOut(logid, session.xt.getLogMap());

				Result result = buildResult(session.xt.getTestSpecs(), logid);
				if (result != null) {
					session.xt.involvesMetadata = result.includesMetadata;
				}
				scanLogs(session.xt, session.res, sections);
				session.res.add("Finished");
				result.assertions.add(session.res);
				return result;
			} catch (EnvironmentNotSelectedException e) {
				if (session.res == null)
					session.res = new AssertionResults();
				session.res.add("Must select Environment on Home page to use TLS", false);
				return ResultBuilder.RESULT(testName, session.res, null, null);
			} catch (Exception e) {
				logger.error(ExceptionUtil.exception_details(e));
				if (session.res == null)
					session.res = new AssertionResults();
				session.res.add(ExceptionUtil.exception_details(e), false);
				return ResultBuilder.RESULT(testName, session.res, null, null);
			}
		} catch (Throwable e) {
			logger.error(ExceptionUtil.exception_details(e));
			if (session.res == null)                 
				session.res = new AssertionResults();
			session.res.add(ExceptionUtil.exception_details(e), false);
			return ResultBuilder.RESULT(testName, session.res, null, null);
		}
	}

	public Map<String, Result> getTestResults(List<String> testIds, String testSession) {
		logger.debug(session.id() + ": " + "getTestResults() ids=" + testIds + " testSession=" + testSession );

		Map<String, Result> map = new HashMap<String, Result>();

		ResultPersistence rp = new ResultPersistence();

		for (String id : testIds) {
			try {
				Result result = rp.read(id, testSession);
				map.put(id, result);
			}
			catch (Exception e) {}
		}

		return map;
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
	 * Fetch the log for a particular logId. The logId comes from a Result class
	 * instance. It is an identifier that is generated when the result is
	 * created so that the log details can be cached in the server and the
	 * client can ask for them later.
	 * 
	 * This call only works for test logs created as part of the session 
	 * that correspond to utilities based on the test engine. Raw
	 * test engine output cannot be accessed this way as they are stored
	 * separate from the current GUI session.
	 */
	public TestLogs getRawLogs(XdstestLogId logId) {
		logger.debug(session.id() + ": " + "getRawLogs");
		TestLogs testLogs = new TestLogs();
		testLogs.logId = logId;

		try {
			LogMap logMap = findRawLogs(logId);
			if (logMap == null) {
				String msg = "Internal Server Error: Cannot find logs for id "
						+ logId;
				testLogs.assertionResult = new AssertionResult(msg, false);
				ExceptionUtil.here(msg);
				return testLogs;
			}

			testLogs = getRawLogs(logMap);
			testLogs.logId = logId;

		} catch (Exception e) {
			testLogs.assertionResult = new AssertionResult(
					ExceptionUtil.exception_details(e), false);
		}

		return testLogs;

	}

	LogMap findRawLogs(XdstestLogId logId) {
//		RawLogCache cache;

		// look in tomcat session first, then mesa session		

//		cache = new RawLogCache(session.getTomcatSessionCache());
		try {
			return session.transactionSettings.logRepository.logIn(logId);
//			return cache.getLog(logId);
		} catch (Exception e) {}

//		cache = new RawLogCache(session.getMesaSessionCache());
//		try {
//			return cache.getLog(logId);
//		} catch (Exception e) {}

		return null;

	}

	TestLogs getRawLogs(LogMap logMap) throws Exception {
		return TestLogsBuilder.build(logMap);

		//		for (LogMapItem item : logMap.items) {
		//			LogFileContent logFile = item.log;
		//			for (TestStepLogContent stepLog : logFile.getStepLogs()) {
		//				TestLog testLog = new TestLog();
		//				String stepName = stepLog.getName();
		//				if (testLogs.logs == null)
		//					testLogs.logs = new ArrayList<TestLog>();
		//				testLogs.logs.add(testLog);
		//
		//				testLog.stepName = stepName;
		//				testLog.endpoint = stepLog.getEndpoint();
		//				testLog.inHeader = stepLog.getInHeader();
		//				testLog.inputMetadata = stepLog.getInputMetadata();
		//				testLog.outHeader = stepLog.getOutHeader();
		//				testLog.result = stepLog.getResult();
		//				testLog.status = stepLog.getStatus();
		//				testLog.errors = listAsString(stepLog.getErrors());
		//
		//				testLog.log = stepLog.getRoot();
		//			}
		//		}

		//		return testLogs;
	}

	TestKit getTestKit() {
		if (testKit == null)
			testKit = new TestKit(session.getTestkitFile());
		return testKit;
	}

	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception  {
		logger.debug(session.id() + ": " + "getCollection " + collectionSetName + ":" + collectionName);
		try {
			return getTestKit().getCollection(collectionSetName, collectionName);
		} catch (Exception e) {
			logger.error("getCollection", e);
			throw new Exception(e.getMessage());
		}
	}

	public String getTestReadme(String test) throws Exception {
		logger.debug(session.id() + ": " + "getTestReadme " + test);
		try {
			TestDefinition tt = new TestDefinition(getTestKit().getTestDir(test));
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
	
	public List<Result> runMesaTest(String mesaTestSession, SiteSpec siteSpec, String testName, List<String> sections, 
			Map<String, String> params, Map<String, Object> params2, boolean stopOnFirstFailure) {
		logger.debug(session.id() + ": " + "runMesaTest" + " " + mesaTestSession + " " + testName + " " + sections + " " + siteSpec + " " + params + " " + stopOnFirstFailure);
		try {

			if (session.getEnvironment() == null)
				throw new EnvironmentNotSelectedException("");

			if ((mesaTestSession == null || mesaTestSession.equals("")))
				throw new TestSessionNotSelectedException("Must choose test session");
			session.setSiteSpec(siteSpec);
			
			if (session.transactionSettings.logRepository == null) {
				session.transactionSettings.logRepository = new LogRepositoryFactory().
						getRepository(Installation.installation().testLogFile(), mesaTestSession, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.SPECIFIC_ID, testName); 
				session.transactionSettings.writeLogs = true;
			}

			// this PatientId override is necessary because the patientid.txt file
			// is shared by all threads
			String pid = params.get("$patientid$");
			if (pid != null && !pid.equals("")) {
				session.transactionSettings.patientId = pid;
			}

			// This sets result.logId so it looks like a session-based utility usage
			// of the test engine.  Need to re-label it so the logs can later
			// be properly pulled from the external_cache.
			Result result = xdstest(testName, sections, params, params2, null, stopOnFirstFailure);
//			ResultSummary summary = new ResultSummary(result);
			ResultPersistence rPer = new ResultPersistence();
			
			// Save results to external_cache.  
			try {
				rPer.write(result, mesaTestSession);
			}
			catch (Exception e) {
				result.assertions.add(ExceptionUtil.exception_details(e), false);
			}

			return asList(result);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}

	public boolean isPrivateMesaTesting() {
		logger.debug(session.id() + ": " + "isPrivateMesaTesting");
		return Installation.installation().propertyServiceManager().isTestLogCachePrivate();
	}

	public String getTestplanAsText(String testname, String section) throws Exception {
		try {
		logger.debug(session.id() + ": " + "getTestplanAsText");
		List<String> sections = new ArrayList<String>();
		sections.add(section);

		Xdstest2 xt2 = getNewXt();
		xt2.setTest(testname, sections, null, false);
		TestDetails ts = xt2.getTestSpec(testname);

		File tsFile;

		try {
			tsFile = ts.getTestplanFile(section);
		} catch (Exception e) {
			if (section.indexOf("All") > -1) {
				// test may not contain sections
				try {
					tsFile = ts.getTestplanFile(null);
				} catch (Exception e1) {
					throw new Exception("Cannot load test plan " + testname + "#" + section);
				}
			} else
				throw new Exception("Cannot load test plan " + testname + "#" + section);
		}
		return new OMFormatter(tsFile).toString();
		} catch (Throwable t) {
			throw new Exception(t.getMessage() + "\n" + ExceptionUtil.exception_details(t));
		}
	}

	public List<String> getTestlogListing(String sessionName) throws Exception {
		logger.debug(session.id() + ": " + "getTestlogListing(" + sessionName + ")");

		List<String> names = getMesaTestSessionNames();

		if (!names.contains(sessionName))
			throw new Exception("Don't understand session name " + sessionName);

		File sessionDir = new File(Installation.installation().propertyServiceManager().getTestLogCache() + File.separator + sessionName);

		List<String> testNames = new ArrayList<String>();

		for (File area : sessionDir.listFiles()) {
			if (!area.isDirectory())
				continue;
			for (File test : area.listFiles()) {
				if (!test.isDirectory())
					continue;
				testNames.add(test.getName());
			}
		}

		return testNames;
	}

	/**
	 * Return the contents of all the log.xml files found under external_cache/TestLogCache/<sessionName>.  If there
	 * are multiple sections to the test then load them all. Each element of the
	 * returned list (Result object) represents the output of all steps in a single section of the test.
	 * @param sessionName - not the servlet session but instead the dir name
	 * under external_cache/TestLogCache identifying the user of the service
	 * @param testName like 12355
	 * @return
	 * @throws Exception
	 */
	public List<Result> getLogContent(String sessionName, String testName) throws Exception {
		logger.debug(session.id() + ": " + "getLogContent(" + testName + ")");

		File testDir = getTestLogCache().getTestDir(sessionName, testName);

		if (testDir == null) 
			throw new Exception("Cannot find log file for test " + testName);

		LogMap lm = buildLogMap(testDir, testName);

		// this has a slightly different structure than the testkit
		// so pass the parent dir so the tests dir can be navigated
		File testkitDir = getTestKit().getTestsDir();
		File testkitD = testkitDir.getParentFile();

		TestDetails testDetails = new TestDetails(testkitD, testName);
		List<TestDetails> testDetailsList = new ArrayList<TestDetails>();
		testDetailsList.add(testDetails);

		for (String section : lm.getLogFileContentMap().keySet()) {
			LogFileContent ll = lm.getLogFileContentMap().get(section);
			testDetails.addTestPlanLog(section, ll);
		}

		// Save the created logs in the SessionCache
		XdstestLogId logid = newTestLogId();
		session.transactionSettings.logRepository.logOut(logid, lm);
//		session.saveLogMapInSessionCache(lm, logid);

		Result result = buildResult(testDetailsList, logid);

		return asList(result);
	}
	
	
	/**
	 * 
	 * @param sessionName the username for the current session?
	 * @return
	 * @throws Exception
	 */
	public List<MessageLog> getDirectLogs(String sessionName) throws Exception {
		logger.debug(session.id() + ": " + "getLogContent()"); 
		ArrayList<MessageLog> logs = UserLog.readUserLogs(sessionName);

		return (List<MessageLog>)logs;
	}
	

	public LogMap buildLogMap(File testDir, String testName) throws Exception {
		LogMap lm = new LogMap();

		// this has a slightly different structure than the testkit
		// so pass the parent dir so the tests dir can be navigated
		File testkitDir = getTestKit().getTestsDir();
		File testkitD = testkitDir.getParentFile();

		TestDetails testDetails = new TestDetails(testkitD, testName);

		List<String> sectionNames = testDetails.getSectionsFromTestDef(new File(testkitDir + File.separator + testName));

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
		logger.debug(session.id() + ": " + "getCodesConfiguration");

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
	//		r.timestamp = calendar.getTime().toString();
	//
	//		return r;
	//	}

	//	Result mkResult(XdstestLogId id) {
	//		Result result = mkResult();
	//		result.logId = id;
	//		return result;
	//	}

	XdstestLogId newTestLogId() {
		return new XdstestLogId(UuidAllocator.allocate().replaceAll(":", "_"));
	}

	Result buildResult(List<TestDetails> testSpecs, XdstestLogId logId) throws Exception {
		String label = "";
		if (testSpecs.size() == 1) {
			label = testSpecs.get(0).getTestNum();
		} else {
			label = "Combined Test";
		}
		Result result = ResultBuilder.RESULT(label);
		result.logId = logId;
		//		Result result = mkResult(logId);

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
		if (testSpecs != null) {
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
										rdsr = MetadataSupport
										.firstDecendentWithLocalName(
												response,
												"RetrieveDocumentSetResponse");
									if (rdsr != null) {
										RetrieveB rb = new RetrieveB();
										Map<String, RetInfo> resMap = rb
												.parse_rep_response(response);
										for (String docUid : resMap.keySet()) {
											RetInfo ri = resMap.get(docUid);
											Document doc = new Document();
											doc.uid = ri.getDoc_uid();
											doc.repositoryUniqueId = ri
													.getRep_uid();
											doc.mimeType = ri.getContent_type();
											doc.homeCommunityId = ri.getHome();
											doc.cacheURL = getRepositoryCacheWebPrefix()
													+ doc.uid
													+ getRepositoryCacheFileExtension(doc.mimeType);

											if (stepResult.documents == null)
												stepResult.documents = new ArrayList<Document>();
											stepResult.documents.add(doc);

											File localFile = new File(
													Installation.installation().warHome() + File.separator + 
													"xdstools2" + File.separator + "DocumentCache" + File.separator
													+ doc.uid
													+ getRepositoryCacheFileExtension(doc.mimeType));

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
		}

		return result;

	}

	String getRepositoryCacheWebPrefix() {
		String toolkitHost = session.getServerIP();
		// context.getInitParameter("toolkit-host").trim();
		String toolkitPort = session.getServerPort();
		// context.getInitParameter("toolkit-port").trim();
		return "http://" + toolkitHost + ":" + toolkitPort
				+ "/xdstools2/xdstools2/DocumentCache/";
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

		return names;
	}

	public boolean addMesaTestSession(String name) throws Exception  {
		File cache;
		try {
			cache = Installation.installation().propertyServiceManager().getTestLogCache();

			if (name == null || name.equals(""))
				throw new Exception("Cannot add test session with no name");
		} catch (Exception e) {
			logger.error("addMesaTestSession", e);
			throw new Exception(e.getMessage());
		}
		File dir = new File(cache.toString() + File.separator + name);
		dir.mkdir();
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

	public List<String> getTestdataSetListing(String testdataSetName) {
		logger.debug(session.id() + ": " + "getTestdataSetListing:" + testdataSetName);
		return getTestKit().getTestdataSetListing(testdataSetName);
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
		logger.debug(session.id() + ": " + "getNewPatientId()");
		return session.allocateNewPid(assigningAuthority);
	}

	public Map<String, String> getCollectionNames(String collectionSetName) throws Exception  {
		logger.debug(session.id() + ": " + "getCollectionNames(" + collectionSetName + ")");
		return getTestKit().getCollectionNames(collectionSetName);
	}


}
