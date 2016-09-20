package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.installation.Installation
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
            SimId supportId
            SimulatorConfig supportSimConfig = null
            RepOrchestrationResponse response = new RepOrchestrationResponse()

            boolean reuse = false  // updated as we progress
            supportId = new SimId(request.userName, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            File orchestrationPropFile = Installation.installation().orchestrationPropertiesFile(request.userName, ActorType.REPOSITORY.shortName)
            Properties orchProps = new Properties()
            boolean propertiesUpdated = false
            if (orchestrationPropFile.exists())
                orchProps.load(new FileInputStream(orchestrationPropFile))
            Pid pid

            response.repSite = new SiteSpec(request.sutSite.name)
            response.repSite.orchestrationSiteName = supportId.toString()
            if (!request.isUseExistingSimulator()) {
                api.deleteSimulatorIfItExists(supportId)
                orchProps.clear()
            }
            if (api.simulatorExists(supportId)) {
                supportSimConfig = api.getConfig(supportId)
                reuse = true
            } else {
                supportSimConfig = api.createSimulator(supportId).getConfig(0)
            }
            if (orchProps.getProperty("pid") != null) {
                pid = PidBuilder.createPid(orchProps.getProperty("pid"))
            } else {
                pid  = session.allocateNewPid()
                orchProps.setProperty("pid", pid.asString())
                propertiesUpdated = true
            }
            response.setPid(pid)

            SimulatorConfigElement idsEle
            // disable checking of Patient Identity Feed
            if (!reuse) {
                idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
                idsEle.setValue(false)

                api.saveSimulator(supportSimConfig)
            }
            if (propertiesUpdated) {
                orchestrationPropFile.parentFile.mkdirs()
                orchProps.store(new FileOutputStream(orchestrationPropFile), null)
            }

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
                    sutSim.add(new SimulatorConfigElement(SimulatorProperties.registerEndpoint, ParamType.ENDPOINT, endpoint))
                    api.saveSimulator(sutSim)
                }
            }

            response.regConfig = supportSimConfig     //
            response.supportSite = new SiteSpec(request.sutSite.name)
            response.supportSite.orchestrationSiteName = supportId.toString()
            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

}
