package gov.nist.toolkit.installation;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyManager {

	static Logger logger = Logger.getLogger(PropertyManager.class);

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
		if (name.equals("Admin_password")) {
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
	
	public String getPassword() {
		loadProperties();
		return (String) toolkitProperties.get("Admin_password");
	}
	
	public String getToolkitHost() {
		loadProperties();
		return (String) toolkitProperties.get("Toolkit_Host");
	}
	
	public String getToolkitPort() {
		loadProperties();
		return (String) toolkitProperties.get("Toolkit_Port");
	}
	
	public String getToolkitTlsPort() {
		loadProperties();
		return (String) toolkitProperties.get("Toolkit_TLS_Port");
	}
	
	public String getToolkitGazelleConfigURL() {
		loadProperties();
		return (String) toolkitProperties.get("Gazelle_Config_URL");
	}
	
	public String getExternalCache() {
		loadProperties();
		String cache = (String) toolkitProperties.get("External_Cache");
		System.setProperty("External_Cache", cache);
		return cache;
	}
	
	public boolean isUseActorsFile() {
		loadProperties();
		String use = (String) toolkitProperties.get("Use_Actors_File");
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
			toolkitProperties.load(new FileInputStream(propFile));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
