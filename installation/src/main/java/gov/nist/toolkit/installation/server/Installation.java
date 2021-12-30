package gov.nist.toolkit.installation.server;


import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.http.annotation.Obsolete;
import java.util.logging.Logger;
import gov.nist.toolkit.installation.shared.TestSession;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;


public class Installation {
    private File warHome = null;
    private File externalCache = null;
    private String sep = File.separator;
    public TkProps tkProps = new TkProps();
	private String servletContextName = "/xdstools2";

    public final static String DEFAULT_ENVIRONMENT_NAME = "default";
    private final static String LOG_ARCHIVE_DIRECTORY = "archive";

    private PropertyServiceManager propertyServiceMgr = null;
    private static Logger logger = Logger.getLogger(Installation.class.getName());

    static Installation me = null;
    static private boolean testIsRunning = false;


    public static boolean isTestRunning() {
        return testIsRunning;
    }

    public static void setTestRunning(boolean testIsRunning) {
        Installation.testIsRunning = testIsRunning;
    }

    /**
     * will self initialize to the production manager.  For testing purposes
     * it can be initialized with TestResourceCacheFactory
     * @return
     */

    public String getToolkitBaseUrl() {
        boolean ssl = propertyServiceMgr.isUsingSSL();
        return ((ssl) ? "https://" : "http://")
                + propertyServiceMgr.getToolkitHost()
                + ":"
                + ((ssl) ? propertyServiceMgr.getSSLPort() : propertyServiceMgr.getToolkitPort())
                + getServletContextName()
                + "/Xdstools2.html";
    }

    public String toString() {
        return String.format("warHome=%s externalCache=%s", warHome, externalCache);
    }

    static {
        logger.info("Attempting static initialization of WARHOME");
        // This works for unit tests if warhome.txt is installed as part of a unit test environment
        String warhomeTxt = null;
        try {
            //warhomeTxt = instance().getClass().getResource("/warhome/warhome.txt").getFile();
        } catch (Throwable t) {}
        if (warhomeTxt != null) {
            instance().warHome(new File(warhomeTxt).getParentFile());
            logger.info("WARHOME initialized to " + instance().warHome);
        }
    }

    static public Installation instance() {
        if (me == null) {
            me = new Installation();
        }
        return me;
    }

    static public Installation instance(ServletContext servletContext) {
        if (me.warHome == null)
            me.warHome(new File(servletContext.getRealPath("/")));
        return me;
    }

    private Installation() {
        logger.info(String.format("Installation rooted at %s", toString()));
    }

    public File warHome() {
        if (warHome == null) {
            File warMarkerFile = null;
            try {
                warMarkerFile = Paths.get(instance().getClass().getResource("/").toURI()).resolve("war/war.txt").toFile();
            } catch (Throwable t) {}
            if (warMarkerFile != null) {
                instance().warHome(warMarkerFile.getParentFile());
            }
        }
        return warHome;
    }
    synchronized public void warHome(File warHome) {
        if (this.warHome != null /* && warHome().equals(warHome) */) {
            logger.info("... oops - warHome already initialized to " + warHome);
            return; /* already set */
        }
        logger.info("V2 - Installation - war home set to " + warHome);
        if (warHome == null)
            logger.severe(ExceptionUtil.here("warhome is null"));
        this.warHome = warHome;
//        propertyServiceMgr = null;
        propertyServiceManager();  // initialize
        String ec = propertyServiceManager().getPropertyManager().getExternalCache();

        if (ec == null || ec.equals("")) logger.severe("EC not found in toolkit.properties");

        logger.info("External Cache as reported by toolkit.properties");
		if (externalCache == null) { // this can be different in a unit test situation
            externalCache = new File(ec);
        }
		logger.info("Installation: External Cache set to " + externalCache);
		if (!externalCache.exists()) {
            logger.severe("External Cache does not exist at " + externalCache);
            externalCache = null;
            return;
        }
        logger.info("Toolkit running at " + propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getToolkitPort());

        TestSessionFactory.initialize(TestSession.DEFAULT_TEST_SESSION);
    }

