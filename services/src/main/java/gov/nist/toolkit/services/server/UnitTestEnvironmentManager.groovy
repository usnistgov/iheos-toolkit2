package gov.nist.toolkit.services.server

import gov.nist.toolkit.envSetting.EnvSetting
import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

import java.nio.file.Paths

/**
 * This is used to initialize parts of toolkit for internal unit tests.
 *
 * Should be incorporated into itTests.support.TestSupport when all
 * Integration tests are moved to module it-tests
 */
@TypeChecked
public class UnitTestEnvironmentManager {
    static String envName = null;
    static Logger logger = Logger.getLogger(UnitTestEnvironmentManager.class);
    private Session session;
    File warHomeDir;
    File ecDir;

    UnitTestEnvironmentManager() {
    }

    UnitTestEnvironmentManager(File warHomeDir, File ecDir) {
        this.warHomeDir = warHomeDir
        this.ecDir = ecDir

        isWarHome(warHomeDir)

        File toolkitProperties = new File(warHomeDir.parentFile, "toolkit.properties")
        Installation.instance().propertyServiceManager().loadPropertyManager(toolkitProperties)

        initEcDir(ecDir)

        this.session = createSession(warHomeDir, ecDir)
        new EnvSetting(session.getId(), getDefaultEnv(), Installation.instance().environmentFile(getDefaultEnv()))
    }

    static public Session setupLocalToolkit(String environmentName) {
        envName = environmentName
        Session session = setupLocalToolkit()
        new EnvSetting(session.getId(), environmentName, Installation.instance().environmentFile(environmentName))
        return session
    }

    static public Session setupLocalToolkit() {
        logger.info("UnitTestEnvironmentManager")
        File warMarker = Paths.get(new UnitTestEnvironmentManager().getClass().getResource('/').toURI()).resolve('war/war.txt').toFile()
        if (warMarker == null) {
            logger.fatal("Cannot locate WAR marker file for test environment")
            throw new ToolkitRuntimeException("Cannot locate WAR root for test environment")
        }
        File warHome = warMarker.parentFile
        isWarHome(warHome)
        File externalCacheMarker = Paths.get(new UnitTestEnvironmentManager().getClass().getResource('/').toURI()).resolve('external_cache/external_cache.txt').toFile()
        if (externalCacheMarker == null) {
            logger.fatal("Cannot locate external cache marker file for test environment")
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }

        File externalCache = externalCacheMarker.parentFile
        initEcDir(externalCache)

        Session session = createSession(warHome, externalCache)

        new EnvSetting(session.getId(), getDefaultEnv(), Installation.instance().environmentFile(getDefaultEnv()))

//        ListenerFactory.init(Installation.instance().listenerPortRange);

//        Installation.instance().propertyServiceManager().getPropertyManager().setExternalCache(externalCache.toString());
        return session
    }

    private static void isWarHome(File warHome) {
        if (!warHome || !warHome.isDirectory()) throw new ToolkitRuntimeException('WAR not found')
    }

    private static initEcDir(File externalCache) {
        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)
//        Installation.instance().externalCache(externalCache)
    }

    private static Session createSession(File warHome, File externalCache) {
        Session session = new Session(warHome, externalCache)
        session.setEnvironment(getDefaultEnv())
        return session
    }

    private static String getDefaultEnv() {
        (envName) ? envName : Installation.instance().defaultEnvironmentName()
    }


    static public ToolkitApi localToolkitApi() { return ToolkitApi.forServiceUse(); }

    Session getSession() {
        return session
    }
}