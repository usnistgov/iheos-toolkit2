package gov.nist.toolkit.installation;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class PropertyServiceManager  /*extends CommonServiceManager*/ {
	PropertyManager propertyManager = null;
	File warHome = null;
	
	static Logger logger = Logger.getLogger(PropertyServiceManager.class);
	
	public PropertyServiceManager(File warHome)  {
		this.warHome = warHome;
	}

	public File getActorsDirName() {
		File f = new File(getPropertyManager().getExternalCache() + File.separator + "actors");
		f.mkdirs();
		return f;
	}

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

	public String getToolkitEnableAllCiphers() {
		logger.debug(": " + "getToolkitEnableAllCiphers");
		return getPropertyManager().getToolkitEnableAllCiphers();
	}

	public File getActorsFileName() {
		logger.debug(": " + "getActorsFileName");
		return new File(getPropertyManager().getExternalCache() + File.separator + "actors.xml");
	}

	public File getSimDbDir() {
		logger.debug(": " + "getSimDbDir");
		File f = new File(getPropertyManager().getExternalCache() + File.separator + "simdb");
		f.mkdirs();
		return f;
	}

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

		propertyManager = new PropertyManager(warHome + File.separator + "WEB-INF" + File.separator + "toolkit.properties");

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
		String testLogCache = getPropertyManager().getExternalCache() + File.separator + "TestLogCache";
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

		f = new File(testLogCache);
		f.mkdirs();

		if (!( f.exists() && f.isDirectory() && f.canWrite()  )) {
			String msg = "Cannot access Test Log Cache [" + testLogCache + "] - either it doesn't exist, isn't a directory or isn't writable";
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
		String attributeCache = getPropertyManager().getExternalCache() + File.separator + "Attributes" + File.separator + username;
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

	/**
	 * Set environment name for current session
	 * @param name
	 * @throws
	 */
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

//	public void setSessionProperties(Map<String, String> props) {
//		logger.debug(": " + "setSessionProperties()");
//		Session s = getSession();
//		if (s == null)
//			return;
//		s.setSessionProperties(props);
//	}
}
