package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RepOrchestrationRequest
import gov.nist.toolkit.services.client.RepOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.sitemanagement.client.Site
import groovy.transform.TypeChecked
/**
 * Build orchestration for testing a Repository.
 * A Registry sim is built and configured to not validate Register transactions agains PIF.
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
            String supportIdName = 'rep_test_support'
            SimId supportSimId
            SimulatorConfig supportSimConfig = null
            RepOrchestrationResponse response = new RepOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid:  new TestInstanceManager(request, response, ''), // No testId needed since PIF won't be sent
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            boolean reuse = false  // updated as we progress
            supportSimId = new SimId(request.userName, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.REPOSITORY, pidNameMap.keySet(), !request.useExistingState)
            Pid pid

            Site sutSite = SimCache.getSite(session.getId(), request.sutSite.name)
            response.repSite = sutSite
            if (!request.isUseExistingSimulator()) {
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
            response.setPid(pid)


            if (!request.isUseExistingSimulator()) {
                SimulatorConfigElement idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)

                // disable checking of Patient Identity Feed
                idsEle.setValue(false)

                api.saveSimulator(supportSimConfig)
            }
            orchProps.save()

            response.regConfig = supportSimConfig     //
            response.supportSite = SimCache.getSite(session.getId(), supportSimId.toString())

            // Transactions will be initiated on support site.  Link it to SUT site so that at the last minute
            // the SUT transactions will be added to support site.
            response.supportSite.setOrchestrationSiteName(sutSite.getSiteName())
            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
