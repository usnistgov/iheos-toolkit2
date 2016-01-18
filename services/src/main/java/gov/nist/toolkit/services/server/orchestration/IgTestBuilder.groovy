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
            SimDb.deleteSims(new SimDb().getSimIdsForUser(request.userName))

            oneDocPid = session.allocateNewPid()
            twoDocPid = session.allocateNewPid()

            buildRGs()

            submit(request.userName, rgConfigs.get(0).id, new TestInstance("15807"), 'onedoc', oneDocPid)
            submit(request.userName, rgConfigs.get(0).id, new TestInstance("15807"), 'twodoc', twoDocPid)

            IgOrchestrationResponse response = new IgOrchestrationResponse()
            response.oneDocPid = oneDocPid
            response.twoDocPid = twoDocPid
            response.simulatorConfigs = rgConfigs
            response.igSimulatorConfig = igConfig

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

    void submit(String userName, SimId simId, TestInstance testId, String section, Pid patientId) {
        // load the reg/rep with two documents
        List<String> sections = [ section ]
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId.asString())

        List<Result> results = api.runTest(userName, simId.toString(), testId, sections, qparams, true)
        if (!results.get(0).passed())
            throw new Exception(results.get(0).toString())
    }

    void buildRGs() {
        // build and initialize remote communities
        String id = 'rg1'
        SimId rgSimId = new SimId(request.userName, id, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        println "creating rg1 sim"
        SimulatorConfig rgSimConfig = api.createSimulator(rgSimId).getConfig(0)

        // this expects full server version of simulator config
        // this call makes the configuration available as a site for the test client
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig)

        // disable checking of Patient Identity Feed
        SimulatorConfigElement rgEle = rgSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
        rgEle.setValue(false)
        api.saveSimulator(rgSimConfig)

        rgConfigs << rgSimConfig

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