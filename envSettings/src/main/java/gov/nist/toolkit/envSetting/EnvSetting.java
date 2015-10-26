package gov.nist.toolkit.envSetting;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EnvSetting {
	// SessionID ==> Environment Setting
	static Map<String, EnvSetting> settings = new HashMap<String, EnvSetting>();
    static public final String DEFAULTSESSIONID = "DEFAULT";
    static public final String DEFAULTENVIRONMENTNAME = "default";
	String envName;
	File envDir;
	
	static Logger logger = Logger.getLogger(EnvSetting.class);

	static public EnvSetting getEnvSetting(String sessionId) throws EnvironmentNotSelectedException {
        EnvSetting s = getEnvSettingForSession(sessionId);
        if (s == null) throw new EnvironmentNotSelectedException("");
		return s;
	}

    static public EnvSetting getEnvSettingForSession(String sessionId) {
        EnvSetting s = settings.get(sessionId);
        if (s == null) {
            if (DEFAULTSESSIONID.equals(sessionId)) {
                installDefaultEnvironment();
                return settings.get(sessionId);
            } else
                return null;
        }
        return s;
    }

    static void installDefaultEnvironment() {
        File envFile = Installation.installation().internalEnvironmentFile(DEFAULTENVIRONMENTNAME);
        if (envFile == null || !envFile.exists()) throw new EnvironmentNotSelectedException("Default Environment not configured - file " + envFile + " not found.");
        new EnvSetting(DEFAULTSESSIONID, DEFAULTENVIRONMENTNAME, envFile);
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

    public EnvSetting(String envName) {
        this.envName = envName;
        this.envDir = Installation.installation().environmentFile(envName);
        validateEnvironment();
    }
	
	private EnvSetting(String name, File dir) {
		this.envName = name;
		this.envDir = dir;
        validateEnvironment();
	}
	
	public String getEnvName() {
		return envName;
	}
	
	public File getEnvDir() {
		return envDir;
	}
	
	public File getCodesFile() throws EnvironmentNotSelectedException {
		if (envDir == null) 
			throw new EnvironmentNotSelectedException(String.format("Environment %s does not exist", envName));
//			return new File(Installation.installation().warHome() + File.separator + "toolkitx" + File.separator + "codes" + File.separator + "codes.xml");
		File f = new File(envDir + File.separator + "codes.xml");
		if (f.exists())
			return f;
		logger.warn("Codes file " + f + " does not exist");
		return null;
	}

    void validateEnvironment() {
        if (getCodesFile() == null)
            throw new EnvironmentNotSelectedException("Selected environment " + envName + " not valid - does not contain codes.xml file");
    }

}
