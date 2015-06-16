package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException

/**
 * Created by bill on 6/15/15.
 */
class Support {

    static public Session setupToolkit() {
//        File warHome = new File(getClass().getResource('/war/war.txt').toURI().path).parentFile
//        if (!warHome || !warHome.isDirectory()) throw new ToolkitRuntimeException('WAR not found')
//        File externalCache = new File(getClass().getResource('/external_cache/external_cache.txt').toURI().path).parentFile
//        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
//        Session session = new Session(warHome, externalCache)
//        session.setEnvironment('test')
//        return session


        File warHome = new File(getClass().getResource('/war/war.txt').toURI().path).parentFile
        if (!warHome || !warHome.isDirectory()) throw new ToolkitRuntimeException('WAR not found')
        File externalCache = new File('/Users/bill/tmp/toolkit2')
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        Session session = new Session(warHome, externalCache)
        session.setEnvironment('NA2015')
        return session
    }
}
