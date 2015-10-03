package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.errormgr.AssertionResults;
import gov.nist.toolkit.testengine.logrepository.LogRepository;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.StepGoals;
import gov.nist.toolkit.testenginelogging.TestDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
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

	XdsTest xt;
	LogRepository logRepository;
	File testkit;
	File altTestkit;
	String testnum;
	Site site;
	File toolkitDir;   // never referenced
	List<String> sections;
	List<TestDetails> testDetails;
	SecurityParams tki;
	public boolean involvesMetadata = false;   // affects logging
	static Logger logger = Logger.getLogger(Xdstest2.class);


	/**
	 * Connect to toolkit.
	 * @param toolkitDir
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public Xdstest2(File toolkitDir, SecurityParams tki) throws Exception {
		this.toolkitDir = toolkitDir;
		xt = new XdsTest();
		xt.setToolkit(toolkitDir);
		setTestkitLocation(new File(toolkitDir + File.separator + "testkit"));
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
							// TODO Auto-generated method stub
							
						}
						public void checkServerTrusted(
								java.security.cert.X509Certificate[] chain,
								String authType)
								throws java.security.cert.CertificateException {
							// TODO Auto-generated method stub
							
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
	 * Set Testkit location.
	 * Testkit is a file/directory based database of test descriptions. Each test description
	 * includes one or more testplans.  A testplan is a script that runs the test. Xdstest or 
	 * the test engine (same thing) is the language interpreter for the testplan language. Each
	 * test has a name.  These names frequently map to Kudu/Gazelle test numbers. The simplest
	 * test has a single testplan.  A testplan is a sequence of steps.  The steps are executed
	 * in order. A testplan is executed start to finish. There is no way to say - execute step 3.  
	 * Frequently, the early test steps initialize the SUT for later steps. More complicated tests 
	 * can have multiple sections with each section containing a testplan.  A test can be run in
	 * two ways: execute test x (run all sections of the test) or execute test x section y (where
	 * only the specified test section is run). Within the testkit, tests are segregated into
	 * areas. Current area names are: tests, examples, testdata. By default, the testplan 
	 * lookup function searches all areas for a given named test so these divisions are purely
	 * administrative.  Not all areas are distributed with the testkit. Some, like selftest, are
	 * used for extended regression testing of the Public Registry services. Note, not all 
	 * tests from the testkit are imported into the GUI tool which uses this interface.  The
	 * ant build.xml controls this.
	 * 
	 * @param locationDir
	 */
	public void setTestkitLocation(File locationDir) {
		testkit = locationDir;
		xt.setTestkit(locationDir);
	}

	public void setAlternateTestkitLocation(File dir) {
		altTestkit = dir;
		xt.setAltTestkit(dir);
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
		this.logRepository.logDir().mkdirs();
		xt.setLogRepository(logRepository);
	}

	/**
	 * Select test to be run. All steps of all sections of this test will be
	 * run. Overrides earlier calls to addTest* methods.
	 * 
	 * @param testname - corresponds to name of a directory of TESTKIT/area/testname
	 * where area comes from a default list and does not need to be specified.  All sections
	 * of the test are executed in the default order.
	 * @throws Exception - Thrown if testname does not exist in the testkit
	 */
	public void addTest(String testname) throws Exception {
		testnum = testname;
		xt.addTestSpec(new TestDetails(xt.getTestkit(), testname));

	}

	/**
	 * Select test to be run. All steps of this section will be run. Overrides
	 * earlier calls to addTest* methods.
	 * 
	 * @param testname - corresponds to name of a directory of TESTKIT/area/testname
	 * @param sections - list of sections of the test to execute. The ordering in this list
	 * controls the order of execution.
	 * @param areas - controls which areas of the testkit should be searched
	 * @throws Exception - Thrown if testname does not exist in the testkit
	 */
	public void addTest(String testname, List<String> sections, String[] areas, boolean doLogCheck) throws Exception {
		testnum = testname;
		this.sections = sections;
		TestDetails testDetails;
		if (areas == null)
			testDetails = new TestDetails(xt.getTestkit(), testname);
		else
			testDetails = new TestDetails(xt.getTestkit(), testname, areas);
		if (logRepository != null)
			testDetails.setLogDir(logRepository.logDir());
		if (doLogCheck) {
			if (sections != null && sections.size() != 0)
				testDetails.selectSections(sections);
		}
		xt.addTestSpec(testDetails);
	}
	
	public void addTest(String testName, File testDir) throws Exception {
		testnum = testName;
		TestDetails testDetails = new TestDetails(testDir);
		if (logRepository != null)
			testDetails.setLogDir(logRepository.logDir());
		xt.addTestSpec(testDetails);
	}

	public void addTest(String testname, List<String> sections, String[] areas) throws Exception {
		addTest(testname, sections, areas, true);
	}
	
	public TestDetails getTestSpec(String testname) throws Exception {
		return new TestDetails(xt.getTestkit(), testname);
	}

	/**
	 * Select test collection to be run. Overrides earlier calls to addTest*
	 * methods. A test collection is a list of tests which are executed in the
	 * order of the list.  This is useful for creating regression tests where you
	 * might collect all Stored Query tests to completely exercise the Stored Query 
	 * interface of a Document Registry.
	 * 
	 * @param collection - collection name.  Collections are stored in the collections subdirectory 
	 * of the testkit.
	 * @throws Exception - If the collection does not exist.
	 */
	public void addTestCollection(String collectionName) throws Exception {
		xt.addTestCollection(collectionName);
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
		logger.debug("Running " + testnum);
		testDetails = xt.runAndReturnLogs(externalLinkage, externalLinkage2, ts, ts.writeLogs);
		if (testDetails == null)
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
	 * it is usually necessary to display the details of the assertions that failed. This includes 
	 * the receipt of SOAPFaults, teststeps that failed, and general error messages that appear
	 * in the testlog.
	 * @return - Organized collection of error messages
	 * @throws Exception - Something went seriously wrong
	 */
	public AssertionResults scanLogs(Collection<String> sectionsToScan) throws Exception {

		if (sectionsToScan != null && sectionsToScan.size() == 0) 
			sectionsToScan = null;

		if (testDetails.size() > 1) 
			sectionsToScan = null;

		AssertionResults res = new AssertionResults();
		String dashes = "------------------------------------------------------------------------------------------------";

		res.add(dashes);
		for (TestDetails testSpec : testDetails) {
			res.add("Test: " + testSpec.getTestNum());
			res.add(dashes);
			Collection<String> sections;
			if (sectionsToScan == null)
				sections = testSpec.getTestPlanLogs().keySet();
			else
				sections = sectionsToScan;
			for (String section : sections) {
				if (section.equals("THIS"))
					continue;
				LogFileContent testLog = testSpec.getTestPlanLogs().get(section);
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
				for (TestStepLogContent stepLog : testLog.getStepLogs()) {
					res.add("Section: " + testLog.getTestAttribute() + " Step: " + stepLog.getName());
					res.add("Endpoint: " + stepLog.getEndpoint());

					StepGoals stepGoals = stepLog.getGoals();
					res.add("Goals:", true);
					for (String goal : stepGoals.goals) {
						res.add(formatGoal(goal), true);
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

					res.add("Status: " + ((stepLog.isSuccess()) ? "Pass" : "Fail"), stepLog.isSuccess());
					
					for (String detail : stepLog.getDetails()) {
						res.add("Detail: " + detail);
					}

					for (String report : stepLog.getReports()) {
						res.add("Report: " + report);
					}

					try {
						Metadata m;
						m = stepLog.getParsedInputMetadata();
						if (m != null && m.hasMetadata()) 
							res.add("Contents Sent:\n" + m.getContentsDescription());
						m = stepLog.getMetadata();
						if (m != null && m.hasMetadata()) 
							res.add("Contents Returned:\n" + m.getContentsDescription());
					} catch (Exception e) {

					}

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
	public List<TestDetails> getTestSpecs() {
		return testDetails;
	}

	/**
	 * Return LogMap, the collection of information from the log.xml files generated by
	 * the running of one or more tests.
	 * @return LogMap 
	 * @throws Exception - No tests have been specified, no logs can be found, or the log files cannot
	 * be parsed.
	 */
	public LogMap getLogMap() throws Exception {
		LogMap lm = new LogMap();

		if (testDetails == null)
			throw new Exception("Xdstest2#getLogMap: testSpecs is null");

		for (TestDetails testSpec : testDetails) {
			for (String section : testSpec.getTestPlanLogs().keySet()) {
				LogFileContent testLog = testSpec.getTestPlanLogs().get(section);
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

}
