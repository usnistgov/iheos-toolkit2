package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.services.client.IdsOrchestrationRequest
import gov.nist.toolkit.services.client.IdsOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import groovy.transform.TypeChecked


/**
* Build environment for testing Initiating Gateway SUT.
*
*/

@TypeChecked
class IdsOrchestrationBuilder {
    Session session
    IdsOrchestrationRequest request
    ToolkitApi api
    Util util

    public IdsOrchestrationBuilder(ToolkitApi api, Session session, IdsOrchestrationRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            String supportIdName = 'rr'
            SimId supportId
            SimulatorConfig supportSimConfig
            IdsOrchestrationResponse response = new IdsOrchestrationResponse()

            supportId = new SimId(request.userName, supportIdName, ActorType.REPOSITORY_REGISTRY.name, request.environmentName)
            api.deleteSimulatorIfItExists(supportId)
            supportSimConfig = api.createSimulator(supportId).getConfig(0)

            SimulatorConfigElement idsEle
            // disable checking of Patient Identity Feed
            idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
            idsEle.setValue(false)

            idsEle = supportSimConfig.getConfigEle(SimulatorProperties.repositoryUniqueId)
            idsEle.setValue('1.2.3')

            api.saveSimulator(supportSimConfig)

            response.regrepConfig = supportSimConfig

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }
}