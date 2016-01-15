package gov.nist.toolkit.services.server.orchestration
import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.Pid
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import groovy.transform.TypeChecked
/**
 * Build environment for testing Initiating Gateway SUT.
 * Will not try to delete sims if they already exist - request will fail.
 */
@TypeChecked
class IgTestBuilder {

    static List<SimulatorConfig> build(ToolkitApi api, int numberCommunities, String userName, String environmentName, Pid patientId, boolean includeLinkedIG) {
        List<SimulatorConfig> rgConfigs = []  // two elements - IG config and list of RG configs

        // build and initialize remote communities
        (1..numberCommunities).each {
            String id = String.format("rg%d", it)
            SimId rgSimId = new SimId(userName, id, ActorType.RESPONDING_GATEWAY.name, environmentName)
            SimulatorConfig rgSimConfig = api.createSimulator(rgSimId).getConfig(0)

            // this expects full server version of simulator config
            // this call makes the configuration available as a site for the test client
            SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig)

            // disable checking of Patient Identity Feed
            SimulatorConfigElement rgEle = rgSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
            rgEle.setValue(false)
            api.saveSimulator(rgSimConfig)

            rgConfigs << rgSimConfig

            // load the reg/rep with two documents
            TestInstance testId = new TestInstance("12318")
            List<String> sections = null
            Map<String, String> qparams = new HashMap<>()
            qparams.put('$patientid$', patientId.asString())

            List<Result> results = api.runTest(userName, rgSimId.toString(), testId, sections, qparams, true)
            assert results.get(0).passed()
        }

        List<SimulatorConfig> configs = []

        if (includeLinkedIG) {
            // create initiating gateway
            String igId = 'ig'
            SimId igSimId = new SimId(userName, igId, ActorType.INITIATING_GATEWAY.name, environmentName)
            SimulatorConfig igConfig = api.createSimulator(igSimId).getConfig(0);

            // link all responding gateways to initiating gateway
            List<String> rgConfigIds = rgConfigs.collect() { SimulatorConfig rg -> rg.id.toString() }
            SimulatorConfigElement rgs = igConfig.getConfigEle(SimulatorProperties.respondingGateways)
            rgs.setValue(rgConfigIds)
            api.saveSimulator(igConfig)

            configs << igConfig
        }
        configs.addAll(rgConfigs)
        configs
    }
}