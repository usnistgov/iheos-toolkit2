package gov.nist.toolkit.xdstools2.server.api

import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import spock.lang.Specification

/**
 * Created by bill on 6/15/15.
 */
class ClientApiIT extends Specification {
    Session session
    String regSimId = 'myreg'
    String repSimId = 'myrec'
    String rrSimId = 'rr'
    boolean tls = false

    def setup() {
        session = Support.setupToolkit()
    }

    def 'Run Register Transaction'() {
        setup:
        // Build Registry sim as target of submission
        SimulatorApi simApi = new SimulatorApi(session)
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Build test client to ack as Repository to send submission'
        ClientApi client = new ClientApi(session)

        and: 'Send transaction'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', '123^^^&1.2.343&ISO');

        boolean status = client.run('11990', site, tls, parms)

        then:
        status
    }


//    def 'Run Provide and Register Transaction to Recipient'() {
//        setup:
//        // Build Recipient sim as target of submission
//        SimulatorApi simApi = new SimulatorApi(session)
//        Simulator sim = simApi.create('rec', repSimId)
//
//        when: 'Create site for simulator'
//        Site site = SimManager.getSite(sim.configs.get(0))
//
//        then: 'site exists'
//        site
//
//        when: 'Build test client to ack as Repository to send submission'
//        ClientApi client = new ClientApi(session)
//
//        and: 'Send transaction'
//        Map<String, String> parms  = new HashMap<String, String>();
//        parms.put('$patientid$', '123^^^&1.2.343&ISO');
//
//        boolean status = client.run('12371', site, tls, parms)
//
//        then:
//        status
//    }


    def 'Run Provide and Register Transaction to RR'() {
        setup:
        // Build Recipient sim as target of submission
        SimulatorApi simApi = new SimulatorApi(session)
        Simulator sim = simApi.create('rr', rrSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Build test client to ack as Repository to send submission'
        ClientApi client = new ClientApi(session)

        and: 'Send transaction'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', '123^^^&1.2.343&ISO');

        boolean status = client.run('11966', site, tls, parms)

        then:
        status
    }
}
