package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testenginelogging.LogFileContentBuilder;
import gov.nist.toolkit.testenginelogging.TestLogDetails;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.testkitutilities.TestDefinition;
import gov.nist.toolkit.testkitutilities.TestKitSearchPath;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsParameterException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class XdsTest {

	private String mgmt;
	File toolkit;
	private LogRepository logRepository;
	private List<TestLogDetails> testSpecs;
	private boolean verbose = false;
	private boolean prepareOnly = false;
	private String siteName = null;
	private boolean secure = false;
	private boolean wssec = false;
	private boolean showExceptionTrace = false;
	boolean stopOnFirstFailure = false;
	private List<String> bargs;
	private Site site;
	private Sites sites;
	boolean status;
	TestConfig testConfig;
	final static Logger logger = Logger.getLogger(XdsTest.class);
	private boolean noExit = false;
	private static String[] configurationOptions = {
		"-T", "--testkit",
		"-L", "--logdir",
		"-s", "--site",
		"-K", "--toolkit"
	};
	private TestSession testSession;
	

	private static String[] operationOptions = {
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

	private static List<String> configurationOptionList;
	private static List<String> operationOptionList;

	static {
		configurationOptionList = Arrays.asList(configurationOptions);
		operationOptionList = Arrays.asList(operationOptions);
	}

	 XdsTest(TestKitSearchPath searchPath, TestSession testSession) {
//		this.searchPath = searchPath;
		this.testSession = testSession;
		noExit = true;
		testConfig = new TestConfig();
	}

	void error(String msg) {
		System.out.println(msg);
		if (noExit) return;
		System.exit(-1);
	}

	/**
	 * Load config from environment. Used only from command line incantation. From
	 * GUI, these are already filled in.
	 * @throws Exception
	 */
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
			sites = new CombinedSiteLoader(testSession).load(file, null);
		} catch (XdsInternalException e) {
			throw new Exception("Error loading actors.xml from path " + file + " : XDSTOOLKIT configuration option not set or incorrect\n" +
					"specific error is:\n\t" + e.getMessage());
		}
		bassert(sites.getSiteNames().size() > 0,"Could not load actor configuration from " + mgmt + File.separatorChar + "actors.xml");
		return sites;
	}

	public void setSite(Site site) {
		this.site = site;
		if (testConfig.site == null) testConfig.site = site;
	}
	
	public void setSites(Sites sites) {
		this.sites = sites;
	}

	private void initTestConfig() {
		testConfig.logRepository = logRepository;
		testConfig.site = site;
		testConfig.secure = secure;
		testConfig.saml = wssec;
		testConfig.configHome = (site != null) ? site.getHome() : "";
		testConfig.trace = showExceptionTrace;
		testConfig.prepare_only = prepareOnly;
		if (testConfig.soap == null)
			testConfig.soap = new Soap();
	}


	 List<TestLogDetails> runAndReturnLogs(Map<String, String> externalLinkage, Map<String, Object> externalLinkage2, TransactionSettings globalTransactionSettings, boolean writeLogFiles) throws Exception {
		initTestConfig();

        if (testConfig.site == null) {
            String siteName = System.getProperty("site");
            if (siteName != null)
                testConfig.site = loadSites().getSite(siteName, testSession);
        }

		if (testConfig.site == null)
			testConfig.site = site;

        testConfig.allRepositoriesSite = (sites == null) ? null : sites.getAllRepositoriesSite();

		if (testSpecs == null)
			throw new Exception("XdsTest#runAndReturnLogs: testSpecs is null");

		TestKitSearchPath searchPath = new TestKitSearchPath(globalTransactionSettings.environmentName, globalTransactionSettings.testSession);

        this.status = true;
		for (TestLogDetails testSpec : testSpecs) {
			testSpec.getTestInstance().setTestSession(testSession);
			System.out.println("Test: " + testSpec.getTestInstance().getId());
			TestDefinition testDefinition = searchPath.getTestDefinition(testSpec.getTestInstance().getId());
			File testKitFile = searchPath.getTestKitForTest(testSpec.getTestInstance().getId()).getTestKitDir();
			if (testKitFile == null)
				throw new Exception("Test " + testSpec.getTestInstance() + " not found");

			// Changes made by a test should be isolated to that test
			// They need to run independently
			TransactionSettings ts = globalTransactionSettings.clone();

			testConfig.testInstance = testSpec.getTestInstance();
			System.out.println("Sections: " + testSpec.testPlanFileMap.keySet());
			boolean isMultiSectionTest = testSpec.testPlanFileMap.keySet().size() > 1;
			for (String section : testSpec.testPlanFileMap.keySet()) {
                // This is experimental - this would improve output quality but does it break anything?
                String sectionLabel = testSpec.getTestInstance() + "-" + ((section.equals(".") ? "default" : section ));
				File testPlanFile = testSpec.testPlanFileMap.get(section);

				testConfig.testplanDir = testPlanFile.getParentFile();
				testConfig.logFile = null;
				testConfig.archiveLogFile = null;

				TestInstance testLogId = testSpec.getTestInstance();
				testSpec.setTestInstance(testLogId);
				File logDirectory;
				if (ts != null && ts.logRepository != null)
					logDirectory = ts.logRepository.logDir(testLogId);
				else
					logDirectory = logRepository.logDir(testLogId);

				// This is the log.xml file

				testConfig.logFile = new TestKitLog(logDirectory, testKitFile).getLogFile(testPlanFile);
				if (Installation.instance().propertyServiceManager().getArchiveLogs()) {
					File now = Installation.instance().newArchiveDir();
					testConfig.archiveLogFile = new File(now, "log.xml");
				}
				writeLogFiles = true;

				PlanContext plan = new PlanContext();		
				plan.setPreviousSectionLogs(testSpec.sectionLogMapDTO);
				plan.setTestConfig(testConfig);
				plan.setCurrentSection(section);
				plan.setExtraLinkage(externalLinkage);
				plan.setExtraLinkage2(externalLinkage2);
				plan.setWriteLogFiles(writeLogFiles);
				plan.setPreviousSectionLogs(testSpec.sectionLogMapDTO);
				plan.setTransactionSettings(ts);

				boolean status;
				try {
					status = plan.run(testPlanFile, isMultiSectionTest);
				} catch (MultiSectionTestRejectException e) {
					// cannot continue automatically
					break;
				}
				// this.status records whether any test failed
                if (this.status)
    				this.status = status;
				// Write document cache before the contents get stripped for response logging. Contents are preserved in  the full log.
				writeDocumentCache(plan.getResultsDocument());
                // This use of section is used to link reportDTOs in log files
				testSpec.addTestPlanLog(section, new LogFileContentBuilder().build(plan.results_document));

				if (status) 
					System.out.println("\t\t...Pass");
				else 
					System.out.println("\t\t...Fail");

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

	void writeDocumentCache(OMElement el) {
		try {
			el = XmlUtil.firstDecendentWithLocalName(
					el,
					"RetrieveDocumentSetResponse");

			if (el!=null) {
				RetrieveB rb = new RetrieveB();
				Map<String, RetrievedDocumentModel> resMap = rb
						.parse_rep_response(el).getMap();

				for (String docUid : resMap.keySet()) {
					RetrievedDocumentModel ri = resMap.get(docUid);
					File localFile = new File(getRepositoryCache(), ri.getDocUid().replace(":","") + LogFileContentBuilder.getRepositoryCacheFileExtension(ri.getContent_type()));

					Io.bytesToFile(localFile,
							ri.getContents());
				}
			}

		} catch (Throwable t) {
			System.out.println(t.toString());
		}
	}

	private File getRepositoryCache() {
		File cache = new File(Installation.instance().warHome(), "DocumentCache");
		cache.mkdirs();
		return cache;
	}

	 void addTestSpec(TestLogDetails ts) {
		if (testSpecs == null)
			testSpecs = new ArrayList<TestLogDetails>();
		testSpecs.add(ts);
	}

	 void setLogRepository(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	 void setWssec(boolean wssec) {
		this.wssec = wssec;
	}

	public void setToolkit(File toolkit) throws IOException {
		this.toolkit = toolkit;
		mgmt = toolkit + File.separator + "xdstest";
//		logRepository =
//		new LogRepositoryFactory().getLogRepository(Installation.instance().testLogCache(), null, LogRepositoryFactory.IO_format.JAVA_SERIALIZATION, LogRepositoryFactory.Id_type.TIME_ID, null);
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

}

