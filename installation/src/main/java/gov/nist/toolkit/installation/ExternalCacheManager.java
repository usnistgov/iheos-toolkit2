package gov.nist.toolkit.installation;

import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Initialize External Cache
 */
public class ExternalCacheManager {
    static Logger logger = Logger.getLogger(ExternalCacheManager.class);

    /**
     * initialize external cache to location.  If external cache location is already initialized this will be ignored.
     * To change location use reinitialize()
     * @param location an existing writable directory
     * @return
     * @throws XdsException
     */
    synchronized public static String initialize(File location) throws XdsException {
        logger.info("Initialize External Cache to " + location);
        if (!location.exists()) return String.format("External Cache location %s does not exist", location);
        if (!location.isDirectory()) return String.format("External Cache location %s is not a directory", location);
        if (!location.canWrite()) return String.format("External Cache location %s cannot be written", location);
        if (Installation.instance().externalCache() == null)
            Installation.instance().externalCache(location);
        // initialize environment
        initializeDefaultEnvironment(location, Installation.instance().environmentFile());
        return null;
    }

    synchronized public static void reinitialize(File location) throws XdsException {
        logger.info("Reinitialize External Cache to " + location);
        Installation.instance().externalCache(null);  // so that it can be updated
        String error = initialize(location);
        if (error != null) throw new XdsException(error, "");
        File environment = Installation.instance().environmentFile();
        // initialize environment
        initializeDefaultEnvironment(location, environment);
        // initialize test log cache
        Installation.instance().testLogCache().mkdirs();
        // initialize SimDb
        Installation.instance().simDbFile().mkdirs();
    }

    private static void initializeDefaultEnvironment(File location, File environment) throws XdsException {
        logger.info("initialize default environment check");
        if (!environment.exists() || !Installation.instance().environmentFile(Installation.DEFAULT_ENVIRONMENT_NAME).exists()) {
            logger.info("Initializing environments in " + location);
            try {
                FileUtils.copyDirectory(Installation.instance().internalEnvironmentsFile(), new File(location, "environment"));
            } catch (IOException e) {
                throw new XdsException("Cannot initialize environments area of External Cache at " + location, "", e);
            }
        }
    }

    private static void initializeDefaultSites() throws XdsException {
        try {
            File internalActorsDir = Installation.instance().internalActorsDir();
            String[] list = internalActorsDir.list();
            if (list == null) return;
            File externalDir = Installation.instance().actorsDir();
            for (String internalName : list) {
                if (!internalName.endsWith("xml")) continue;
                File internalFile = new File(internalActorsDir, internalName);
                File externalFile = new File(externalDir, internalName);
                if (!externalFile.exists())
                    FileUtils.copyFile(internalFile, externalFile);
            }
        } catch (IOException e) {
            throw new XdsException("IO Error installing default sites to external cache", "", e);
        }
    }

    public static void initializeFromMarkerFile(File markerFile) throws XdsException {
        reinitialize(markerFile.getParentFile());
    }

    public static void initialize() throws XdsException {
        File location = new File(Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache());
        String error = initialize(location);
        if (error != null) {
            String msg = "External cache location " + location + " does not exist, is not writeable or is not a directory";
            logger.error(msg);
//            throw new XdsException(msg, null);
        }
        initializeDefaultSites();
    }

    public static String validate() {
        File location = Installation.instance().externalCache();
        if (!location.exists()) return String.format("External Cache location %s does not exist. " + HOW_TO_FIX, location);
        if (!location.isDirectory()) return String.format("External Cache location %s is not a directory. " + HOW_TO_FIX, location);
        if (!location.canWrite()) return String.format("External Cache location %s cannot be written. " + HOW_TO_FIX, location);
        File defEnv = Installation.instance().environmentFile("default");
        if (!defEnv.exists()) return String.format("Default Environment (default) not found in External Cache (%s). " +
                        HOW_TO_FIX,
                location);


        return null;
    }

    static final String HOW_TO_FIX = "Open Toolkit Configuration, edit External Cache location (if necessary) and save. If your External Cache location is ok " +
            " you may only need to update your External Cache.  The SAVE will do that update.";
}
