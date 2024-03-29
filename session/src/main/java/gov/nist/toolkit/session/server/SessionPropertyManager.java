package gov.nist.toolkit.session.server;

import java.util.logging.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SessionPropertyManager {
	static Logger logger = Logger.getLogger(SessionPropertyManager.class.getName());

	String propFile;
	Properties properties = null;
	
	public void clear() {
		properties = new Properties();
	}
	
	public Map<String, String> asMap() {
		Map<String, String> m = new HashMap<String, String>();
		
		for (Object okey : properties.keySet()) {
			String key = (String) okey;
			String value = properties.getProperty(key);
			m.put(key, value);
		}
		
		return m;
	}
	
	public SessionPropertyManager(String sessionDirectory) {
		this.propFile = sessionDirectory + File.separator + "session.properties";
		properties = new Properties();
	}
	
	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(propFile);
			properties.store(fos, "");
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void load() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propFile));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void set(String prop, String value) {
		properties.setProperty(prop, value);
	}
	
	public void add(Map<String, String> props) {
		for (Object okey : props.keySet()) {
			String key = (String) okey;
			properties.setProperty(key, props.get(key));
		}
	}
	
	public String get(String prop) {
		return (String) properties.getProperty(prop);
	}
	
}
