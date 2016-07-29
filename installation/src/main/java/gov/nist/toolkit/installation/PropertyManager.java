package gov.nist.toolkit.installation;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class PropertyManager {

	static Logger logger = Logger.getLogger(PropertyManager.class);

	static public final String ADMIN_PASSWORD      = "Admin_password";
	static public final String TOOLKIT_HOST        = "Toolkit_Host";
	static public final String TOOLKIT_PORT        = "Toolkit_Port";
	static public final String TOOLKIT_TLS_PORT    = "Toolkit_TLS_Port";
	static public final String GAZELLE_CONFIG_URL  = "Gazelle_Config_URL";
	static public final String EXTERNAL_CACHE      = "External_Cache";
	static public final String USE_ACTORS_FILE     = "Use_Actors_File";
	static public final String TESTKIT             = "Testkit";
	static public final String LISTENER_PORT_RANGE = "Listener_Port_Range";

	String propFile;
	Properties toolkitProperties = null;
	
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
	
	void validateProperty(String name, String value) throws Exception {
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

	public String getToolkitGazelleConfigURL() {
		loadProperties();
		return (String) toolkitProperties.get(GAZELLE_CONFIG_URL);
	}
	
	public String getExternalCache() {
		loadProperties();
		String cache = (String) toolkitProperties.get(EXTERNAL_CACHE);
		System.setProperty("External_Cache", cache);
		return cache;
	}
	
	public String getImageCache() {
	   loadProperties();
	   String cache = (String) toolkitProperties.getProperty("Image_Cache");
	   System.setProperty("Image_Cache", cache);
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
	

}
