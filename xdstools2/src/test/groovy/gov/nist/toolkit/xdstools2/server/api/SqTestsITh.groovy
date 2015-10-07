package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.transactions.CallType
import spock.lang.Specification
/**
 * Created by bill on 8/24/15.
 */
class SqTestsITh extends Specification {

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

    def 'Initialize SQ tests'() {
        setup:
        simApi.delete(regSimId)   // Delete sim since old data will mess up results
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Declare patientid'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', pid);

        then:
        true

        when: 'Test data part 1'
        boolean status = client.runTest('12346', site, tls, parms, true, CallType.SOAP)

        then:
        status

        when: 'Test data part 2'
        status = client.runTest('12374', site, tls, parms, true, CallType.SOAP)

        then:
        status
    }

    def 'Run SQ tests'() {
        setup:
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Run SQ tests'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', pid);
        boolean status = client.runTestCollection('SQ.b', site, tls, parms, true, CallType.SOAP)

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
