package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.services.client.IdsOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RepOrchestrationRequest
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RepOrchestrationBuilder {
    Session session
    RepOrchestrationRequest request
    ToolkitApi api
    Util util

    public RepOrchestrationBuilder(ToolkitApi api, Session session, RepOrchestrationRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            String supportIdName = 'rr'
            SimId supportId
            SimulatorConfig supportSimConfig = null
            IdsOrchestrationResponse response = new IdsOrchestrationResponse()

            boolean reuse = false
            supportId = new SimId(request.userName, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            if (request.isUseExistingSimulator()) {
                if (api.simulatorExists(supportId)) {
                    supportSimConfig = api.getConfig(supportId)
                    reuse = true
                } else {
                    supportSimConfig = api.createSimulator(supportId).getConfig(0)
                }
            } else {
                api.deleteSimulatorIfItExists(supportId)
                supportSimConfig = api.createSimulator(supportId).getConfig(0)
            }

            SimulatorConfigElement idsEle
            // disable checking of Patient Identity Feed
            if (!reuse) {
                idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
                idsEle.setValue(false)

                api.saveSimulator(supportSimConfig)
            }

            response.regrepConfig = supportSimConfig

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
