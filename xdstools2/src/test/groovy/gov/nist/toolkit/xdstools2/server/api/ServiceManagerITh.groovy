package gov.nist.toolkit.xdstools2.server.api
import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.services.server.SimulatorApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.engine.TransactionSettings
import spock.lang.Specification
/**
 * Created by bill on 8/11/15.
 */
class ServiceManagerITh extends Specification {
    Session session
    SimulatorApi simApi
    String regSimId = 'myreg'
    String repSimId = 'myrec'
    String rrSimId = 'rr'
    boolean tls = false

    def setup() {
        ClientApi client = new ClientApi()
        session = client.getSession()
        simApi = new SimulatorApi(session)
    }

    // As a utility it assumes the results go in the SessionCache
    // This is controlled in XdsTestServiceManager
    //      run ->
    //      run ->
    // and override can be done via
    // session.transactionSettings.logRepository =
    def 'Run Register Transaction as Utility'() {
        setup:
        // Build Registry sim as target of submission
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Send transaction'
        String pid = '123^^^&1.2.343&ISO'
        Map<String, String> parms  = new HashMap<String, String>();

        session.siteSpec = new SiteSpec(site.getSiteName(), ActorType.REGISTRY, null)
        XdsTestServiceManager manager = session.xdsTestServiceManager()
        TransactionSettings ts = new TransactionSettings()
        ts.patientId = pid
        ts.writeLogs = true
        session.transactionSettings = ts

        // This writes log files to war/SessionCache/STANDALONE/timestamp
        Result result = manager.xdstest('11990', null, parms, null, null, true)

        then:
        result.passed()
    }


    def 'Run Register Transaction as Test'() {
        setup:
        // Build Registry sim as target of submission
        Simulator sim = simApi.create('reg', regSimId)

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Send transaction'
        String pid = '123^^^&1.2.343&ISO'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', pid);
        parms.put('$patient_id$', pid);

        session.siteSpec = new SiteSpec(site.getSiteName(), ActorType.REGISTRY, null)
        XdsTestServiceManager manager = session.xdsTestServiceManager()
        TransactionSettings ts = new TransactionSettings()

        // writes results to /Users/bill/tmp/toolkit2/TestLogCache/STANDALONE/11990
        List<Result> results = manager.runMesaTest(session.getId(), session.siteSpec, '11990', null, parms, null, true)

        then:
        results.get(0).passed()
    }
}
