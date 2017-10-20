package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorOption
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RecOrchestrationRequest
import gov.nist.toolkit.services.client.RecOrchestrationResponse
import gov.nist.toolkit.services.client.SrcOrchestrationRequest
import gov.nist.toolkit.services.client.SrcOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class SrcOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private SrcOrchestrationRequest request
    private Util util
    private ActorOption actorOption = new ActorOption()

    public SrcOrchestrationBuilder(ToolkitApi api, Session session, SrcOrchestrationRequest request) {
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
        this.actorOption.copyFrom(request.actorOption)
    }

    RawResponse buildTestEnvironment() {

        try {
            String supportIdName = 'mhdrec_support'
            SimId simId;
            SimulatorConfig simConfig

            SrcOrchestrationResponse response = new SrcOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid: new TestInstanceManager(request, response, ''), // No testId needed since PIF won't be sent
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            simId = new SimId(request.userName, supportIdName, ActorType.MHD_DOC_RECIPIENT.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.MHD_DOC_RECIPIENT, pidNameMap.keySet(), !request.useExistingState)

            if (!request.useExistingState) {
                api.deleteSimulatorIfItExists(simId)
                orchProps.clear()
            }

            if (api.simulatorExists(simId)) {
                simConfig = api.getConfig(simId)
            } else {
                simConfig = api.createSimulator(simId).getConfig(0)
            }

//            if (!request.isUseExistingSimulator()) {
//                // disable checking of Patient Identity Feed
//                SimulatorConfigElement idsEle = simConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
//                idsEle.setBooleanValue(false)
//                api.saveSimulator(simConfig)
//            }
            orchProps.save()

            response.config = simConfig
            response.supportSite = SimCache.getSite(session.getId(), simId.toString())

            return response

        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }

    }

}
