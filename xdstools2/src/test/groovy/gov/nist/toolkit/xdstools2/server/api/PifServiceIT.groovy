package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.client.Pid
import gov.nist.toolkit.actorfactory.client.PidBuilder
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager
import spock.lang.Specification

/**
 * Created by bill on 9/23/15.
 */
class PifServiceIT extends Specification {
    Session session
    String siteName = 'mike__reg'
    ToolkitApi api;

    def setup() {
        api = new ToolkitApi()
        session = api.session
        api.deleteSimulatorIfItExists(new SimId(siteName))
        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'PIF Utility test'() {
        when:
        Pid pid = PidBuilder.createPid('P4^^^&1.2&ISO')
        SiteSpec site = new SiteSpec()
        site.setName(siteName)
        new XdsTestServiceManager(session).sendPidToRegistry(site, pid)

        then: true
    }

}
