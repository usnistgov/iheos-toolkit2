package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.services.server.SimulatorApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.TestSession
import spock.lang.Specification

/**
 * Created by bill on 6/15/15.
 */
class SimulatorApiITh extends Specification {
    Session session
    String simId = 'myreg'

    def setup() {
        session = TestSession.setupToolkit()
    }

    def 'Create, test, delete Simulator'() {
        when:
        SimulatorApi simApi = new SimulatorApi(session)
        Simulator sim = simApi.create('reg', simId)
        println sim.toString()

        then:
        simApi.exists(simId)

        when:
        simApi.delete(simId)

        then:
        !simApi.exists(simId)
    }
}
