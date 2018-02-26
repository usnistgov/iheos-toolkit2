package gov.nist.toolkit.itSupport.xc

import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.toolkitApi.InitiatingGateway
import gov.nist.toolkit.toolkitApi.RespondingGateway
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
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

            spi.delete(id, userName)  // in case it already exists

            RespondingGateway respondingGateway = spi.createRespondingGateway(id, userName, environmentName)

            SimConfig rgSimConfig = spi.get(respondingGateway)

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


        [igConfig, rgConfigs]
    }
}
