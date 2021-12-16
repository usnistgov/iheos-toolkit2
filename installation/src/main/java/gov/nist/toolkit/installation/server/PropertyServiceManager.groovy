package gov.nist.toolkit.installation.server

import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.transform.TypeChecked
import java.util.logging.*

import java.nio.file.Paths

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

    static Logger logger = Logger.getLogger(PropertyServiceManager.class.getName());
	
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

	boolean getIgnoreInternalTestkit() {
		return getPropertyManager().ignoreInternalTestkit();
	}

	public boolean getArchiveLogs() {
		logger.fine(": getArchiveLogs");
		return getPropertyManager().archiveLogs();
	}

	public String getMSH3() {
		logger.fine(": " + "getMSH3");
		return getPropertyManager().getMSH3();
	}

	public String getMSH4() {
		logger.fine(": " + "getMSH4");
		return getPropertyManager().getMSH4();
	}

	public String getMSH5() {
		logger.fine(": " + "getMSH5");
		return getPropertyManager().getMSH5();
	}

	public String getMSH6() {
		logger.fine(": " + "getMSH6");
		return getPropertyManager().getMSH6();
	}

	public boolean getCacheDisabled() {
		logger.fine(": " + "getCacheDisabled");
		return "true".equals(getPropertyManager().getCacheDisabled());
	}

	public String getAdminPassword() {
//		logger.fine(": " + "getAdminPassword");
		return getPropertyManager().getPassword();
	}

	public String getToolkitHost() {
//		logger.fine(": " + "getToolkitHost");
		return getPropertyManager().getToolkitHost();
	}

	public boolean isUsingSSL() {
		return getPropertyManager().isUsingSSL();
	}

	public String getSSLPort() {
		return getPropertyManager().getSSLPort();
	}

	public String getToolkitPort() {
//		logger.fine(": " + "getToolkitPort");
        if (overrideToolkitPort != null) {
//            logger.info("Overriding toolkit port -> " + overrideToolkitPort);
            return overrideToolkitPort;
        }
		return getPropertyManager().getToolkitPort();
	}

	public String getToolkitTlsPort() {
//		logger.fine(": " + "getToolkitTlsPort");
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

	String getStsActorName() {
		getPropertyManager().stsActorName
	}

	String getStsTpName() {
		getPropertyManager().stsTpName
	}

	int getNonceSize() {
		getPropertyManager().nonceSize
	}

	String getGazelleTestingSession() {
		getPropertyManager().gazelleTestingSession
	}

	public List<String> getListenerPortRange() {
//		logger.fine(": " + "getListenerPortRange");
		return getPropertyManager().getListenerPortRange();
	}

	public boolean getAutoInitializeConformanceTool() {
		return getPropertyManager().getAutoInitializeConformanceTool();
	}

	public String getToolkitEnableAllCiphers() {
		logger.fine(": " + "getToolkitEnableAllCiphers");
		return getPropertyManager().getToolkitEnableAllCiphers();
	}

	public File getActorsFileName() {
		logger.fine(": " + "getActorsFileName");
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
		logger.fine(": " + "getDefaultEnvironmentName");
		return getPropertyManager().getDefaultEnvironmentName();
	}

	public String getDefaultAssigningAuthority() {
		logger.fine(": " + "getDefaultAssigningAuthority");
		return getPropertyManager().getDefaultAssigningAuthority();
	}

	public String getDefaultTestSession() {
		return getPropertyManager().getDefaultTestSession();
	}

	public boolean getDefaultTestSessionIsProtected() {
		return getPropertyManager().getDefaultTestSessionIsProtected();
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

		String toolkitPropertiesPath = System.getProperty("TOOLKIT_PROPERTIES");
		if (toolkitPropertiesPath == null) {
			toolkitPropertiesPath = "/etc/toolkit/toolkit.properties";
		}

		// Create a File from the properties file in order to pass it to v3
//		assert Installation.instance().warHome()
		File externalPropertiesFile = new File(toolkitPropertiesPath);
		String location;
		try {
			if (externalPropertiesFile.exists()) {
				location = toolkitPropertiesPath;
			} else {
				location = Paths.get(getClass().getResource('/').toURI()).resolve('toolkit.properties').toFile()
			}
			location = location.replaceAll("%20", " ");
			logger.fine("*** getting toolkit.properties file:" + location);

			setPropertiesFile(location);

		} catch (Throwable t) {
			t.printStackTrace();
		}

		logger.info("Property manager initialized from " + location);
		propertyManager = new PropertyManager(location);

        /*
		Detect proper External Cache location using the following logic:

		SP[1] | ECPath Exists | SP[2] | Result
		N       Y       N       External_Cache from the default toolkit.properties is used.
		N       N       N       Bogus ECPath means user needs to update the External Cache manually using the Tool Configuration UI tool page. (This instruction is printed in the startup log message.)
		N       N       Y*       The External_Cache from the system property is used and the default toolkit.properties file ExternalCache property is updated to this value.
		Y       Y       N       External_Cache from the SP[1] toolkit.properties is used.
		Y       N       Y*       The External_Cache from the SP[2] is used and the SP[1] toolkit.properties file ExternalCache property is updated to this value.
		Y       Y       Y*       External_Cache from the SP[1] toolkit.properties is used. SP[2] is ignored. The reason being External_Cache should always be editable from the Toolkit UI, and when the user changes this property the intention is to use that value from that point on. Hence, SP[2] is just a bootstrap to use the testing-tools\external_cache.

		Legend
		SP[1] = Java System Property -DTOOLKIT_PROPERTIES is Present
		ECPath Exists =  Toolkit.properties ExternalCache Java File Path exists (could be either of the toolkit.properties file from SP[1] or the default toolkit.properties depending on the presence of SP[1])
		SP[2] = Java System Property -DEXTERNAL_CACHE is Present
		* = This part of the code
		SP[2] N = Existing case, no need for updates.
         */
		try {
			String ecString = propertyManager.getExternalCache()
			if (ecString != null) {
				File ecFile = new File(ecString)
				if (! ecFile.exists()) {
					logger.info("External_Cache Directory does not exist.")
					logger.info("Checking for EXTERNAL_CACHE System Property ...")
					String ecSpString = System.getProperty("EXTERNAL_CACHE")
					if (ecSpString != null) {
						File ecSpFile = new File(ecSpString)
						if (ecSpFile.exists()) {
							propertyManager.setExternalCache(ecSpString)
							propertyManager.saveProperties()
							logger.info("Updated toolkit.properties using EXTERNAL_CACHE System Property: " + ecSpString)
						} else {
							logger.info("EXTERNAL_CACHE Directory as specified by the System Property does not exist.")
						}
					} else {
                        // Unresolved EXTERNAL_CACHE: All options exhausted.
						logger.info("User needs to manually configure the EXTERNAL_CACHE in the Toolkit Configuration UI.")
					}
				}
			}
		} catch(IOException ioex) {
			logger.severe(ioex.toString())
		}

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
            logger.severe(excuse);
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
			logger.warning(msg);
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
			logger.severe(msg);
			throw new Exception(msg);
		}
		return f;
	}

	public Map<String, String> getToolkitProperties() {
		logger.fine(": " + "getToolkitProperties");
		return getPropertyManager().getPropertyMap();
	}

	public boolean reloadPropertyFile() {
		logger.fine(": " + "reloadPropertyFile");
		propertyManager = null;
		getPropertyManager();
		return true;
	}


	public boolean isTestLogCachePrivate() {
		return true;
	}

	public String getImplementationVersion() {
		logger.fine(": " + "getImplementationVersion");
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
		logger.fine(": " + "getDefaultEnvironment()");
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
