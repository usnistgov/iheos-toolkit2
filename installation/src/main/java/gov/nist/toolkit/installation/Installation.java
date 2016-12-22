package gov.nist.toolkit.installation;


import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class Installation {
    File warHome = null;
    File externalCache = null;
    String sep = File.separator;
    public TkProps tkProps = new TkProps();
	String servletContextName = "/xdstools2";

    public final static String DEFAULT_ENVIRONMENT_NAME = "default";
    private final static Logger LOGGER=Logger.getLogger(Installation.class.getName());

    PropertyServiceManager propertyServiceMgr = null;
    static Logger logger = Logger.getLogger(Installation.class);

    static Installation me = new Installation();

    public String toString() {
        return String.format("warHome=%s externalCache=%s", warHome, externalCache);
    }

    static {
        // This works for unit tests if warhome.txt is installed as part of a unit test environment
        String warhomeTxt = null;
        try {
            warhomeTxt = instance().getClass().getResource("/warhome/warhome.txt").getFile();
        } catch (Throwable t) {}
        if (warhomeTxt != null) {
            instance().warHome(new File(warhomeTxt).getParentFile());
        }
    }

    static public Installation instance() {
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
            String warTxt = null;
            try {
                warTxt = instance().getClass().getResource("/war/war.txt").getFile();
            } catch (Throwable t) {}
            if (warTxt != null) {
                instance().warHome(new File(warTxt).getParentFile());
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
            logger.error(ExceptionUtil.here("warhome is null"));
        this.warHome = warHome;
        propertyServiceMgr = null;
        propertyServiceManager();  // initialize
        String ec = propertyServiceManager().getPropertyManager().getExternalCache();

        logger.info("External Cache as reported by toolkit.properties");
		if (externalCache == null) { // this can be different in a unit test situation
            externalCache = new File(ec);
        }
		logger.info("Installation: External Cache set to " + externalCache);
        logger.info("Toolkit running at " + propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getToolkitPort());
    }

	public File externalCache() { return externalCache; }
	protected void externalCache(File externalCache) {
			this.externalCache = externalCache;
        logger.info("Installation: External Cache set to " + externalCache);
		try {
			tkProps = TkLoader.tkProps(instance().getTkPropsFile()); //TkLoader.tkProps(new File(Installation.instance().externalCache() + File.separator + "tk_props.txt"));
		} catch (Exception e) {
//			logger.warn("Cannot load tk_props.txt file from External Cache");
            tkProps = new TkProps();
        }

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
        File f = new File(externalCache() + File.separator + "actors");
        f.mkdirs();
        return f;
    }

    /**
    * @return a {@link File} object representing the simdb directory, that is,
    * the directory in which information for simulators is maintained on disc.
    */
   public File simDbFile() {
        return new File(externalCache(), "simdb");
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
     * @param mesaSessionName name of the test session for the user specific testkit.
     * @return list of testkit files
     */
    public List<File> testkitFiles(String environmentName,String mesaSessionName) {
        List<File> testkits=new ArrayList<File>();
        if (environmentName!=null) {
            // paths to the testkit repository in the environment directory
            File environmentTestkitsFile = new File(environmentFile(environmentName), "testkits");
            File usrTestkit=null;
            if (mesaSessionName!=null) {
                // path to the user's testkit (based on the name of the test session)
                usrTestkit = new File(environmentTestkitsFile, mesaSessionName);
            }else {
                LOGGER.info("Mesa session name is null");
            }
            // path to the environment specific testkit (generated from Code Update)
            File environmentDefaultTestkit = new File(environmentTestkitsFile, "default");
            if (usrTestkit != null && usrTestkit.exists() && !mesaSessionName.equals("default"))
                testkits.add(usrTestkit);
            if (environmentDefaultTestkit.exists()) {
                testkits.add(environmentDefaultTestkit);
            }
        }else{
            LOGGER.info("Environment name is null");
        }
        // toolkit default testkit
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

	public File getKeystoreDir(String environmentName) {
		return new File(environmentFile(environmentName), "keystore");
	}

	public File getKeystore(String environmentName) {
		return new File(getKeystoreDir(environmentName), "keystore");
	}

	public File getKeystorePropertiesFile(String environment) {
		File dir = getKeystoreDir(environment);
		return new File(dir, "keystore.properties");
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

    // Default codes.xml to use if no environments are configured
    public File internalEnvironmentFile(String envName) {
        return new File(new File(toolkitxFile(), "environment"), envName);
    }

    public File internalEnvironmentsFile() {
        return new File(toolkitxFile(), "environment");
    }

    public File sessionLogFile(String sessionId) {
        return new File(warHome + sep + "SessionCache" + sep + sessionId);
    }

    public File sessionCache() {
        return new File(warHome + sep + "SessionCache");
    }

    public File testLogCache() {
        return new File(externalCache + sep + "TestLogCache");
    }

    public File testLogCache(String testSessionName) {
        return new File(testLogCache(), testSessionName);
    }

    public File orchestrationCache(String testSessionName, String actorType) {
        return new File(new File(testLogCache(testSessionName), "orchestration"), actorType);
    }

    public File orchestrationPropertiesFile(String testSessionName, String actorType) {
        File orchestrationCacheDir = orchestrationCache(testSessionName, actorType);
        File propFile = new File(orchestrationCacheDir, "orchestration.properties");
        return propFile;
    }

    public File imageCache(String cacheName) {
        return new File(externalCache + sep + "ImageCache" + sep + cacheName);
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

}
