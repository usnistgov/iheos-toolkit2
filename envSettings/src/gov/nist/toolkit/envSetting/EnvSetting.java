package gov.nist.toolkit.envSetting;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class EnvSetting {
	static Map<String, EnvSetting> settings = new HashMap<String, EnvSetting>();
	String envName;
	File envDir;
	
	static Logger logger = Logger.getLogger(EnvSetting.class);

	static public EnvSetting getEnvSetting(String sessionId) throws EnvironmentNotSelectedException {
		EnvSetting s = settings.get(sessionId);
		if (s == null)
			throw new EnvironmentNotSelectedException("");
		return s;
	}

	public EnvSetting(String sessionId, String name, File dir) {
		logger.info("Session " + sessionId + " environment " + name + " ==> " + dir);
		settings.put(sessionId, new EnvSetting(name, dir));
	}
	
	public EnvSetting(String sessionId, String name) {
		File dir = Installation.installation().environmentFile(name);
		logger.info("Session " + sessionId + " environment " + name + " ==> " + dir);
		settings.put(sessionId, new EnvSetting(name, dir));
	}
	
	private EnvSetting(String name, File dir) {
		this.envName = name;
		this.envDir = dir;
	}
	
	public String getEnvName() {
		return envName;
	}
	
	public File getEnvDir() {
		return envDir;
	}
	
	public File getCodesFile() throws EnvironmentNotSelectedException {
		if (envDir == null) 
			throw new EnvironmentNotSelectedException("");
//			return new File(Installation.installation().warHome() + File.separator + "toolkitx" + File.separator + "codes" + File.separator + "codes.xml");
		File f = new File(envDir + File.separator + "codes.xml");
		if (f.exists())
			return f;
		return null;
	}


}