    public static String asFilenameBase(Date date) {
        Calendar c  = Calendar.getInstance();
        c.setTime(date);

        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
            month = "0" + month;
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        if (day.length() == 1 )
            day = "0" + day;
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        if (hour.length() == 1)
            hour = "0" + hour;
        String minute = Integer.toString(c.get(Calendar.MINUTE));
        if (minute.length() == 1)
            minute = "0" + minute;
        String second = Integer.toString(c.get(Calendar.SECOND));
        if (second.length() == 1)
            second = "0" + second;
        String mili = Integer.toString(c.get(Calendar.MILLISECOND));
        if (mili.length() == 2)
            mili = "0" + mili;
        else if (mili.length() == 1)
            mili = "00" + mili;

        String dot = "_";

        String val =
                year +
                        dot +
                        month +
                        dot +
                        day +
                        dot +
                        hour +
                        dot +
                        minute +
                        dot +
                        second +
                        dot +
                        mili
                ;
        return val;
    }

    public static String nowAsFilenameBase() {
        return asFilenameBase(new Date());
    }

    public File archive() { return new File(externalCache(), LOG_ARCHIVE_DIRECTORY); }

    public File newArchiveDir() {
        File archive = new File(externalCache(), LOG_ARCHIVE_DIRECTORY);
        File now = new File(archive, nowAsFilenameBase());
        now.mkdirs();
        return now;
    }

    public File datasets() { return new File(externalCache(), "datasets"); }

	public File externalCache() { return externalCache; }
	public void externalCache(File externalCache) {
			this.externalCache = externalCache;
        logger.info("Installation: External Cache set to " + externalCache);
		try {
			tkProps = TkLoader.tkProps(instance().getTkPropsFile()); //TkLoader.tkProps(new File(Installation.instance().externalCache() + File.separator + "tk_props.txt"));
		} catch (Exception e) {
//			logger.warning("Cannot load tk_props.txt file from External Cache");
            tkProps = new TkProps();
        }
    }

    public List<String> getEnvironmentNames() {
        List<String> names = new ArrayList<>();
        File envsFile = environmentFile();

        for (File envFile : envsFile.listFiles()) {
            if (envFile.isDirectory())
                names.add(envFile.getName());
        }
        return names;
    }

    public List<TestSession> getTestSessions() {
        Set<TestSession> ts = new HashSet<>();
        File tlsFile = testLogCacheDir();

        if (tlsFile.exists()) {
            for (File tlFile : tlsFile.listFiles()) {
                if (tlFile.isDirectory() && !tlFile.getName().startsWith("."))
                    ts.add(new TestSession(tlFile.getName()));
            }
        }

        tlsFile = simDbFile();
        if (tlsFile.exists()) {
            for (File tlFile : tlsFile.listFiles()) {
                if (tlFile.isDirectory() && !tlFile.getName().startsWith("."))
                    ts.add(new TestSession(tlFile.getName()));
            }
        }

        tlsFile = actorsDir();
        if (tlsFile.exists()) {
            for (File tlFile : tlsFile.listFiles()) {
                if (tlFile.isDirectory() && !tlFile.getName().startsWith("."))
                    ts.add(new TestSession(tlFile.getName()));
            }
        }
        List<TestSession> testSessions = new ArrayList<>();
        testSessions.addAll(ts);
        return testSessions;
    }

    public void overrideToolkitPort(String port) {
        propertyServiceManager().setOverrideToolkitPort(port);
    }

    public File getTkPropsFile() {
        return new File(Installation.instance().externalCache() + File.separator + "tk_props.txt");
    }



    public boolean initialized() { return warHome != null && externalCache != null; }

    public PropertyServiceManager propertyServiceManager() {
        if (propertyServiceMgr == null)
            propertyServiceMgr = new PropertyServiceManager();
        return propertyServiceMgr;
    }

