package gov.nist.toolkit.services.server.orchestration

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
import gov.nist.toolkit.sitemanagement.client.SiteSpec
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

            boolean reuse = false  // updated as we progress
            supportSimId = new SimId(request.userName, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.REPOSITORY, pidNameMap.keySet())
            Pid pid

            response.repSite = new SiteSpec(request.sutSite.name)
            response.repSite.orchestrationSiteName = supportSimId.toString()
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
            if (orchProps.getProperty("pid") != null) {
                pid = PidBuilder.createPid(orchProps.getProperty("pid"))
            } else {
                pid  = session.allocateNewPid()
                orchProps.setProperty("pid", pid.asString())
            }
            response.setPid(pid)


            // disable checking of Patient Identity Feed
            if (!reuse) {
                SimulatorConfigElement idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
                idsEle.setValue(false)

                api.saveSimulator(supportSimConfig)
            }
            orchProps.save()

            // if SUT is simulator and it does not have a Register endpoint, add endpoint from
            // support sim
            // This is a Integration Test convenience
            SimulatorConfig sutSim = null
            try {
                sutSim = api.getConfig(new SimId(request.sutSite.name))
            } catch (Exception e) {}
            if (sutSim == null) {
                // not a sim
            } else {
                // is a sim
                String registerEndpoint = sutSim.getConfigEle(SimulatorProperties.registerEndpoint).asString()
                if (registerEndpoint == null || registerEndpoint.equals("")) {
                    // set in endpoint from support site
                    String endpoint = supportSimConfig.getConfigEle(SimulatorProperties.registerEndpoint).asString()
//                    sutSim.add(new SimulatorConfigElement(SimulatorProperties.registerEndpoint, ParamType.ENDPOINT, endpoint))
                    api.saveSimulator(sutSim)
                }
            }

            response.regConfig = supportSimConfig     //
            response.supportSite = new SiteSpec(request.sutSite.name)
            response.supportSite.orchestrationSiteName = supportSimId.toString()
            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
