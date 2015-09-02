package gov.nist.toolkit.session.server
import gov.nist.toolkit.envSetting.EnvSetting
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException
/**
 * Created by bill on 6/15/15.
 */
class TestSession {

    static public Session setupToolkit() {
        File warHome = new File(getClass().getResource('/war/war.txt').toURI().path).parentFile
        if (!warHome || !warHome.isDirectory()) throw new ToolkitRuntimeException('WAR not found')
        File externalCache = new File(getClass().getResource('/external_cache/external_cache.txt').toURI().path).parentFile
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        Session session = new Session(warHome, externalCache)
        String defaultEnvName = Installation.installation().defaultEnvironmentName();
        session.setEnvironment(defaultEnvName)
        new EnvSetting(session.getId(), defaultEnvName, Installation.installation().environmentFile(defaultEnvName))
        return session
    }
}
