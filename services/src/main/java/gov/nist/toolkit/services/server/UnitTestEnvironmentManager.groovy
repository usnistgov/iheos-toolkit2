package gov.nist.toolkit.services.server
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.envSetting.EnvSetting
import gov.nist.toolkit.installation.ExternalCacheManager
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

/**
 * This is used to initialize parts of toolkit for internal unit tests.
 *
 *
 */
@TypeChecked
public class UnitTestEnvironmentManager {
    static Logger logger = Logger.getLogger(UnitTestEnvironmentManager.class);

    static public Session setupLocalToolkit(String environmentName) {
        Session session = setupLocalToolkit()
        new EnvSetting(session.getId(), environmentName, Installation.installation().environmentFile(environmentName))
        return session
    }

    static public Session setupLocalToolkit() {
        URL warMarker = new UnitTestEnvironmentManager().getClass().getResource('/war/war.txt');
        if (warMarker == null) {
            logger.fatal("Cannot locate WAR root for test environment")
            throw new ToolkitRuntimeException("Cannot locate WAR root for test environment")
        }
        File warHome = new File(warMarker.toURI().path).parentFile
        if (!warHome || !warHome.isDirectory()) throw new ToolkitRuntimeException('WAR not found')
        URL externalCacheMarker = new UnitTestEnvironmentManager().getClass().getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            logger.fatal("Cannot locate external cache for test environment")
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.initialize(externalCache)
//        Installation.installation().externalCache(externalCache)

        Session session = new Session(warHome, externalCache)
        String defaultEnvName = Installation.installation().defaultEnvironmentName();
        session.setEnvironment(defaultEnvName)
        new EnvSetting(session.getId(), defaultEnvName, Installation.installation().environmentFile(defaultEnvName))

        ListenerFactory.init(Installation.installation().listenerPortRange);
        return session
    }

    static public ToolkitApi localToolkitApi() { return ToolkitApi.forServiceUse(); }

}