    public File actorsDir() {
        return new File(externalCache() + File.separator + "actors");
    }

    public File actorsDir(TestSession testSession) {
        if (testSession == null) throw new ToolkitRuntimeException("TestSession is null");
        File f = new File(actorsDir(), testSession.getValue());
        f.mkdirs();
        return f;
    }

    public File testSessionMgmtDir() {
        return new File(Installation.instance().externalCache(), "TestSessionMgmt");
    }

    public File testSessionMgmtDir(TestSession testSession) {
        return new File(new File(Installation.instance().externalCache(), "TestSessionMgmt"), testSession.getValue());
    }

    public File simDbFile() {
        return new File(externalCache(), "simdb");
    }

    /**
    * @return a {@link File} object representing the simdb directory, that is,
    * the directory in which information for simulators is maintained on disc.
    */
   public File simDbFile(TestSession testSession) {
       if (testSession == null) throw new ToolkitRuntimeException("TestSession is null");
        return new File(simDbFile(), testSession.getValue());
    }

    public File resourceCacheFile() {
       File f = new File(externalCache(), "resourceCache");
       f.mkdirs();
       return f;
   }

    public File fhirSimDbFile(TestSession testSession) {
        return simDbFile(testSession);
    }

    public List<String> getListenerPortRange() {
        return propertyServiceManager().getListenerPortRange();
    }

    public File toolkitxFile() {
        return new File(warHome(), "toolkitx");
    }
    public File schemaFile() {
        return new File(toolkitxFile(), "schema");
    }
    public File internalTestkitFile() {
        File testkit = propertyServiceManager().getTestkit();
        if (testkit != null) {
            return testkit;
        }
        testkit = new File(toolkitxFile(), "testkit");
        return testkit;
    }
    public File internalActorsDir() { return new File(toolkitxFile(), "actors"); }

    /**
     * This method return a list of testkit files sorted by priority level.
     * The first in the list is the top priority testkit.
     * It can contain a user testkit for an environment, an environment testkit (generated from code update)
     * and the toolkit default testkit.
     * It always contains at least the default testkit of the toolkit. The presence of the other
     * two depends on the existence.
     * @param environmentName name of the environment to look into for the environment specific testkits.
     * @param testSession name of the test session for the user specific testkit.
     * @return list of testkit files
     */
    public List<File> testkitFiles(String environmentName,TestSession testSession) {
        List<File> testkits=new ArrayList<File>();

        if (externalCache == null)
            throw new ToolkitRuntimeException("External Cache not configured");

        if (environmentName!=null) {
            // paths to the testkit repository in the environment directory
            File environmentTestkitsFile = new File(environmentFile(environmentName), "testkits");
            File usrTestkit=null;
            if (testSession!=null) {
                // path to the user's testkit (based on the name of the test session)
                usrTestkit = new File(environmentTestkitsFile, testSession.getValue());
            }else {
                logger.info("Mesa session name is null");
            }
            // path to the environment specific testkit (generated from Code Update)
            File environmentDefaultTestkit = new File(environmentTestkitsFile, "default");
            if (usrTestkit != null && usrTestkit.exists() && !testSession.equals(new TestSession("default")))
                testkits.add(usrTestkit);
            if (environmentDefaultTestkit.exists()) {
                testkits.add(environmentDefaultTestkit);
            }
        }else{
            logger.info("Environment name is null");
        }
        // toolkit default testkit
        if (!propertyServiceMgr.getIgnoreInternalTestkit())
            testkits.add(internalTestkitFile());
        return testkits;
    }

    public String defaultEnvironmentName() { return propertyServiceManager().getDefaultEnvironment(); }

    public File environmentFile(String envName) {
        return new File(externalCache + sep + "environment" + sep + envName);
    }

    public File environmentFile() {
        return new File(externalCache + sep + "environment");
    }

    public File getDefaultCodesFile() {
        return new File(environmentFile(defaultEnvironmentName()), "codes.xml");
    }

