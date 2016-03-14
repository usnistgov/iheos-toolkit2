package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.services.server.ClientApi
import gov.nist.toolkit.services.server.SimulatorApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.transactions.CallType
import spock.lang.Specification

/**
 * Created by bill on 8/24/15.
 */
class GetAllTestITh extends Specification {
    ClientApi client
    Session session
    SimulatorApi simApi
    String regSimId = 'myreg'
    String rrSimId = 'rr'
    boolean tls = false
    String pid = '123^^^&1.2.343&ISO'

    def setup() {
        client = new ClientApi()
        session = client.getSession()
        simApi = new SimulatorApi(session)
    }

    def 'Run 11990 Register test'() {
        setup:
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Send transaction'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', pid);

        boolean status = client.runTest('11990', site, tls, parms, false, CallType.SOAP)

        then:
        status
    }

    def 'Run GetAll tests'() {
        setup:
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Run SQ tests'
        Map<String, String> parms  = new HashMap<String, String>();
//        parms.put('$patientid$', pid);
        boolean status = client.runTest('15803', site, tls, parms, true, CallType.SOAP)

        then:
        status
    }
}
