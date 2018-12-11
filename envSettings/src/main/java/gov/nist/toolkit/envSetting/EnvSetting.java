package gov.nist.toolkit.envSetting;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EnvSetting {
	// SessionID ==> Environment Setting
	private static Map<String, EnvSetting> settings = new HashMap<String, EnvSetting>();
    static public final String DEFAULTSESSIONID = "DEFAULT";
    static public final String DEFAULTENVIRONMENTNAME = "default";
	String envName;
	File envDir;
	
	static Logger logger = Logger.getLogger(EnvSetting.class);

	static public EnvSetting getEnvSetting(String sessionId) throws EnvironmentNotSelectedException {
        EnvSetting s = getEnvSettingForSession(sessionId);
        if (s == null) {
            logger.info(String.format("For session %s...", sessionId));
            logger.info(String.format("Session/Env mapping table - %s", settings.toString()));
            throw new EnvironmentNotSelectedException("");
        }
		return s;
	}

    public String toString() {
        return String.format("ENV %s => %s", envName, envDir);
    }

    private static void addSetting(String sessionId, EnvSetting envSetting) {
	    settings.put(sessionId, envSetting);
        if (settings.keySet().size() == 3)
            logger.info("third setting");
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

    public static void installDefaultEnvironment() {
        File envFile = Installation.instance()./*internal*/environmentFile(DEFAULTENVIRONMENTNAME);
        if (envFile == null || !envFile.exists()) throw new EnvironmentNotSelectedException("Default Environment not configured - file " + envFile + " not found.");
        new EnvSetting(DEFAULTSESSIONID, DEFAULTENVIRONMENTNAME, envFile);
//        new EnvSetting(Installation.defaultSessionName(), DEFAULTENVIRONMENTNAME, envFile);
//        new EnvSetting(Installation.defaultServiceSessionName(), DEFAULTENVIRONMENTNAME, envFile);
    }

    public static void installServiceEnvironment() {
        File envFile = Installation.instance().internalEnvironmentFile(DEFAULTENVIRONMENTNAME);
        if (envFile == null || !envFile.exists()) throw new EnvironmentNotSelectedException("Default Environment not configured - file " + envFile + " not found.");
        new EnvSetting(Installation.defaultServiceSessionName(), DEFAULTENVIRONMENTNAME, envFile);
    }

	public EnvSetting(String sessionId, String name, File dir) {
//		logger.info(sessionId + ": EnvSetting -  uses environment " + name + " ==> " + dir);
		addSetting(sessionId, new EnvSetting(name, dir));
	}
	
	public EnvSetting(String sessionId, String name) {
		File dir = Installation.instance().environmentFile(name);
//		logger.info("Session " + sessionId + " environment " + name + " ==> " + dir);
        addSetting(sessionId, new EnvSetting(name, dir));
	}

    public EnvSetting(String envName) {
        this.envName = envName;
        this.envDir = Installation.instance().environmentFile(envName);
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
//			return new File(Installation.instance().warHome() + File.separator + "toolkitx" + File.separator + "codes" + File.separator + "codes.xml");
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
