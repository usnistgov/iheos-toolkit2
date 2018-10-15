package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorOption
import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.shared.IheItiProfile
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.services.client.IsrOrchestrationRequest
import gov.nist.toolkit.services.client.IsrOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import groovy.transform.TypeChecked

/**
 * Build orchestration for testing a Isr.
 * A Registry sim is built and configured to not validate Register transactions against PIF.
 */
@TypeChecked
class IsrOrchestrationBuilder extends AbstractOrchestrationBuilder {
    Session session
    IsrOrchestrationRequest request
    ToolkitApi api
    Util util
    ActorOption actorOption = new ActorOption()

    public IsrOrchestrationBuilder(ToolkitApi api, Session session, IsrOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
        this.actorOption.copyFrom(request.actorOption)
    }

    RawResponse buildTestEnvironment() {
        if (IheItiProfile.XDS.equals(actorOption.profileId)) {
            return buildXdsTestEnvironment()
        } else {
            return RawResponseBuilder.build(new Exception("Isr orchestration does not support this Profile part: " + actorOption.toString()))
        }
    }

    RawResponse buildXdsTestEnvironment() {
        try {
            String supportIdName = 'isr_support'
            SimId supportSimId
            SimulatorConfig supportSimConfig = null
            IsrOrchestrationResponse response = new IsrOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid:  new TestInstanceManager(request, response, ''), // No testId needed since PIF won't be sent
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            boolean reuse = false  // updated as we progress
            supportSimId = new SimId(request.testSession, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.ISR, pidNameMap.keySet(), !request.useExistingState)
            Pid pid

            if (!request.isUseExistingState()) {
                api.deleteSimulatorIfItExists(supportSimId)
                orchProps.clear()
            }
            if (api.simulatorExists(supportSimId)) {
                supportSimConfig = api.getConfig(supportSimId)
                reuse = true
            } else {
                supportSimConfig = api.createSimulator(supportSimId).getConfig(0)
            }
            if (orchProps.getProperty("pid") != null && !forceNewPatientIds) {
                pid = PidBuilder.createPid(orchProps.getProperty("pid"))
            } else {
                pid  = session.allocateNewPid()
                orchProps.setProperty("pid", pid.asString())
            }
            response.setRegisterPid(pid)


            if (!request.isUseExistingState()) {
                SimulatorConfigElement idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)

                // disable checking of Patient Identity Feed
                idsEle.setBooleanValue(false)

                api.saveSimulator(supportSimConfig)
            }
            orchProps.save()

            response.config = supportSimConfig     //

            // Transactions will be initiated on support site.
            response.regSite = SimCache.getSite(supportSimId.toString(), request.testSession)

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