	public File getKeystoreDir(String environmentName) {
		return new File(environmentFile(environmentName), "keystore");
	}

	public File getKeystore(String environmentName) {
		return new File(getKeystoreDir(environmentName), "keystore");
	}

    public File getTruststore(String environmentName) {
        File f = new File(getKeystoreDir(environmentName), "truststore");
        if (f.exists()) {
            return f;
        }
        return getKeystore(environmentName);
    }

    public File getKeystorePropertiesFile(String environment) {
        File dir = getKeystoreDir(environment);
        return new File(dir, "keystore.properties");
    }

	public File getTruststorePropertiesFile(String environment) {
		File dir = getKeystoreDir(environment);
		File f   = new File(dir, "truststore.properties");
		if (f.exists()) {
		    return f;
        }
		return getKeystorePropertiesFile(environment);
	}


	public String getKeystorePassword(String environmentName) throws IOException {
		File propertiesFile = getKeystorePropertiesFile(environmentName);
		if (!propertiesFile.exists() || propertiesFile.isDirectory())
			return null;
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = Io.getInputStreamFromFile(propertiesFile);
			props.load(is);
		} finally {
			if (is!=null)
				is.close();
		}
		return props.getProperty("keyStorePassword");
	}

    public String getTruststorePassword(String environmentName) throws IOException {
        File propertiesFile = getTruststorePropertiesFile(environmentName);
        if (!propertiesFile.exists() || propertiesFile.isDirectory())
            return getKeystorePassword(environmentName);

        Properties props = new Properties();
        InputStream is = null;
        try {
            is = Io.getInputStreamFromFile(propertiesFile);
            props.load(is);
        } finally {
            if (is!=null)
                is.close();
        }
        return props.getProperty("trustStorePassword");
    }

	public String wikiBaseAddress() {
        return propertyServiceManager().getWikiBaseAddress();
    }

    // Default codes.xml to use if no environments are configured
    public File internalEnvironmentFile(String envName) {
        return new File(new File(toolkitxFile(), "environment"), envName);
    }

    public File internalEnvironmentsFile() {
        return new File(toolkitxFile(), "environment");
    }
    public File internalDatasetsFile() {
        return new File(toolkitxFile(), "datasets");
    }
    public File internalResourceCacheFile() {
        return new File(toolkitxFile(), "resourceCache");
    }

    public File sessionLogFile(String sessionId) {
        return new File(warHome + sep + "SessionCache" + sep + sessionId);
    }

    public File sessionCache() {
        return new File(warHome + sep + "SessionCache");
    }

    protected File testLogCacheDir() {
        return new File(externalCache + sep + "TestLogCache");
    }

    public File testLogCache() {
        return new File(externalCache + sep + "TestLogCache");
    }

    public File testLogCache(TestSession testSession) {
        return new File(testLogCache(), testSession.getValue());
    }

    public File orchestrationCache(TestSession testSession, String actorType) {
        return new File(new File(testLogCache(testSession), "orchestration"), actorType);
    }

    public File orchestrationPropertiesFile(TestSession testSession, String actorType) {
        File orchestrationCacheDir = orchestrationCache(testSession, actorType);
        File propFile = new File(orchestrationCacheDir, "orchestration.properties");
        return propFile;
    }

    public File imageCache(String cacheName) {
        return new File(externalCache + sep + "ImageCache" + sep + cacheName);
    }

    public File getInteractionSequencesFile() {
        return new File(toolkitxFile(), "interaction-sequences" + sep + "InteractionSequences.xml");
    }

    /*
        Return the Tool Tab Configuration file that is located in the default folder: $ToolkitFolder/tool-tab-configs.
        File name is created by a concatenation:  toolId + "Tabs.xml"
     */
    public File getToolTabConfigFile(String toolId) {
        if (toolId!=null)
            return new File(toolkitxFile(), "tool-tab-configs" + sep + toolId + "Tabs.xml");
        return null;
    }

    /*
        This method is called when the user wants to search for the Tool Tab Configuration file
        in the (current) environment folder of the external cache. If that file does not exist, then
        drop back and return the confguration file from the default folder.
        If successful, file path is: "$external_cache/$environment/tool-tab-configs/$toolId" + "Tabs.xml"
     */
    public File getToolTabConfigFile(String environment, String toolId) {
        if (environment == null || environment.equals("")) {
            return getToolTabConfigFile(toolId);
        }
        if (toolId!=null) {
            File environmentsRootFile=environmentFile();
            File f = new File(environmentFile().getAbsolutePath() + sep + environment + sep + "tool-tab-configs" + sep + toolId + "Tabs.xml");
            if (f.exists()) {
                return f;
            } else {
                return getToolTabConfigFile(toolId);
            }
        }
        return null;
    }

    /*
        This method is called when the user wants to search for the Tool Tab Configuration file
        in the test session folder of the external cache. If that file does not exist, then try
        looking in the environment folder.
        If successful, file path is: "$external_cache/$environment/testkits/$test_session/tool-tab-configs/$toolId" + "Tabs.xml"
     */

    public File getToolTabConfigFile(String environment, String testSession, String toolId) {
        if (environment == null || environment.equals("")) {
            return getToolTabConfigFile(testSession);
        }
        if (testSession == null || testSession.equals("")) {
            return getToolTabConfigFile(environment, toolId);
        }
        if (toolId!=null) {
            File environmentsRootFile=environmentFile();
            File f = new File(environmentFile().getAbsolutePath() + sep + environment + sep + "testkits" +sep + testSession + sep + "tool-tab-configs" + sep + toolId + "Tabs.xml");
            if (f.exists()) {
                return f;
            } else {
                return getToolTabConfigFile(environment, toolId);
            }
        }
        return null;
    }

    public static String defaultSessionName() { return "STANDALONE"; }
    public static String defaultServiceSessionName() { return "SERVICE"; }

    public static final String[] defaultAreas = new String [] { "tests", "testdata", "examples", "internal", "play",
            "selftest", "development", "utilities", "collection", "static.collections"};

    public static final String collectionsDirName = "collections";
    public static final String actorCollectionsDirName = "actorcollections";

    /**
     * This method returns all the existing testkits.
     * @return list of all existing testkits.
     */
    public List<File> getAllTestkits(){
        List<File> testkits=new ArrayList<File>();
        File environmentsRootFile=environmentFile();
        File[] envList=environmentsRootFile.listFiles();
        if (envList!=null) {
            for (File environment : envList) {
                File testkitsContainer = new File(environment, "testkits");
                if (testkitsContainer.exists()) {
                    testkits.addAll(Arrays.asList(testkitsContainer.listFiles()));
                }
            }
        }
        testkits.add(internalTestkitFile());
        return testkits;
    }

    public String getServletContextName() {
		return servletContextName;
	}

	public void setServletContextName(String servletContextName) {
		logger.info("ServletContext initialized to " + servletContextName);
		this.servletContextName = servletContextName;
	}

    public String getToolkitAsFhirServerBaseUrl() {
        return "http://" +
                propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getToolkitPort() +
                ((getServletContextName().isEmpty()) ? "" : "/" + getServletContextName() ) +
                "/fhir";
    }


    // I think this is wrong!!!
    @Obsolete
    public String getToolkitProxyBaseUrl() {
        return "http://" +
                propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getProxyPort() +
                ((getServletContextName().isEmpty()) ? "" : "/" + getServletContextName() ) +
                "/fhir";
    }


    public boolean testSessionExists(TestSession testSession) {
        return getTestSessions().contains(testSession);
    }

    public TestSession getDefaultTestSession() {
        String ts = propertyServiceManager().getDefaultTestSession();
        if (ts == null || ts.equals("")) return null;
        return new TestSession(ts);
    }
}
