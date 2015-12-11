package gov.nist.toolkit.installation;


import gov.nist.toolkit.tk.TkLoader;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

public class Installation {
	File warHome = null;
	File externalCache = null;
	String sep = File.separator;
	public TkProps tkProps = new TkProps();

    public final static String DEFAULT_ENVIRONMENT_NAME = "default";

	PropertyServiceManager propertyServiceMgr = null;
	static Logger logger = Logger.getLogger(Installation.class);

	static Installation me = null;

    static {
        // This works for unit tests if warhome.txt is installed as part of a unit test environment
        String warhomeTxt = null;
        try {
            warhomeTxt = installation().getClass().getResource("/warhome/warhome.txt").getFile();
        } catch (Throwable t) {}
        if (warhomeTxt != null) {
            installation().warHome = new File(warhomeTxt).getParentFile();
        }
    }

	static public Installation installation() {
		if (me == null)
			me = new Installation();
		return me;
	}
	
	static public Installation installation(ServletContext servletContext) {
		if (me == null)
			me = new Installation();
		if (me.warHome == null)
			me.warHome = new File(servletContext.getRealPath("/"));
		return me;
	}

	private Installation() {
        logger.info(String.format("Installation rooted at %s", toString()));
    }
	
	public File warHome() { 
		return warHome; 
		}
	synchronized public void warHome(File warHome) {
		if (warHome()!=null /* && warHome().equals(warHome) */) {
            logger.info("... oops - warHome already initialized");
            return; /* already set */
        }
		logger.info("V2 - Installation - war home set to " + warHome);
        if (warHome == null)
            logger.error(ExceptionUtil.here("warhome is null"));
		this.warHome = warHome;
		propertyServiceMgr = null;
		if (externalCache == null) // this can be different in a unit test situation
			externalCache = new File(propertyServiceManager().getPropertyManager().getExternalCache());
        logger.info("Toolkit running at " + propertyServiceManager().getToolkitHost() + ":" + propertyServiceManager().getToolkitPort());
	}

	public File externalCache() { return externalCache; }
	protected void externalCache(File externalCache) {
			this.externalCache = externalCache;
        logger.info("V2 Installation: External Cache set to " + externalCache);
		try {
			tkProps = TkLoader.tkProps(installation().getTkPropsFile()); //TkLoader.tkProps(new File(Installation.installation().externalCache() + File.separator + "tk_props.txt"));
		} catch (Exception e) {
//			logger.warn("Cannot load tk_props.txt file from External Cache");
			tkProps = new TkProps();
		}

	}

	public File getTkPropsFile() {
		return new File(Installation.installation().externalCache() + File.separator + "tk_props.txt");
	}
	
	public boolean initialized() { return warHome != null && externalCache != null; }
	
	public PropertyServiceManager propertyServiceManager() {
		if (propertyServiceMgr == null)
			propertyServiceMgr = new PropertyServiceManager(warHome);
		return propertyServiceMgr;
	}

	public File getActorsDirName() {
		File f = new File(externalCache() + File.separator + "actors");
		f.mkdirs();
		return f;
	}

	public File simDbFile() {
		return new File(externalCache(), "simdb");
//		return propertyServiceManager().getSimDbDir();
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
	public File testkitFile() {
        File testkit = propertyServiceManager().getTestkit();
        if (testkit != null) {
            logger.info(String.format("Testkit source is %s", testkit));
            return testkit;
        }
        testkit = new File(toolkitxFile(), "testkit");
        logger.info(String.format("Testkit source is %s", testkit));
        return testkit;
    }

    public String defaultEnvironmentName() { return propertyServiceManager().getDefaultEnvironment(); }
	
	public File environmentFile(String envName) {
		return new File(externalCache + sep + "environment" + sep + envName);
	}

	public File environmentFile() {
		return new File(externalCache + sep + "environment");
	}

	// Default codes.xml to use if no environments are configured
	public File internalEnvironmentFile(String envName) {
		return new File(new File(toolkitxFile(), "environment"), envName);
	}

    public File internalEnvironmentsFile() {
        return new File(toolkitxFile(), "environment");
    }

//    public File directSendLogFile(String userName) {
//		return new File(externalCache + sep + "direct" + sep + "sendlog" + sep + userName);
//	}

//	public File directSendLogs() {
//		return new File(externalCache + sep + "direct" + sep + "sendlog");
//	}

//	public File directLogFile(String userName) {
//		return new File(externalCache + sep + "direct" + sep + "direct-logs" + sep + userName);
//	}

//	public File directLogs() {
//		return new File(externalCache + sep + "direct" + sep + "direct-logs");
//	}

	public File sessionLogFile(String sessionId) {
		return new File(warHome + sep + "SessionCache" + sep + sessionId);
	}

	public File sessionCache() {
		return new File(warHome + sep + "SessionCache");
	}

	public File testLogCache() {
		return new File(externalCache + sep + "TestLogCache");
	}

	public String defaultSessionName() { return "STANDALONE"; }
    public String defaultServiceSessionName() { return "SERVICE"; }

	/**
	 * Queries the PropertyServiceManager to retrieve the Toolkit Properties as a File.
	 * This function is called from within v3.
	 * @return the toolkit properties file
	 */
//	public File getToolkitPropertiesFile(){
//		return propertyServiceMgr.getPropertiesFile();
//	}
}
