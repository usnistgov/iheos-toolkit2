package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrysupport.logging.RegistryResponseLog;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepositoryFactory;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import gov.nist.toolkit.xdsexception.XdsParameterException;
import org.apache.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class XdsTest {

	String version = "xx.yy";
	//controlling variables
	File testkit;
	File altTestkit;
	String mgmt;
	File toolkit;
	LogRepository logRepository;
	TestInstance testInstance;
	String testPart;
	List<TestLogDetails> testSpecs;
	List<File> logFiles;
	String testDir; 
	String tokens;
	boolean verbose = false;
	boolean verboseverbose = false;
	boolean prepareOnly = false;
	String siteName = null;
	String siteConfig = null;
	boolean secure = false;
	boolean wssec = false;
	String selfTest;
	boolean listingOnly = false;
	boolean tcListingOnly = false;
	String sep = File.separator;
	boolean runFlag = true;
	boolean showErrorsFlag = false;
	boolean showResponseFlag = false;
	boolean showExceptionTrace = false;
	boolean showReadme = false;
	boolean showConfig = false;
	boolean stopOnFirstFailure = false;
	boolean phoneHome = false;
	List<String> bargs;
	String decodeEnvironmentVar = null;
	boolean recursive;
	Site site;
	Sites sites;
	boolean status;
	String testPath;
	TestConfig testConfig;
	final static Logger logger = Logger.getLogger(XdsTest.class);
	boolean noExit = false;
	static String[] configurationOptions = {
		"-T", "--testkit",
		"-L", "--logdir",
		"-s", "--site",
		"-K", "--toolkit"
	};
	

	static String[] operationOptions = {
		"-h", "--help",
		"-v", "--verbose",
		"-t", "--test",
		"-tc", "--testcollection",
		"-err",
		"-ls", "--list",
		"-lsc",
		"-P", "--prepare",
		"-S", "--secure",
		"--saml",
		"-V", "--version",
		"-se", "--stoponerror",
		"-run"
	};

	static List<String> configurationOptionList;
	static List<String> operationOptionList;

	static {
		configurationOptionList = Arrays.asList(configurationOptions);
		operationOptionList = Arrays.asList(operationOptions);
	}

	public static boolean main(String[] args) {
		logger.info("xdstest started");
		XdsTest xt = new XdsTest(false);
//		String[] a = {"-Dsite=dev", "-DXDSTOOLKIT=/Users/bill/tmp/toolkitc", "-DXDSTESTLOGDIR=/Users/bill/dev/xdstoolkit/logs", "-DXDSTESTKIT=/Users/bill/tmp/toolkitc/testkit","-t", "12318", "-err"};
		return xt.run(args);
	}

	public XdsTest() {
		noExit = true;
		testConfig = new TestConfig();
	}

	public XdsTest(boolean noExit) {
		this.noExit = noExit;
		testConfig = new TestConfig();
	}

	void error(String msg) {
		System.out.println(msg);
		if (noExit) return;
		System.exit(-1);
	}

	List<String> reverse(List<String> in) {
		List<String> out = new ArrayList<String>();
		for (int i=in.size()-1; i>=0; i--) { out.add(in.get(i)); }
		return out;
	}

	void validateEnvironment() throws XdsParameterException {
		bassert(toolkit != null && toolkit.exists(),"XDSTOOLKIT directory " + toolkit + " does not exist");
		bassert(new File(toolkit + "/xdstest").exists(),"XDSTOOLKIT directory " + toolkit + " is not really the toolkit, no xdstest subdirectory exists");

//		bassert(logRepository != null && logRepository.isDirectory(),"XDSTESTLOGDIR directory " + logRepository + " does not exist or is not a directory");

		bassert(testkit != null && testkit.exists(),"XDSTESTKIT directory " + testkit + " does not exist");
		bassert(new File(testkit + "/tests").exists(),"XDSTESTKIT directory " + testkit + " is not really the testkit, no tests subdirectory exists");
	}

	String getFromEnv(String propName) {
		String value = null;
		value = System.getenv(propName);
		if (value != null)
			return value;
		value = System.getProperty(propName);
		return value;
	}

	public void loadProperties(List<String> props) {
		String delete = null;
		while (true) {
			for (String prop : props) {
				if (prop.startsWith("-D")) {
					delete = prop;

					prop = prop.substring(2);
					String[] parts = prop.split("=");
					if (parts.length == 2) {
						System.out.println("Set Property " + parts[0] + " to " + parts[1]);
						System.setProperty(parts[0], parts[1]);
					}

					break;
				}
			}
			if (delete == null)
				break;
			props.remove(delete);
			delete = null;
		}
	}

	/**
	 * Load config from environment. Used only from command line incantation. From
	 * GUI, these are already filled in.
	 * @throws Exception
	 */
	public void loadFromEnvironment() throws Exception  {
		String toolkitstr = getFromEnv("XDSTOOLKIT");
		String testkitdir = getFromEnv("XDSTESTKIT");
		String logdirstr = getFromEnv("XDSTESTLOGDIR");
		siteName = getFromEnv("site");

		if (testkitdir != null)
			testkit = new File(testkitdir);
		if (logdirstr != null) {
			File testLogCache = Installation.installation().testLogCache();
			logRepository = new LogRepositoryFactory().
					getRepository(
							testLogCache,   // location
							null,           // user
							LogIdIOFormat.JAVA_SERIALIZATION,   // IO format
							LogIdType.TIME_ID,                // ID type
							null);            // ID
		}
		if (toolkitstr != null)
			toolkit = new File(toolkitstr);

		validateEnvironment();

		version = Io.stringFromFile(new File(toolkit + "/admin/version")).trim();
		testConfig.version = version;

		mgmt = toolkit + File.separator + "xdstest";
		testConfig.testmgmt_dir = mgmt;

		loadTestKitVersion();
		
		// these properties are needed by the DSIG package in gov.nist.registry.common2.dsig
		// specifically the KeyStoreAccessObject which need to know there to find the 
		// keystore for signing SAML assertionEleList
		
		try {
			System.setProperty("DSIG_keystore_url",System.getProperty("javax.net.ssl.keyStore"));
			System.setProperty("DSIG_keystore_password",System.getProperty("javax.net.ssl.keyStorePassword"));
			System.setProperty("DSIG_keystore_alias", getFromEnv("keystore_alias"));
		} catch (Exception e) {}
	}

	public boolean run(String[] in_args)  {
		List<String> args = new ArrayList<String>();
		for (int i=0; i<in_args.length; i++) args.add(in_args[i]);

		try {
			return run(args);
		} catch (Exception e) {
			showException(e);
			return false;
		}
	}

	void loadTestKitVersion() throws Exception {
		File toolkitVersionFile = new File(toolkit + File.separator + "admin" + File.separator + "version");
		if (!toolkitVersionFile.exists())
			throw new Exception("Invalid toolkit (" + toolkit + ") - no admin/version file");
		testConfig.testkitVersion = Io.stringFromFile(toolkitVersionFile).trim().replaceAll(" ", "_");
	}

	Sites loadSites() throws FactoryConfigurationError, Exception {
		Sites sites;

		File file = null;
		try {
			file = new File(mgmt + File.separatorChar + "actors.xml");
//			sites = new Sites(file);
			sites = new CombinedSiteLoader().load(file, null);
		} catch (XdsInternalException e) {
			throw new Exception("Error loading actors.xml from path " + file + " : XDSTOOLKIT configuration option not set or incorrect\n" +
					"specific error is:\n\t" + e.getMessage());
		}
		bassert(sites.getSiteNames().size() > 0,"Could not load actor configuration from " + mgmt + File.separatorChar + "actors.xml");
		return sites;
	}

	File getMgmtFile(String filename) {
		return new File(mgmt + File.separatorChar + filename);
	}

	String loadConfigFile(String filename) throws IOException {
		return Io.stringFromFile(getMgmtFile(filename));
	}

	void saveConfigFile(String filename, String value) throws IOException {
		Io.stringToFile(getMgmtFile(filename), value);
	}

	public void setSite(Site site) {
		this.site = site;
		if (testConfig.site == null) testConfig.site = site;
	}
	
	public void setSites(Sites sites) {
		this.sites = sites;
	}


	public boolean run(List<String> args) throws Exception   {

		testConfig.args = args.toString();

		loadProperties(args);

		loadFromEnvironment();

		parseOptions(args);

		if (decodeEnvironmentVar != null) {
			if (decodeEnvironmentVar.equals("XDSTOOLKIT")) {
				System.out.println(toolkit);
				System.exit(1);
			}
		}

		if (site == null) logger.debug("No site specified");
		else logger.debug("Site specified is " + site.getName());

		sites = loadSites();
		testConfig.allRepositoriesSite = sites.getAllRepositoriesSite();

		if (siteName == null) {
			// no site is offered on command line, maybe default is declared in actors.xml
			siteName = sites.getDefaultSiteName();
		}

		if (site == null) {
			logger.debug("site not set - try pulling default site");
		}

		site = sites.getSite(siteName);

		if (site == null) {
			logger.debug("site still not set");
		}

		testConfig.site = site;

		logger.debug("XdsTest.run - Site is " + siteName);

		if (showConfig) {
			showConfiguration(sites);
		}

		if (verboseverbose)
			System.out.println(site);


		bassert(siteName != null, "No site specified on command line and no default present in actors.xml");

		return runTest();	

	}

	public boolean runTest() throws Exception,
	FactoryConfigurationError {


		initTestConfig();


		if (listingOnly) {
			try {
				TestLogDetails.listTestKitContents(testkit);
			} catch (Exception e) {}
			return true;
		}

		if (tcListingOnly) {
			try {
				TestCollection.listTestKitCollections(testkit);
			} catch (Exception e) {}
			if ( !noExit) System.exit(-1);
		}



		boolean status = true;
		if (runFlag) 
			status = run();

		if (showResponseFlag)
			showResponse();
		else if (showErrorsFlag) 
			showErrors();

		if ( !noExit )
			System.exit(0);
		return status;
	}

	private void initTestConfig() {
		testConfig.testkitHome = testkit;
		testConfig.altTestkitHome = altTestkit;
		testConfig.logRepository = logRepository;
		testConfig.site = site;
		testConfig.secure = secure;
		testConfig.saml = wssec;
		testConfig.configHome = (site != null) ? site.getHome() : "";
		testConfig.trace = showExceptionTrace;
		testConfig.prepare_only = prepareOnly;
		if (testConfig.soap == null)
			testConfig.soap = new Soap();

		testConfig.pid_allocate_endpoint = (site != null) ? site.getPidAllocateURI() : null;

		logFiles = new ArrayList<File>();
	}

	private void showResponse() throws FactoryConfigurationError, Exception {
		for (File logFile : logFiles) {
			LogFileContent log = new LogFileContent(logFile);
			StringBuffer buf = new StringBuffer();
			for (int i=0; i<log.size(); i++) {
				TestStepLogContent tslog = log.getTestStepLog(i);
				buf.append("STEP ");
				buf.append(tslog.getName());
				buf.append(":");
				buf.append(tslog.getRegistryResponse().toString());
				buf.append("\n");
			}
			System.out.println(buf);
		}
	}

	private void showErrors()  {
		StringBuffer buf = new StringBuffer();
		buf.append("*******************   Error Summary  *******************\n");
		for (File logFile : logFiles) {
			LogFileContent log;
			try {
				log = new LogFileContent(logFile);
			} 
			catch (Exception e) {
				buf.append("Error: " + e.getMessage() );
				buf.append(logFile);
				buf.append("\n");
				continue;
			}
			if ( !log.isSuccess() ) {
				String fatal = log.getFatalError();
				if (fatal != null) {
					buf.append("Test ");
					buf.append(log.getTestAttribute());
					buf.append("\t");
					buf.append("Fatal Error: ");
					buf.append(fatal);
					buf.append("\n");
					continue;
				}
				for (int stepIndex=0; stepIndex<log.size(); stepIndex++) {
					String stepName;
					try {
						stepName = log.stepName(stepIndex);
					} catch (Exception e) {
						buf.append("Error: cannot access " + stepIndex + "th step of log file\n");
						continue;
					}

					addRegistryResponseErrors(buf,
							log, stepIndex, stepName); 


					addAssertionErrors(buf, log,
							stepIndex);

					addSoapFaults(buf, log,
							stepIndex);
				}
			}
		}
		System.out.println(buf);
	}

	private void addAssertionErrors(StringBuffer buf, LogFileContent log,
			int stepIndex) {
		List<String> assertionErrors = null;
		try {
			assertionErrors = log.getAssertionErrors(stepIndex);
		} catch (XdsInternalException e) {
			buf.append(e.getMessage());
			buf.append("\n");
			return;
		}
		for (String assertionError : assertionErrors) {
			buf.append("***** Test ");
			buf.append(log.getTestAttribute());
			buf.append("\tstep ");
			buf.append(assertionError);
			buf.append("\n");
		}
	}

	private void addSoapFaults(StringBuffer buf, LogFileContent log,
			int stepIndex) {
		List<String> soapFaults = null;
		try {
			soapFaults = log.getSoapFaults(stepIndex);
		} catch (XdsInternalException e) {
			buf.append(e.getMessage());
			buf.append("\n");
			return;
		}
		for (String assertionError : soapFaults) {
			buf.append("***** Test ");
			buf.append(log.getTestAttribute());
			buf.append("\tSOAP FAULT ");
			buf.append("\tstep ");
			buf.append(assertionError);
			buf.append("\n");
		}
	}

	private void addRegistryResponseErrors(StringBuffer buf,
			LogFileContent log, int stepIndex, String stepName) {
		RegistryResponseLog rr;
		try {
			rr = log.getUnexpectedErrors(stepIndex);
		} catch (Exception e) {
			//			buf.append("Error: cannot extract RegistryResponse: " + e.getMessage() + "\n");
			//			buf.append(ExceptionUtil.exception_details(e));
			return;
		}
		if (rr.size() > 0) {
			buf.append("***** Test ");
			buf.append(log.getTestAttribute());
			buf.append("\t");
			buf.append("Step ");
			buf.append(stepName);
			buf.append("\n");
			buf.append(rr.getErrorSummary());
			buf.append("\n");
		}
	}

	void showException(Exception e) {
		if (showExceptionTrace || e instanceof NullPointerException) 
			System.out.println(ExceptionUtil.exception_details(e));
		else
			System.out.println("\t\t" + e.getMessage());
	}

	void showConfiguration(Sites sites) throws Exception {
		StringBuffer buf = new StringBuffer();

		//loadFromEnvironment();

		try {
			//
			// Sites
			//

			buf.append("\n========== Sites ===========\n");
			Collection<String> siteNames = sites.getSiteNames();
			buf.append("Sites: ");
			for (Iterator<String> it=siteNames.iterator(); it.hasNext(); ) {
				buf.append(" ");
				buf.append(it.next());
			}
			buf.append("\n");
			buf.append("\tfrom ").append(getMgmtFile("actors.xml")).append("\n");

			//
			// Patient ID
			//
			buf.append("\n========== Patient ID ===========\n");
			String value;
			try {
				value = loadConfigFile("patientid.txt").trim();
			} catch (Exception e) {
				buf.append("No Patient ID found...allocating from " + testConfig.pid_allocate_endpoint + " ...");
				new PatientIdAllocator(testConfig).useNewPatientId();
				try {
					buf.append("Success\n");
					value = loadConfigFile("patientid.txt").trim();
				} catch (Exception e1) {
					buf.append("Failed\n");
					value = null;
				}
			}
			buf.append("Patient ID: ").append(value).append("\n");
			buf.append("\tfrom ").append(getMgmtFile("patientid.txt")).append("\n");
			if (value == null) buf.append("\tError: no Patient ID defined\n");

			//
			// Alt Patient ID
			//
			buf.append("\n========== Alt Patient ID ===========\n");
			try {
				value = loadConfigFile("patientid_alt.txt").trim();
			} catch (Exception e) {
				buf.append("No Alt Patient ID found...allocating from " + testConfig.pid_allocate_endpoint + " ...");
				value = new PatientIdAllocator(testConfig).getNewPatientId();
				try {
					saveConfigFile("patientid_alt.txt", value);
					buf.append("Success\n");
				} catch (Exception e1) {
					buf.append("Failed\n");
					value = null;
				}

			}
			buf.append("Alternate Patient ID: ").append(value).append("\n");
			buf.append("\tfrom ").append(getMgmtFile("patientid_alt.txt")).append("\n");
			if (value == null) buf.append("\tWarning: no Alternate Patient ID defined\n");

			//
			// UniqueId Base
			//
			buf.append("\n========== Unique ID base ===========\n");
			try  {
				value = loadConfigFile("uniqueid_base.txt").trim();
			} catch (Exception e) {
				value = "1.2.3.4.5.";
				buf.append("No Unique ID base found...initializing to " + value + " ...");
				saveConfigFile("uniqueid_base.txt", value);
				buf.append("Success\n");
			}
			buf.append("Unique ID base: ").append(value).append("\n");
			buf.append("\tfrom ").append(getMgmtFile("uniqueid_base.txt")).append("\n");
			if (value == null) buf.append("\tError: no Unique ID base defined\n");
			else {
				if (value.startsWith("1.2.3")) buf.append("\tWarning: Default Unique ID base is being used - define your own\n");
				if (!value.endsWith(".")) buf.append("\tError: Default Unique ID base must end with period (.) character\n");
			}

			//
			// UniqueId Incr
			//
			buf.append("\n========== Unique ID incr ===========\n");
			try {
				value = loadConfigFile("uniqueid_incr.txt").trim();
			} catch (Exception e) {
				value = "1";
				buf.append("No Unique ID incr found...initializing to " + value + " ...");
				saveConfigFile("uniqueid_incr.txt", value);
				buf.append("Success\n");
			}
			buf.append("Unique ID increment: ").append(value).append("\n");
			buf.append("\tfrom ").append(getMgmtFile("uniqueid_incr.txt")).append("\n");
			if (value == null) buf.append("\tError: no Unique ID increment defined\n");

			//
			// Source Id
			//
			buf.append("\n========== Source ID ===========\n");
			value = null;
			try {
				value = loadConfigFile("sourceid.txt").trim();
			} catch (Exception e) {}
			buf.append("Source ID: ").append(value).append("\n");
			buf.append("\tfrom ").append(getMgmtFile("sourceid.txt")).append("\n");
			if (value == null) buf.append("\tWarning: no Source ID defined\n");

			//
			// Keystores
			//
			buf.append("\n========== Keystores ===========\n");
			buf.append("Keystores: ");
			File keystores_dir = new File(mgmt + File.separatorChar + "keystores");
			if (!keystores_dir.exists()) buf.append("\tWarning: keystores directory missing\n");
			else {
				String[] keystore_names = keystores_dir.list();
				for (int i=0; i<keystore_names.length; i++) {
					if (keystore_names[i].startsWith("."))
						continue;
					buf.append(" ").append(keystore_names[i]);
				}
				buf.append("\n");
			}
			buf.append("\tfrom ").append(getMgmtFile("keystores")).append("\n");

		} catch (Exception e) {
			buf.append(ExceptionUtil.exception_details(e));
		}


		System.out.println(buf);
	}

	private boolean run()  {
		runHadError = false;
		try {
			TransactionSettings ts = new TransactionSettings();
			ts.logRepository = logRepository;
			runAndReturnLogs(null, null, ts, true);
		} catch (Exception e1) {
			if (showExceptionTrace) 
				System.out.println(ExceptionUtil.exception_details(e1));
			else
				System.out.println("\t\t" + e1.getMessage());
			return false;
		}
		return !runHadError;   
	}

	boolean runHadError = false;

	public List<TestLogDetails> runAndReturnLogs(Map<String, String> externalLinkage, Map<String, Object> externalLinkage2, TransactionSettings globalTransactionSettings, boolean writeLogFiles) throws Exception {
		initTestConfig();

		//resetTestSpecLogs();

        if (testConfig.site == null) {
            String siteName = System.getProperty("site");
            if (siteName != null)
                testConfig.site = loadSites().getSite(siteName);
        }

		if (testConfig.site == null)
			testConfig.site = site;

        testConfig.allRepositoriesSite = (sites == null) ? null : sites.getAllRepositoriesSite();

		if (testSpecs == null)
			throw new Exception("XdsTest#runAndReturnLogs: testSpecs is null");

        this.status = true;
		for (TestLogDetails testSpec : testSpecs) {
			System.out.println("Test: " + testSpec.getTestInstance().getId());
			// Changes made by a test should be isolated to that test
			// They need to run independently
			TransactionSettings ts = globalTransactionSettings.clone();

			testConfig.testInstance = testSpec.getTestInstance();
			System.out.println("Sections: " + testSpec.testPlanFileMap.keySet());
			for (String section : testSpec.testPlanFileMap.keySet()) {
                // This is experimental - this would improve output quality but does it break anything?
                String sectionLabel = testSpec.getTestInstance() + "-" + ((section.equals(".") ? "default" : section ));
				File testPlanFile = testSpec.testPlanFileMap.get(section);

				testConfig.testplanDir = testPlanFile.getParentFile();
				testConfig.logFile = null;

				TestInstance testLogId = testSpec.getTestInstance();
				testSpec.setTestInstance(testLogId);
				File logDirectory = logRepository.logDir(testLogId);
				if (ts != null && ts.logRepository != null)
					logDirectory = ts.logRepository.logDir(testLogId);

				// This is the log.xml file
				testConfig.logFile = new TestKitLog(logDirectory, testkit, altTestkit).getLogFile(testPlanFile);
				writeLogFiles = true;
				if (writeLogFiles) {
					logFiles.add(testConfig.logFile);
				}
				
				PlanContext plan = new PlanContext();		
				plan.setPreviousSectionLogs(testSpec.sectionLogMap);
				plan.setTestConfig(testConfig);
				plan.setCurrentSection(section);
				plan.setExtraLinkage(externalLinkage);
				plan.setExtraLinkage2(externalLinkage2);
				plan.setWriteLogFiles(writeLogFiles);
				plan.setPreviousSectionLogs(testSpec.sectionLogMap);
				plan.setTransactionSettings(ts);

				boolean status =  plan.run(testPlanFile);
				// this.status records whether any test failed
                if (this.status)
    				this.status = status;
                // This use of section is used to link reports in log files
				testSpec.addTestPlanLog(section, new LogFileContent(plan.results_document));

				if (status) 
					System.out.println("\t\t...Pass");
				else 
					System.out.println("\t\t...Fail");

				if (!status)
					runHadError = true;

                if (this.status == false) {
                    if (stopOnFirstFailure) {
                        System.out.println("Stopping on first failure");
                        return testSpecs;
                    }
                    System.out.println("Continuing after errors");
                }
			}
		}
		return testSpecs;
	}

	public void resetTestSpecLogs() {
		if (testSpecs != null)
			for (TestLogDetails testSpec : testSpecs) {
				testSpec.resetLogs();
			}		
	}


	void parseOptions(List<String> args) throws Exception {
//		System.out.println("Args: " + args);
		if (args.size() > 0) {
			bargs = reverse(args); // bargs = bacwards args (works as stack)
		} else
			bargs = args;
		testSpecs = new ArrayList<TestLogDetails>();

		parseOperationOptions();

		if (logRepository == null)
			throw new Exception("logRepository not configured");
			//logRepository = testkit;  // put logs right next to testplans

		boolean tlsConfigured = false;
		try { 
			tlsConfigured = "true".equals(System.getenv("tlsConfigured")); 
		} catch (Exception e) {}

		boolean tls2Configured = false;
		try { 
			tls2Configured = "true".equals(System.getProperty("tlsConfigured")); 
		} catch (Exception e) {}

		if (secure && ! tlsConfigured && ! tls2Configured) 
			parameterError("-S option specified but TLS is not configured, please set the XDSEVENT environment variable");

	}

	void parseOperationOptions() throws Exception {
		while (bhas()) {
			String option = bpop("");

			if (option.equals("--secure") || option.equals("-S")) {
				secure = true;
			}
			else if (option.equals("--wssec") ) {
				wssec = true;
			}
			else if (option.equals("--phonehome") ) {
				phoneHome = true;
			}
			else if (option.equals("--testcollection") || option.equals("-tc")) {
				String testCollectionName = bpop("--testcollection expects a path");
				optAssert(!testCollectionName.startsWith("-"), "--testcollection expects a test collection name");

				TestCollection tcol = new TestCollection(testkit,testCollectionName);
				if (verbose) System.out.println("testcollection: " + tcol);

				testSpecs.addAll(tcol.getTestSpecs());

				while (bhas() && !bpeek().startsWith("-")) {
					testSpecs.addAll(new TestCollection(testkit,bpop("oops")).getTestSpecs());
				}
				runFlag = true;
			}
			else if (option.equals("--test") || option.equals("-t")) {
				if (verbose) System.out.println("parsing -t");
				bshow();
				testInstance = new TestInstance(bpop("--testId expects a test number"));
				if (verbose) System.out.println("testId=" + testInstance);
				optAssert(!testInstance.getId().startsWith("-"), "--testId expects a test number");
				TestLogDetails ts = new TestLogDetails(testkit, testInstance);

				if (verbose) System.out.println("before section check");
				if (verbose) System.out.println("testspec is " + ts);
				bshow();
				if (bhas() && !bpeek().startsWith("-")) {
					if (verbose) System.out.println("Looking for sections");
					bshow();
					List<String> sections = null;
					while (bhas() && !bpeek().startsWith("-")) {
						String section = bpop("oops");
						if (verbose) System.out.println("found section=" + section);
						if (sections == null)
							sections = new ArrayList<String>();
						sections.add(section);
					}
					if (sections != null) {
						ts.setLogRepository(logRepository);
						ts.selectSections(sections);
					}
				}
				testSpecs.add(ts);
				testInstance = null;
				runFlag = true;
			}
			else if (option.equals("--decode")) {
				decodeEnvironmentVar = bpop("--decode option requires following name");
			}
			else if (option.equals("--config") || option.equals("-c")) {
				showConfig = true;
			}
			else if (option.equals("--help") || option.equals("-h")) {
				displayHelpAndExit();
			}
			else if (option.equals("--errors") || option.equals("-err")) {
				showErrorsFlag = true;
			}
			else if (option.equals("--response") || option.equals("-r")) {
				showResponseFlag = true;
			}
			else if (option.equals("--verboseverbose") || option.equals("-vv") ||
					option.equals("--verbose") || option.equals("-v")) {
				verboseverbose = true;
				verbose = true;
				testConfig.verbose = true;
			}
			else if (option.equals("-se") || option.equals("--stoponerror")) {
				stopOnFirstFailure = true;
			}
			else if (option.equals("-O") || option.equals("--override")) {
				testConfig.endpointOverride = true;
			}
			else if (option.equals("--listing") || option.equals("-ls")) {
				listingOnly = true;
				recursive = false;
			}
			else if (option.equals("-lsc")) {
				tcListingOnly = true;
				recursive = false;
			}
			else if (option.equals("-run")) {
				runFlag = true;
			}
			else if (option.equals("-trace") || option.equals("--trace")) {
				showExceptionTrace = true;
			}
			else if (option.equals("--prepare") || option.equals("-P")) {
				prepareOnly = true;
			}
			else if (option.equals("--version") || option.equals("-V")) {
				System.out.println("xdstest XDS Testing Tool version " + version);
				if ( !noExit )
					System.exit(0);
			}
			else if (option.equals("--selftest") || option.equals("-st")) {
				selfTest();
			}
			else if (configurationOptionList.contains(option)) {
				parameterError("Configuration option " + option + " must be specified before operation options. " +
				" see xdstest -h for details.");
			}
			else 
				System.out.println("Unknown option " + option + ". Ignoring. See xdstest -h for details.");

		}
	}

	public List<TestLogDetails> getTestSpecs() {
		return testSpecs;
	}

	public void addTestSpec(TestLogDetails ts) {
		if (testSpecs == null)
			testSpecs = new ArrayList<TestLogDetails>();
		testSpecs.add(ts);
	}

	public File getTestkit() {
		return testkit;
	}
	public File getAltTestkit() {
		return altTestkit;
	}

	public void setTestkit(File testkit) {
		this.testkit = testkit;
	}

	public void setAltTestkit(File testkit) {
		this.altTestkit = testkit;
	}


	public File getLogDir() throws IOException {
		return logRepository.logDir();
	}

	public void setLogRepository(LogRepository logRepository) {
		this.logRepository = logRepository;
	}




	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void addTestCollection(String testCollectionName) throws Exception {
		if (testSpecs == null)
			testSpecs = new ArrayList<TestLogDetails>();

		List<TestLogDetails> details;

		try {
			details = new TestCollection(altTestkit, testCollectionName).getTestSpecs();
		} catch (Exception e) {
			details = new TestCollection(testkit, testCollectionName).getTestSpecs();
		}
		testSpecs.addAll(details);
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public void setWssec(boolean wssec) {
		this.wssec = wssec;
	}

	public void setToolkit(File toolkit) throws IOException {
		this.toolkit = toolkit;
		mgmt = toolkit + File.separator + "xdstest";
//		logRepository =
//		new LogRepositoryFactory().getRepository(Installation.installation().testLogCache(), null, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
		testConfig.testmgmt_dir = mgmt;
	}

	String bpeek() throws XdsParameterException { haveArg("bpeak() failed - list empty"); return bargs.get(bargs.size()-1);  }
	String peek() { return bargs.get(bargs.size()-1); }
	String bpop(String msg) throws XdsParameterException { haveArg(msg); String val = peek(); bargs.remove(bargs.size()-1); return val; }
	void bpush(String arg) { bargs.add(arg); }
	void bshow() { if (verbose) System.out.println(bargs); }
	void haveArg(String msg) throws XdsParameterException { if (bargs.size() == 0) { parameterError(msg); } }
	boolean bhas() { return bargs.size() > 0; }
	void bassert(boolean test, String message) throws XdsParameterException { if (!test) { parameterError("Error: " + message);  } }
	void optAssert(boolean test, String message) throws XdsParameterException { if (!test) { parameterError(message); } }

	void parameterError(String  msg) throws XdsParameterException  {
		throw new XdsParameterException(msg, null);
	}

	void displayHelpAndExit() {
		try {
			String help;
			help = Io.stringFromFile(new File(toolkit + File.separator + "xdstest" + File.separator + "help.txt"));
			System.out.println(help);
		} catch (IOException e) {
			System.out.println("Cannot load help file from " + toolkit + File.separator + "xdstest" + File.separator + "help.txt");
		}
		if (!noExit)
			System.exit(0);
	}

	void selfTest() {

	}


}

