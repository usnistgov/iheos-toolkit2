package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.SiteServiceManager
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import spock.lang.Specification

/**
 * Created by bill on 6/15/15.
 */
class ClientApiIT extends Specification {
    Session session
    String simId = 'myreg'
    boolean tls = false

    def setup() {
        session = Support.setupToolkit()
    }

    def 'Run Register Transaction'() {
        setup:
        // Build Registry sim as target of submission
        SimulatorApi simApi = new SimulatorApi(session)
        Simulator sim = simApi.create('reg', simId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Build test client to ack as Repository to send submission'
        ClientApi client = new ClientApi(session)

        and: 'Send transaction'
        boolean status = client.run('11990', site, tls)

        then:
        status
    }
}
