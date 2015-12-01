package gov.nist.toolkit.installation;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PropertyServiceManager  /*extends CommonServiceManager*/ {
	PropertyManager propertyManager = null;
	File warHome = null;
    String TOOLKIT_PROPERTIES_PATH = "";
    File propertiesFile = null;

	static Logger logger = Logger.getLogger(PropertyServiceManager.class);
	
	public PropertyServiceManager(File warHome)  {
		this.warHome = warHome;
	}

	// this was removed force lookup through Installation.  Makes writing unit tests easier
//	private File getActorsDirName() {
//		File f = new File(getPropertyManager().getExternalCache() + File.separator + "actors");
//		f.mkdirs();
//		return f;
//	}

	// isRead - is the actors file about to be read? (as opposed to written)
	public File configuredActorsFile(boolean isRead) throws IOException {
		File loc = getActorsFileName();
		if (isRead) {
			if (!loc.canRead())
				return null;
			return loc;
		}
		if (!isRead && (loc.canWrite() || loc.createNewFile()))
			return loc;
		return null;
	}

	public boolean getCacheDisabled() {
		logger.debug(": " + "getCacheDisabled");
		return "true".equals(getPropertyManager().getCacheDisabled());
	}

	public String getAdminPassword() {
		logger.debug(": " + "getAdminPassword");
		return getPropertyManager().getPassword();
	}

	public String getToolkitHost() {
		logger.debug(": " + "getToolkitHost");
		return getPropertyManager().getToolkitHost();
	}

	public String getToolkitPort() {
		logger.debug(": " + "getToolkitPort");
		return getPropertyManager().getToolkitPort();
	}

	public String getToolkitTlsPort() {
		logger.debug(": " + "getToolkitTlsPort");
		return getPropertyManager().getToolkitTlsPort();
	}

	public List<String> getListenerPortRange() {
		logger.debug(": " + "getListenerPortRange");
		return getPropertyManager().getListenerPortRange();
	}

	public String getToolkitEnableAllCiphers() {
		logger.debug(": " + "getToolkitEnableAllCiphers");
		return getPropertyManager().getToolkitEnableAllCiphers();
	}

	public File getActorsFileName() {
		logger.debug(": " + "getActorsFileName");
		return new File(Installation.installation().externalCache() + File.separator + "actors.xml");
	}

    public File getTestkit() {
        String x = getPropertyManager().getTestkit();
        if (x == null) return null;
        File testkit = new File(x);
        if (testkit.exists() && testkit.isDirectory()) return testkit;
        return null;
    }


//	// This now pulls from Installation so that external cache location can be overridden
//	public File getSimDbDir() {
//		logger.debug(": " + "getSimDbDir");
//		File f = new File(getPropertyManager().getExternalCache() + File.separator + "simdb");
////		File f = new File(Installation.installation().externalCache() + File.separator + "simdb");
//		f.mkdirs();
//		return f;
//	}

	public String getDefaultEnvironmentName() {
		logger.debug(": " + "getDefaultEnvironmentName");
		return getPropertyManager().getDefaultEnvironmentName();
	}

	public String getDefaultAssigningAuthority() {
		logger.debug(": " + "getDefaultAssigningAuthority");
		return getPropertyManager().getDefaultAssigningAuthority();
	}

	public PropertyManager getPropertyManager() {
		loadPropertyManager();
		return propertyManager;
	}
	
	public void loadPropertyManager() {
		if (propertyManager != null)
			return;

        // Create a File from the properties file in order to pass it to v3
        TOOLKIT_PROPERTIES_PATH = warHome + File.separator + "WEB-INF" + File.separator + "toolkit.properties";
        setPropertiesFile(TOOLKIT_PROPERTIES_PATH);

        propertyManager = new PropertyManager(TOOLKIT_PROPERTIES_PATH);

		// This removes the dependency that 
		// gov.nist.registry.common2.xml.SchemaValidation
		// has on port 9080
		// Schema references will be made directly through the file system and not
		// via "system" references (via a URI)
		System.setProperty("XDSSchemaDir", warHome + File.separator + "toolkitx" + File.separator + "schema");
	}


	public File internalActorsFile() {
		return new File(warHome + File.separator + 
				"toolkitx" + File.separator + "xdstest" + File.separator + "actors.xml");
	}


	public File getTestLogCache() throws IOException {
		File testLogCache = Installation.installation().testLogCache();
		File f;
		
//		// internal is obsolete
//		if ("internal".equals(testLogCache)) {
//			testLogCache = getWarHome() + "SessionCache" + 
//					File.separator +
//					getSession().getId() + File.separator + 
//					"TestLog";
//			f = new File(testLogCache);
//			f.mkdirs();
//			return f;
//		}

		f = testLogCache;

        // First make sure EC is workable
        String excuse = ExternalCacheManager.validate();
        if (excuse != null) {
            logger.error(excuse);
            throw new IOException(excuse);
        }

		if (!( f.exists() && f.isDirectory() && f.canWrite()  )) {
			String msg = "Cannot access Test Log Cache [" + testLogCache + "] - either it doesn't exist, isn't a directory or isn't writable. " +
                    "Open Toolkit Configuration, edit External Cache location (if necessary) and save. If your External Cache location is ok " +
                    " you may only need to update your External Cache.  The SAVE will do that update.";
			logger.warn(msg);
			throw new IOException(msg);
		}

		return f;
	}
	
	public String getAttributeValue(String username, String attName) throws Exception {
		return Io.stringFromFile(getAttributeFile(username, attName));
	}
	
	public void setAttributeValue(String username, String attName, String attValue) throws Exception {
		Io.stringToFile(getAttributeFile(username, attName), attValue);
	}
	
	File getAttributeFile(String username, String attName) throws Exception {
		return new File(getAttributeCache(username) + File.separator + attName + ".txt");
	}
	
	File getAttributeCache(String username) throws Exception {
		String attributeCache = Installation.installation().externalCache() + File.separator + "Attributes" + File.separator + username;
		File f = new File(attributeCache);

		if (!( f.exists() && f.isDirectory() && f.canWrite()  )) {
			String msg = "Cannot access Attribute Cache [" + attributeCache + "] - either it doesn't exist, isn't a directory or isn't writable";
			logger.fatal(msg);
			throw new Exception(msg);
		}
		return f;
	}

	public Map<String, String> getToolkitProperties() {
		logger.debug(": " + "getToolkitProperties");
		return getPropertyManager().getPropertyMap();
	}




	public boolean reloadPropertyFile() {
		logger.debug(": " + "reloadPropertyFile");
		propertyManager = null;
		getPropertyManager();
		return true;
	}


	public boolean isTestLogCachePrivate() {
		return true;
//		String testLogCache = getPropertyManager().getExternalCache() + File.separator + "TestLogCache";
//		return !"internal".equals(testLogCache);
	}

	public String getImplementationVersion() {
		logger.debug(": " + "getImplementationVersion");

		File f = new File(warHome + File.separator + "build.num");
		String ver = null;
		try {
			ver = Io.stringFromFile(f);
		} catch (IOException e) {
			System.out.println(ExceptionUtil.exception_details(e));
		}
		return ver.substring(ver.indexOf("\n"));
	}

//	public boolean isGazelleConfigFeedEnabled() {
//		logger.debug(": " + "isGazelleConfigFeedEnabled");
//		return SiteServiceManager.getSiteServiceManager().useGazelleConfigFeed(session);
//	}

//	public String getInitParameter(String parmName) {
//		logger.debug(getSessionIdIfAvailable() + ": " + "getInitParameter(" + parmName + ")");
//		return tsi.servletContext().getInitParameter(parmName);
//	}

//	public List<String> getEnvironmentNames() {
//		logger.debug(": " + "getEnvironmentNames()");
//		return getSession().getEnvironmentNames();
//	}

//	/**
//	 * Set environment name for current session
//	 * @param name
//	 * @throws
//	 */
//	public void setEnvironment(String name)  {
//		logger.debug(": " + "setEnvironment(" + name + ")");
//		getSession().setEnvironment(name);
//	}

//	public String getCurrentEnvironment() {
//		return getPropertyManager().getCurrentEnvironmentName();
//	}

	public String getDefaultEnvironment() {
		logger.debug(": " + "getDefaultEnvironment()");
		return getPropertyManager().getDefaultEnvironmentName();
	}

    /**
     * Create a properties File based on v2 toolkit properties file location.
     * @param path
     */
    public void setPropertiesFile(String path) {
        propertiesFile = new File(path);
    }

    /**
     * Getter used by v3 to obtain the Toolkit Properties file
     * @return the toolkit properties file located in v2
     */
    public File getPropertiesFile(){
        return propertiesFile;
    }


//	public void setSessionProperties(Map<String, String> props) {
//		logger.debug(": " + "setSessionProperties()");
//		Session s = getSession();
//		if (s == null)
//			return;
//		s.setSessionProperties(props);
//	}
}
