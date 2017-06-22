package gov.nist.toolkit.itSupport.xc

import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.toolkitApi.InitiatingGateway
import gov.nist.toolkit.toolkitApi.RespondingGateway
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
/**
 * Build Initiating Gateway and n Responding Gateways behind it.  Each RG
 * has a Registry/Repository pair initialized with a single two-Document
 * submission.
 *
 * One confusing point.  SimulatorConfig describes the simulator on the server.  SimConfig
 * is the simplified version that is passed across the remote API.  The SimConfig can be
 * converted to SimulatorConfig with
 *     ToolkitFactory.asSimulatorConfig(SimConfig)
 */

class GatewayBuilder {

    static build(ToolkitApi api, SimulatorBuilder spi, int numberCommunities, String userName, String environmentName, String patientId) {
        List<SimConfig> rgConfigs = []  // two elements - IG config and list of RG configs

        // build and initialize remote communities
        (1..numberCommunities).each {
            String id = String.format("rg%d", it)

            System.gc() // On my machine (Sunil's Windows box) this seems to be required because some xml files in the simulator directory are "in use" by Java
            spi.delete(id, userName)  // in case it already exists

            RespondingGateway respondingGateway = spi.createRespondingGateway(id, userName, environmentName)

            SimConfig rgSimConfig = spi.get(respondingGateway)

            // this expects full server version of simulator config
            // this call makes the configuration available as a site for the test client
            SimCache.addToSession(Installation.defaultSessionName(),  ToolkitFactory.asSimulatorConfig(rgSimConfig))

            // disable checking of Patient Identity Feed
            rgSimConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
            rgSimConfig = spi.update(rgSimConfig)

            rgConfigs << rgSimConfig

            // load patient id for 12318


            // load the reg/rep with two documents
            TestInstance testId = new TestInstance("15832")
            List<String> sections = null
            Map<String, String> qparams = new HashMap<>()
            qparams.put('$patientid$', patientId)

            List<Result> results = api.runTest(userName, respondingGateway.getFullId(), testId, sections, qparams, true)
            assert results.get(0).passed()
        }

        // create initiating gateway
        String igId = 'ig'
        spi.delete(igId, userName)  // in case it already exists
        InitiatingGateway initiatingGateway = spi.createInitiatingGateway(igId, userName, environmentName);
        SimConfig igConfig = spi.get(initiatingGateway)

        // link all responding gateways to initiating gateway
        List<String> rgConfigIds = rgConfigs.collect() { it.fullId }
        igConfig.setProperty(SimulatorProperties.respondingGateways, rgConfigIds)
        println igConfig.describe()
        igConfig = spi.update(igConfig)

        // this expects full server version of simulator config
        // this call makes the configuration available as a site for the test client
        SimCache.addToSession(Installation.defaultSessionName(),  ToolkitFactory.asSimulatorConfig(igConfig))


        [igConfig, rgConfigs]
    }
}
