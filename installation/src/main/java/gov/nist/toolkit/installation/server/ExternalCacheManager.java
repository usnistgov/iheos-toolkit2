package gov.nist.toolkit.installation.server;

import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import gov.nist.toolkit.installation.shared.TestSession;

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
        initializeExternalCacheWithInternalData(location);
        return null;
    }

    synchronized public static void reinitialize(File location) throws XdsException {
        logger.info("Reinitialize External Cache to " + location);
        Installation.instance().externalCache(null);  // so that it can be updated
        String error = initialize(location);
        if (error != null) throw new XdsException(error, "");
        File environment = Installation.instance().environmentFile();
        // initialize environment
        initializeExternalCacheWithInternalData(location);
        // initialize test log cache
        Installation.instance().testLogCache(TestSession.DEFAULT_TEST_SESSION).mkdirs();
        // initialize SimDb
        Installation.instance().simDbFile(TestSession.DEFAULT_TEST_SESSION).mkdirs();
    }

    private static void initializeExternalCacheWithInternalData(File externalCache) throws XdsException {
        logger.info("Initialize external cache check");
        if (!Installation.instance().environmentFile().exists() || !Installation.instance().environmentFile(Installation.DEFAULT_ENVIRONMENT_NAME).exists()) {
            logger.info("Initializing environments in " + externalCache);
            try {
                FileUtils.copyDirectory(Installation.instance().internalEnvironmentsFile(), new File(externalCache, "environment"));
            } catch (IOException e) {
                throw new XdsException("Cannot initialize environments area of External Cache at " + externalCache, "", e);
            }
        } else {
            logger.info("Environments exist - not updating.");
        }

//        if (!Installation.instance().datasets().exists()) {
            logger.info("Initializing datasets in " + externalCache);
            try {
                FileUtils.copyDirectory(Installation.instance().internalDatasetsFile(), new File(externalCache, "datasets"));
            } catch (IOException e) {
                String msg = "Cannot initialize datasets area of External Cache at " + externalCache + " - " + e.getMessage();
                logger.error(msg);
                //throw new XdsException(msg, "", e);
            }
//        } else {
//            logger.info("Datasets exist - not updating.");
//        }

//        if (!Installation.instance().resourceCacheFile().exists()) {
            logger.info("Initializing resource cache in " + externalCache);
            try {
                FileUtils.copyDirectory(Installation.instance().internalResourceCacheFile(), new File(externalCache, "resourceCache"));
            } catch (IOException e) {
                String msg = "Cannot initialize resourceCache area of External Cache at " + externalCache + " - " + e.getMessage();
                logger.error(msg);
               // throw new XdsException(msg, "", e);
            }
//        } else {
//            logger.info("Resource cache exists - not updating.");
//        }
    }

    private static void initializeDefaultSites() throws XdsException {
        try {
            File internalActorsDir = Installation.instance().internalActorsDir();
            String[] list = internalActorsDir.list();
            if (list == null) return;
            File externalDir = Installation.instance().actorsDir(TestSession.DEFAULT_TEST_SESSION);
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
            //throw new XdsException(msg, null);
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
