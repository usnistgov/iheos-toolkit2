package gov.nist.toolkit.installation.server;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class PropertyManager {

	static private Logger logger = Logger.getLogger(PropertyManager.class);

	static private final String ADMIN_PASSWORD      = "Admin_password";
	static private final String TOOLKIT_HOST        = "Toolkit_Host";
	static private final String TOOLKIT_PORT        = "Toolkit_Port";
	static private final String TOOLKIT_TLS_PORT    = "Toolkit_TLS_Port";
	static private final String GAZELLE_CONFIG_URL  = "Gazelle_Config_URL";
	static private final String EXTERNAL_CACHE      = "External_Cache";
	static private final String USE_ACTORS_FILE     = "Use_Actors_File";
	static public  final String ENABLE_SAML			= "Enable_SAML";
	static private final String TESTKIT             = "Testkit";
	static private final String LISTENER_PORT_RANGE = "Listener_Port_Range";
	static private final String AUTO_INIT_CONFORMANCE_TOOL = "Auto_init_conformance_tool";
	static private final String MSH_3 = "MSH_3";
	static private final String MSH_4 = "MSH_4";
	static private final String MSH_5 = "MSH_5";
	static private final String MSH_6 = "MSH_6";
	static private final String ARCHIVE_LOGS = "Archive_Logs";
	static private final String MULTIUSER_MODE = "Multiuser_mode";
	static private final String CAS_MODE = "Cas_mode";
	static private final String NONCE_SIZE = "Nonce_size";
	static private final String GAZELLE_TESTING_SESSION = "Gazelle_testing_session";


	private String propFile;
	private Properties toolkitProperties = null;

	public PropertyManager(String propFile) {
		this.propFile = propFile;
	}

	public void update(Map<String, String> props) throws Exception {
		if (toolkitProperties == null)
			toolkitProperties = new Properties();
		for (String key : props.keySet()) {
			String value = props.get(key);
			validateProperty(key, value);
			toolkitProperties.put(key, value);
			save(props);
		}
	}

	private void validateProperty(String name, String value) throws Exception {
		if (name == null)
			throw new Exception("Property with name null not allowed");
		if (name.equals(ADMIN_PASSWORD)) {
			if (value == null || value.equals(""))
				throw new Exception("Empty password not allowed");
		}
		else if (name.equals("Actors_file")) {
			File f = new File(value);
			if (f.exists() && f.canWrite())
				return;
			File dir = f.getParentFile();
			dir.mkdirs();
			if (!dir.exists())
				throw new Exception("Cannot create directory for actors.xml file: " + dir.toString());
			if (!f.exists()) {
				f.createNewFile();
				if (f.exists() && f.canWrite()) {
					f.delete();
					return;
				}
				f.delete();
			}
			throw new Exception("Cannot create actors.xml file at: " + f);
		}
		else if (name.equals("Simulator_database_directory")) {
			File f = new File(value);
			f.mkdirs();
			if (!f.exists())
				throw new Exception("Cannot create Message_database_directory " + value);
		}
	}

	public boolean archiveLogs() {
		loadProperties();
		String value = (String) toolkitProperties.get(ARCHIVE_LOGS);
		if (value == null) return false;
		return value.toLowerCase().equals("true");
	}

	public String getMSH3() {
		loadProperties();
		String value = (String) toolkitProperties.get(MSH_3);
		if (value == null || value.equals("")) value = "SRCADT";
		return value;
	}

	public String getMSH4() {
		loadProperties();
		String value = (String) toolkitProperties.get(MSH_4);
		if (value == null || value.equals("")) value = "DH";
		return value;
	}

	public String getMSH5() {
		loadProperties();
		String value = (String) toolkitProperties.get(MSH_5);
		if (value == null || value.equals("")) value = "LABADT";
		return value;
	}

	public String getMSH6() {
		loadProperties();
		String value = (String) toolkitProperties.get(MSH_6);
		if (value == null || value.equals("")) value = "DH";
		return value;
	}

	public String getCacheDisabled() {
		loadProperties();
		return (String) toolkitProperties.get("Cache_Disabled");
	}

	public String getPassword() {
		loadProperties();
		return (String) toolkitProperties.get(ADMIN_PASSWORD);
	}

	public String getToolkitHost() {
		loadProperties();
		return (String) toolkitProperties.get(TOOLKIT_HOST);
	}

	public String getToolkitPort() {
		loadProperties();
		return (String) toolkitProperties.get(TOOLKIT_PORT);
	}

	public String getToolkitTlsPort() {
		loadProperties();
		return (String) toolkitProperties.get(TOOLKIT_TLS_PORT);
	}

	public List<String> getListenerPortRange() {
		loadProperties();
		String rangeString = (String) toolkitProperties.get(LISTENER_PORT_RANGE);
		if (rangeString == null) throw new ToolkitRuntimeException(LISTENER_PORT_RANGE + " missing from toolkit.properties file");
		String[] parts = rangeString.split(",");
		if (parts.length != 2) throw new ToolkitRuntimeException(LISTENER_PORT_RANGE + " from toolkit.properties is badly formtted - it must be port_number, port_number");
		List<String> range = new ArrayList<>();
		range.add(parts[0].trim());
		range.add(parts[1].trim());
		return range;
	}

	public boolean getAutoInitializeConformanceTool() {
		loadProperties();
		String value = (String) toolkitProperties.get(AUTO_INIT_CONFORMANCE_TOOL);
		if (value == null) return false;
		if (value.trim().equalsIgnoreCase("true")) return true;
		return false;
	}

	public String getToolkitGazelleConfigURL() {
		loadProperties();
		return (String) toolkitProperties.get(GAZELLE_CONFIG_URL);
	}

	public boolean isEnableSaml() {
		loadProperties();
		String use = (String) toolkitProperties.get(ENABLE_SAML);
		if (use == null)
			return true;
		use = use.trim().toLowerCase();
		return "true".compareTo(use) == 0;
	}

	public String getExternalCache() {
		loadProperties();
		String cache = (String) toolkitProperties.get(EXTERNAL_CACHE);
		// may have %20 instead of space characters on Windows.  Clean them up
		if (cache != null)
			cache = cache.replaceAll("%20", " ");
//		System.setProperty("External_Cache", cache);
		return cache;
	}

	public boolean isUseActorsFile() {
		loadProperties();
		String use = (String) toolkitProperties.get(USE_ACTORS_FILE);
		if (use == null)
			return true;
		return "true".compareToIgnoreCase(use) == 0;
	}

	public String getDefaultAssigningAuthority() {
		loadProperties();
		return (String) toolkitProperties.get("PatientID_Assigning_Authority");
	}

	public String getDefaultEnvironmentName() {
		loadProperties();
		return (String) toolkitProperties.get("Default_Environment");
	}

	public String getTestkit() {
		loadProperties();
		String testkit = (String) toolkitProperties.get(TESTKIT);
		if (testkit != null) {
			testkit = testkit.trim();
			if ("".equals(testkit)) testkit = null;
		}
		return testkit;
	}

	@Deprecated
	public String getCurrentEnvironmentName() {
		String cache = getExternalCache();
		File currentFile = new File(cache + File.separator + "environment" + File.separator + "current");
		String currentName = null;
		try {
			currentName = Io.stringFromFile(currentFile).trim();
		} catch (IOException e) {}
		return currentName;
	}

	@Deprecated
	public void setCurrentEnvironmentName(String name) throws IOException {
		String cache = getExternalCache();

		File currentFile = new File(cache + File.separator + "environment" + File.separator + "current");
		Io.stringToFile(currentFile, name);
	}

	public String getToolkitEnableAllCiphers() {
		loadProperties();
		return (String) toolkitProperties.getProperty("Enable_all_ciphers");
	}

	public void save(Map<String, String> props) throws Exception {
		saveProperties();
	}

	public void loadProperties() {
		if (toolkitProperties != null)
			return;
		toolkitProperties = new Properties();
		try {
			logger.info("Loading toolkit properties from " + propFile);
			toolkitProperties.load(new FileInputStream(propFile));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveProperties() {
		try {
			FileOutputStream fos = new FileOutputStream(propFile);
			toolkitProperties.store(fos, "");
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getPropertyMap() {
		loadProperties();
		Map<String, String> props = new HashMap<String, String>();
		for (Object keyObj : toolkitProperties.keySet()) {
			String key = (String) keyObj;
			String value = toolkitProperties.getProperty(key);
			props.put(key, value);
		}
		return props;
	}


	public String getWikiBaseAddress() {
		loadProperties();
		return (String) toolkitProperties.getProperty("Wiki_Base_URL");
	}

	public void setExternalCache(String externalCache){
		toolkitProperties.setProperty(EXTERNAL_CACHE,externalCache);
	}

    public String getProxyPort() {
		loadProperties();
		return (String) toolkitProperties.getProperty("Proxy_Port");
    }

    public boolean isSingleuserMode() {
		return !isMultiuserMode() && !isCasMode();
	}

    public boolean isMultiuserMode() {
		loadProperties();
		String mode = (String) toolkitProperties.getProperty(MULTIUSER_MODE);
		if (mode == null)
			mode = "false";
		mode = mode.trim();
		if (mode.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	public boolean isCasMode() {
		loadProperties();
		String mode = (String) toolkitProperties.getProperty(CAS_MODE);
		if (mode == null)
			mode = "false";
		mode = mode.trim();
		if (mode.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	public int getNonceSize() {
		loadProperties();
		String value = (String) toolkitProperties.getProperty(NONCE_SIZE);
		try {
			return Integer.parseInt(value.trim());
		} catch (Throwable e) {
			return 6;
		}
	}

	public String getGazelleTestingSession() {
		loadProperties();
		String value = (String) toolkitProperties.getProperty(GAZELLE_TESTING_SESSION);
		if (value != null) {
			return value.trim();
		}
		return null;
	}
}
