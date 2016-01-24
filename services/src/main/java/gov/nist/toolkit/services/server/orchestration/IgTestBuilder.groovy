package gov.nist.toolkit.services.server.orchestration
import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.Pid
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.IgOrchestationManagerRequest
import gov.nist.toolkit.services.client.IgOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import groovy.transform.TypeChecked
/**
 * Build environment for testing Initiating Gateway SUT.
 * Will not try to delete sims if they already exist - request will fail.
 */
@TypeChecked
class IgTestBuilder {
    Session session
    IgOrchestationManagerRequest request
    Pid oneDocPid
    Pid twoDocPid
    Pid twoRgPid
    ToolkitApi api
    List<SimulatorConfig> rgConfigs = []
    SimulatorConfig igConfig = null

    public IgTestBuilder(ToolkitApi api, Session session, IgOrchestationManagerRequest request) {
        this.api = api
        this.session = session
        this.request = request
    }

    RawResponse buildTestEnvironment() {
        try {
            new SimDb().getSimIdsForUser(request.userName).each { api.deleteSimulator(it) }

            oneDocPid = session.allocateNewPid()
            twoDocPid = session.allocateNewPid()
            twoRgPid = session.allocateNewPid()

            buildRGs()

            String home = rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString()
            submit(request.userName, rgConfigs.get(0).id, new TestInstance("15807"), 'onedoc1', oneDocPid, home)
            submit(request.userName, rgConfigs.get(0).id, new TestInstance("15807"), 'twodoc', twoDocPid, home)

            submit(request.userName, rgConfigs.get(0).id, new TestInstance("15807"), 'onedoc2', twoRgPid, home)
            submit(request.userName, rgConfigs.get(1).id, new TestInstance("15807"), 'onedoc3', twoRgPid, home)

            IgOrchestrationResponse response = new IgOrchestrationResponse()
            response.oneDocPid = oneDocPid
            response.twoDocPid = twoDocPid
            response.twoRgPid = twoRgPid
            response.simulatorConfigs = rgConfigs
            response.igSimulatorConfig = igConfig

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

    void submit(String userName, SimId simId, TestInstance testId, String section, Pid patientId, String home) {
        // load the reg/rep with two documents
        List<String> sections = [ section ]
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId.asString())
        qparams.put('$testdata_home$', home);

        List<Result> results = api.runTest(userName, simId.toString(), testId, sections, qparams, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    void buildRGs() {
        // build and initialize remote communities
        String id1 = 'rg1'
        String id2 = 'rg2'
        SimId rgSimId1 = new SimId(request.userName, id1, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        SimId rgSimId2 = new SimId(request.userName, id2, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        println "creating rg1 sim"
        SimulatorConfig rgSimConfig1 = api.createSimulator(rgSimId1).getConfig(0)
        println "creating rg2 sim"
        SimulatorConfig rgSimConfig2 = api.createSimulator(rgSimId2).getConfig(0)

        // this expects full server version of simulator config
        // this call makes the configuration available as a site for the test client
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig1)
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig2)

        // disable checking of Patient Identity Feed
        SimulatorConfigElement rgEle1 = rgSimConfig1.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
        rgEle1.setValue(false)
        api.saveSimulator(rgSimConfig1)

        SimulatorConfigElement rgEle2 = rgSimConfig2.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
        rgEle2.setValue(false)
        api.saveSimulator(rgSimConfig2)

        rgConfigs << rgSimConfig1
        rgConfigs << rgSimConfig2

        if (request.includeLinkedIG) {
            // create initiating gateway
            String igId = 'ig'
            SimId igSimId = new SimId(request.userName, igId, ActorType.INITIATING_GATEWAY.name, request.environmentName)
            igConfig = api.createSimulator(igSimId).getConfig(0);

            // link all responding gateways to initiating gateway
            List<String> rgConfigIds = rgConfigs.collect() { SimulatorConfig rg -> rg.id.toString() }
            SimulatorConfigElement rgs = igConfig.getConfigEle(SimulatorProperties.respondingGateways)
            rgs.setValue(rgConfigIds)
            api.saveSimulator(igConfig)
        }
    }
}