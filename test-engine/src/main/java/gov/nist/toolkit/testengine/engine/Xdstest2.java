package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagementui.client.Site;
import gov.nist.toolkit.testengine.errormgr.AssertionResults;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;
import gov.nist.toolkit.testenginelogging.client.StepGoalsDTO;
import gov.nist.toolkit.testenginelogging.client.TestStepLogContentDTO;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKit;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/***
 * API for XDSTest test engine
 *
 * 
 */
public class Xdstest2 {

	private XdsTest xt;
	private LogRepository logRepository;
	private TestKitSearchPath searchPath;
	TestInstance testInstance;
	private Site site;
	private List<String> sections;
	private List<TestLogDetails> testLogDetails;
	private SecurityParams tki;
	public boolean involvesMetadata = false;   // affects logging
	static Logger logger = Logger.getLogger(Xdstest2.class);


	/**
	 * Connect to toolkit.
	 * @param toolkitDir
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public Xdstest2(File toolkitDir, TestKitSearchPath searchPath, SecurityParams tki) throws Exception {
		this.searchPath = searchPath;
		xt = new XdsTest(searchPath);
		xt.setToolkit(toolkitDir);
		xt.loadTestKitVersion();
		this.tki = tki;
		initSecurity(toolkitDir);
		foofoo();
	}
	
	
	void foofoo() {
		  TrustManager[] trustAllCerts = new TrustManager[] {
		            new X509TrustManager() {
		                @SuppressWarnings("unused")
						public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
		                @SuppressWarnings("unused")
						public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
		                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
						public void checkClientTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws java.security.cert.CertificateException {

						}
						public void checkServerTrusted(
								java.security.cert.X509Certificate[] chain,
								String authType)
								throws java.security.cert.CertificateException {

						}
		            }
		    };
		    HostnameVerifier hostVerify = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		            return true;
		        }
		    };

		    try {
		        SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier(hostVerify);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}

	static String cipherSuites = "SSL_RSA_WITH_3DES_EDE_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA";
	static String defaultCipherSuites = null;
	
	Properties loadProperties(File toolkitDir) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(toolkitDir + File.separator + "xdstest" + File.separator + "keystores" + File.separator + "keystore.properties")));
		return props;
	}

	void initSecurity(File toolkitDir) throws FileNotFoundException, IOException, EnvironmentNotSelectedException {
		System.setProperty("javax.net.debug", "ssl");
		enableNormalCiphers();
	}

	public static void enableAllCiphers() {
		// make sure default gets recorded
		if (defaultCipherSuites == null)
			defaultCipherSuites = System.setProperty("https.cipherSuites", cipherSuites);
		System.setProperty("https.cipherSuites", defaultCipherSuites);
	}

	public static void enableNormalCiphers() {
		if (defaultCipherSuites == null)
			defaultCipherSuites = System.setProperty("https.cipherSuites", cipherSuites);
		else
			System.setProperty("https.cipherSuites", cipherSuites);
	}

	/**
	 * LogDir location - logDir is a directory where the xdstest log files will be written.
	 * Each log file is named log.xml. The directory structure of logDir reflects that of the
	 * testkit.
	 * 
	 * @param logRepository
	 * @throws IOException 
	 */
	public void setLogRepository(LogRepository logRepository) throws IOException {
		this.logRepository = logRepository;
//		this.logRepository.logDir().mkdirs();
		xt.setLogRepository(logRepository);
	}

	/**
	 * Select test to be run. All steps of all SECTIONS of this test will be
	 * run. Overrides earlier calls to addTest* methods.
	 * 
	 * @param testInstance - corresponds to name of a directory of TESTKIT/area/testname
	 * where area comes from a default list and does not need to be specified.  All SECTIONS
	 * of the test are executed in the default order.
	 * @throws Exception - Thrown if testname does not exist in the testkit
	 */
	public void addTest(TestKit testKit, TestInstance testInstance) throws Exception {
		this.testInstance = testInstance;
		TestDefinition testDefinition = testKit.getTestDef(testInstance.getId());
		xt.addTestSpec(new TestLogDetails(testDefinition, testInstance));

	}

