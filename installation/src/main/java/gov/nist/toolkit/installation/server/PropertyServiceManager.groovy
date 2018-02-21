package gov.nist.toolkit.installation.server

import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

@TypeChecked
public class PropertyServiceManager {
	PropertyManager propertyManager = null;
//	File warHome = null;
//    String TOOLKIT_PROPERTIES_PATH = "";
    File propertiesFile = null;
    String overrideToolkitPort = null;

    public void setOverrideToolkitPort(String overrideToolkitPort) {
        logger.info("Override toolkit port to " + overrideToolkitPort);
        this.overrideToolkitPort = overrideToolkitPort;
    }

    static Logger logger = Logger.getLogger(PropertyServiceManager.class);
	
//	public PropertyServiceManager(File warHome)  {
//		this.warHome = warHome;
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

	public boolean getArchiveLogs() {
		logger.debug(": getArchiveLogs");
		return getPropertyManager().archiveLogs();
	}

	public String getMSH3() {
		logger.debug(": " + "getMSH3");
		return getPropertyManager().getMSH3();
	}

	public String getMSH4() {
		logger.debug(": " + "getMSH4");
		return getPropertyManager().getMSH4();
	}

	public String getMSH5() {
		logger.debug(": " + "getMSH5");
		return getPropertyManager().getMSH5();
	}

	public String getMSH6() {
		logger.debug(": " + "getMSH6");
		return getPropertyManager().getMSH6();
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
//		logger.debug(": " + "getToolkitHost");
		return getPropertyManager().getToolkitHost();
	}

	public boolean isUsingSSL() {
		return getPropertyManager().isUsingSSL();
	}

	public String getSSLPort() {
		return getPropertyManager().getSSLPort();
	}

	public String getToolkitPort() {
//		logger.debug(": " + "getToolkitPort");
        if (overrideToolkitPort != null) {
//            logger.info("Overriding toolkit port -> " + overrideToolkitPort);
            return overrideToolkitPort;
        }
		return getPropertyManager().getToolkitPort();
	}

	public String getToolkitTlsPort() {
//		logger.debug(": " + "getToolkitTlsPort");
		return getPropertyManager().getToolkitTlsPort();
	}

	boolean isSingleUserMode() {
		!isMultiuserMode() && !isCasMode()
	}

	boolean isMultiuserMode() {
		getPropertyManager().multiuserMode
	}

	boolean isCasMode() {
		getPropertyManager().casMode
	}

	int getNonceSize() {
		getPropertyManager().nonceSize
	}

	String getGazelleTestingSession() {
		getPropertyManager().gazelleTestingSession
	}

	public List<String> getListenerPortRange() {
//		logger.debug(": " + "getListenerPortRange");
		return getPropertyManager().getListenerPortRange();
	}

	public boolean getAutoInitializeConformanceTool() {
		return getPropertyManager().getAutoInitializeConformanceTool();
	}

	public String getToolkitEnableAllCiphers() {
		logger.debug(": " + "getToolkitEnableAllCiphers");
		return getPropertyManager().getToolkitEnableAllCiphers();
	}

	public File getActorsFileName() {
		logger.debug(": " + "getActorsFileName");
		return new File(Installation.instance().externalCache(), "actors.xml");
	}

    public File getTestkit() {
        String x = getPropertyManager().getTestkit();
        if (x == null) return null;
        File testkit = new File(x);
        if (testkit.exists() && testkit.isDirectory()) return testkit;
        return null;
    }


	public String getDefaultEnvironmentName() {
		logger.debug(": " + "getDefaultEnvironmentName");
		return getPropertyManager().getDefaultEnvironmentName();
	}

	public String getDefaultAssigningAuthority() {
		logger.debug(": " + "getDefaultAssigningAuthority");
		return getPropertyManager().getDefaultAssigningAuthority();
	}

	public String getProxyPort() {
		return getPropertyManager().getProxyPort();
	}

	public PropertyManager getPropertyManager() {
		loadPropertyManager();
		return propertyManager;
	}

	public void loadPropertyManager(File toolkitPropertiesFile){
		logger.info("Property manager initialized from " + toolkitPropertiesFile);
		propertyManager = new PropertyManager(toolkitPropertiesFile.toString())
	}

	public void loadPropertyManager() {
		if (propertyManager != null)
			return;

		// Create a File from the properties file in order to pass it to v3
//		assert Installation.instance().warHome()
		File propPath = null;
		String location;
		try {
			URL url = getClass().getResource("/toolkit.properties");
			location = url.getFile();
			location = location.replaceAll("%20", " ");
			logger.debug("*** getting toolkit.properties file:" + location);

			setPropertiesFile(location);

		} catch (Throwable t) {
			t.printStackTrace();
		}

		logger.info("Property manager initialized from " + location);
		propertyManager = new PropertyManager(location);

		// This removes the dependency that
		// gov.nist.registry.common2.xml.SchemaValidation
		// has on port 9080
		// Schema references will be made directly through the file system and not
		// via "system" references (via a URI)
//		System.setProperty("XDSSchemaDir", new File(new File(Installation.instance().warHome(), "toolkitx"), "schema").toString());
	}

	public File internalActorsFile() {
        assert Installation.instance().warHome()
		return new File(new File(new File(Installation.instance().warHome(), "toolkitx"),  "xdstest"), "actors.xml");
	}


	public File getTestLogCache() throws IOException {
		File testLogCache = Installation.instance().testLogCacheDir();
		File f;
		
		f = testLogCache;

        // First make sure EC is workable
        String excuse = ExternalCacheManager.validate();
        if (excuse != null) {
            logger.error(excuse);
            throw new IOException(excuse);
        }

		if (!( f.exists() && f.isDirectory() && f.canWrite()  )) {
            // first try initializing it
            f.mkdirs();
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
		return new File(getAttributeCache(username), attName + ".txt");
	}
	
	File getAttributeCache(String username) throws Exception {
		String attributeCache = new File(new File(Installation.instance().externalCache(), "Attributes"), username);
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
	}

	public String getImplementationVersion() {
		logger.debug(": " + "getImplementationVersion");
        assert Installation.instance().warHome()
		File f = new File(Installation.instance().warHome(), "build.num");
		String ver = null;
		try {
			ver = Io.stringFromFile(f);
		} catch (IOException e) {
			System.out.println(ExceptionUtil.exception_details(e));
		}
		return ver.substring(ver.indexOf("\n"));
	}

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

    public String getWikiBaseAddress ( ) {
    	return getPropertyManager().getWikiBaseAddress() ;
    }
}