	/**
	 * Select test to be run. All steps of this section will be run. Overrides
	 * earlier calls to addTest* methods.
	 * 
	 * @param testInstance - corresponds to name of a directory of TESTKIT/area/testname
	 * @param sections - list of SECTIONS of the test to execute. The ordering in this list
	 * controls the order of execution.
	 * @param areas - controls which areas of the testkit should be searched
	 * @throws Exception - Thrown if testname does not exist in the testkit
	 */
	public void addTest(TestKit testKit, TestInstance testInstance, List<String> sections, String[] areas, boolean doLogCheck) throws Exception {
		this.testInstance = testInstance;
		this.sections = sections;
		TestDefinition testDefinition = testKit.getTestDef(testInstance.getId());
		logger.info("Running test from " + testDefinition.toString());
		TestLogDetails testLogDetails;
		File tk = testKit.getTestKitDir();
		if (areas == null)
			testLogDetails = new TestLogDetails(testDefinition, testInstance);
		else
			testLogDetails = new TestLogDetails(testDefinition, testInstance, areas);
		if (logRepository != null)
			testLogDetails.setLogRepository(logRepository);
		if (doLogCheck) {
			if (sections != null && sections.size() != 0)
				testLogDetails.selectSections(sections);
		}
		xt.addTestSpec(testLogDetails);
	}
	
	public void addTest(TestKit testKit, TestInstance testInstance, File testDir) throws Exception {
		this.testInstance = testInstance;
		TestDefinition testDefinition = testKit.getTestDef(testInstance.getId());
		TestLogDetails testLogDetails = new TestLogDetails(testDefinition, testInstance);
		if (logRepository != null)
			testLogDetails.setLogRepository(logRepository);
		xt.addTestSpec(testLogDetails);
	}

	public void addTest(TestKit testKit, TestInstance testInstance, List<String> sections, String[] areas) throws Exception {
		addTest(testKit, testInstance, sections, areas, true);
	}
	
	public TestLogDetails getTestSpec(TestKit testKit, TestInstance testInstance) throws Exception {
		TestDefinition testDefinition = testKit.getTestDef(testInstance.getId());
		return new TestLogDetails(testDefinition, testInstance);
	}

	/**
	 * Specify Site - description of system under test
	 * 
	 * @param site
	 */
	public void setSite(Site site) {
		xt.setSite(site);
		this.site = site;
	}

	/**
	 * Install site collection, usually from actors.xml
	 * This is needed when site is later specified by name.  Each site knows its own name.
	 * @param sites
	 */
	public void setSites(Sites sites) {
		xt.setSites(sites);
	}

	/**
	 * Control use of TLS. Default is insecure.
	 * 
	 * @param isSecure
	 */
	public void setSecure(boolean isSecure) {
		xt.setSecure(isSecure);
	}
	
	/**
	 * Control use of WSSecurity. Default is insecure.
	 * 
	 * @param isWssec
	 */
	public void setWssec(boolean isWssec) {
		xt.setWssec(isWssec);
	}

	/**
	 * Run previously selected test.
	 * 
	 * @param externalLinkage - name/value mapping of parameters. The names should match
	 * template variables in the testplan(s). The values are inserted before the testplan is 
	 * executed.
	 * @param stopOnFirstFailure - Each teststep results in a pass/fail status. This controls 
	 * how far the test script runs in the presence of errors.
	 * @param ts - several obscure xdstest settings are controlled from here. An example is whether a 
	 * Patient ID should be assigned from the settings in the XDS Test tool or whether the
	 * Patient ID as coded in the testplan should be used. 
	 * @throws Exception - Something went seriously wrong
	 */
	public boolean run(Map<String, String> externalLinkage, Map<String, Object> externalLinkage2,  boolean stopOnFirstFailure, TransactionSettings ts) throws Exception {
		xt.stopOnFirstFailure = stopOnFirstFailure;
		logger.debug("Running " + testInstance.getId());
		testLogDetails = xt.runAndReturnLogs(externalLinkage, externalLinkage2, ts, ts.writeLogs);
		if (testLogDetails == null)
			throw new Exception("Xdstest2#run: runAndReturnLogs return null (testSpecs)");
		return xt.status;
	}

	String formatGoal(String goal) {
		StringBuffer buf = new StringBuffer();

		buf.append("...");
		for (int i=0; i<goal.length(); i++) {
			if (goal.charAt(i) == ' ')
				buf.append(".");
			else {
				buf.append(goal.substring(i));
				break;
			}
		}

		return buf.toString();
	}

	/**
	 * Gather and return the assertion results from the test logs. Once a test has been run, 
	 * it is usually necessary to display the details of the assertionEleList that failed. This includes
	 * the receipt of SOAPFaults, teststeps that failed, and general error messages that appear
	 * in the testlog.
	 * @return - Organized collection of error messages
	 * @throws Exception - Something went seriously wrong
	 */
	public AssertionResults scanLogs(Collection<String> sectionsToScan) throws Exception {

		if (sectionsToScan != null && sectionsToScan.size() == 0) 
			sectionsToScan = null;

		if (testLogDetails.size() > 1)
			sectionsToScan = null;

		AssertionResults res = new AssertionResults();
		String dashes = "------------------------------------------------------------------------------------------------";

		res.add(dashes);
		for (TestLogDetails testSpec : testLogDetails) {
//            logger.info("Scanning Test: " + testSpec.getTestInstance());
			res.add("Test: " + testSpec.getTestInstance());
			res.add(dashes);
			Collection<String> sections;
			if (sectionsToScan == null)
				sections = testSpec.getTestPlanLogs().keySet();
			else
				sections = sectionsToScan;

			for (String section : sections) {
				if (section.equals("THIS"))
					continue;
				LogFileContentDTO testLog = testSpec.getTestPlanLogs().get(section);
				if (testLog == null) {
					// this section failed - report it and continue;
					res.add("Section: " + section);
					res.add("Section failed - no log available", false);
					continue;
				}
				if (testLog.hasFatalError()) {
					res.add("Section: " + testLog.getTestAttribute());
					res.add(testLog.getFatalError(), false);
					continue;
				}
				for (TestStepLogContentDTO stepLog : testLog.getStepLogs()) {
					res.add("Status: " + ((stepLog.isSuccess()) ? "Pass" : "Fail"), stepLog.isSuccess());

					res.add("Section: " + testLog.getTestAttribute() + " Step: " + stepLog.getId());
					res.add("Endpoint: " + stepLog.getEndpoint());

					StepGoalsDTO stepGoalsDTO = stepLog.getStepGoalsDTO();
					res.add("Goals:", true);
					for (String goal : stepGoalsDTO.goals) {
						res.add(formatGoal(goal), true);
					}

                    for (String detail : stepLog.getDetails()) {
                        res.add("Detail: " + detail);
                    }

                    List<String> faults = stepLog.getSoapFaults();
					for (String fault : faults) 
						res.add("SOAPFault: " + fault, false);
					if (!faults.isEmpty())
						continue;

					List<String> errors = stepLog.getErrors();
					for (String error : errors) {
						res.add(error, stepLog.isSuccess());
					}

					for (String error : stepLog.getAssertionErrors())
						res.add(error, false);


//                    for (String report : stepLog.getUseReports()) {
//                        res.add("UseReport: " + report);
//                    }
                    for (String report : stepLog.getReportsSummary()) {
                        res.add("ReportBuilder: " + report);
                    }
//					try {
//						Metadata m;
//						m = stepLog.getParsedInputMetadata();
//						if (m != null && m.hasMetadata())
//							res.add("Contents Sent:\n" + m.getContentsDescription());
//						m = stepLog.getMetadata();
//						if (m != null && m.hasMetadata())
//							res.add("Contents Returned:\n" + m.getContentsDescription());
//					} catch (Exception e) {
//
//					}

					res.add(dashes);
				}
			}
		}

		return res;
	}

	/**
	 * Return currently specified collection of test specifications. This is where detailed
	 * log information is stored when a test completes. It is initialized by the test execution.
	 * @return
	 */
	public List<TestLogDetails> getTestSpecs() {
		return testLogDetails;
	}

	/**
	 * Return LogMapDTO, the collection of information from the log.xml files generated by
	 * the running of one or more tests.
	 * @return LogMapDTO
	 * @throws Exception - No tests have been specified, no logs can be found, or the log files cannot
	 * be parsed.
	 */
	public LogMapDTO getLogMap() throws Exception {
		LogMapDTO lm = new LogMapDTO();

		if (testLogDetails == null)
			throw new Exception("Xdstest2#getLogMap: testSpecs is null");

		for (TestLogDetails testSpec : testLogDetails) {
			for (String section : testSpec.getTestPlanLogs().keySet()) {
				LogFileContentDTO testLog = testSpec.getTestPlanLogs().get(section);
				if (testLog == null) {
					if (section.equals("THIS"))
						continue;
					throw new Exception("Xdstest2#getLogMap: cannot find testlog for section " + section);
				}
				lm.add(testLog.getTestAttribute(), testLog);
			}
		}

		return lm;
	}

//	public void setTestkits(TestKitSearchPath searchPath) {
//		this.searchPath = searchPath;
//	}

	public TestKitSearchPath getTestkits() {
		return searchPath;
	}
}
