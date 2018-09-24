package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorOption
import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.shared.IheItiProfile
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.results.client.SiteBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
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
class DocAdminOrchestrationBuilder extends AbstractOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private DocAdminOrchestrationRequest request
    private Util util
    private ActorOption actorOption = new ActorOption()

    DocAdminOrchestrationBuilder(ToolkitApi api, Session session, DocAdminOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
        this.actorOption.copyFrom(request.actorOption)
    }

    RawResponse buildTestEnvironment() {
        if (IheItiProfile.XDS.equals(actorOption.profileId)) {
            return buildXdsTestEnvironment()
        } else {
            return RawResponseBuilder.build(new Exception("DocAdmin orchestration does not support this Profile part: " + actorOption.toString()))
        }
    }

    RawResponse buildXdsTestEnvironment() {
        try {
            String supportIdName = 'docadmin_support'
            SimId simId
            SimulatorConfig simConfig

            DocAdminOrchestrationResponse response = new DocAdminOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid: new TestInstanceManager(request, response, '15817'),
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            simId = new SimId(request.testSession, supportIdName, ActorType.REGISTRY.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.REGISTRY, pidNameMap.keySet(), forceNewPatientIds)

            if (!request.useExistingState) {
                api.deleteSimulatorIfItExists(simId)
                orchProps.clear()
            }

            if (api.simulatorExists(simId)) {
                simConfig = api.getConfig(simId)
            } else {
                simConfig = api.createSimulator(simId).getConfig(0)
            }

            SimulatorConfigElement muOptionEle
            muOptionEle = simConfig.getConfigEle(SimulatorProperties.UPDATE_METADATA_OPTION)
            muOptionEle.setBooleanValue(true)

            api.saveSimulator(simConfig)

            Pid registerPid
            if (orchProps.getProperty("pid") != null && !forceNewPatientIds) {
                registerPid = PidBuilder.createPid(orchProps.getProperty("pid"))
            } else {
                registerPid = session.allocateNewPid()
                orchProps.setProperty("pid", registerPid.asString())
            }
            response.setRegisterPid(registerPid)

            TestInstance testInstance15817 = new TestInstance('15817', request.testSession)
            MessageItem orchTest15817 = response.addMessage(testInstance15817, true, "")

            if (orchProps.updated()) {
                try {
                    // send necessary Patient ID Feed messages
                    new PifSender(api, request.testSession, new SiteBuilder().siteSpecFromSimId(simId), orchProps).send(PifType.V2, pidNameMap)

                    // Initialize Registry for Stored Query testing
                    Map<String, String> parms = new HashMap<>();
                    parms.put('$patientid$', registerPid.toString())
                } catch (Exception ex) {
                    orchTest15817.setMessage("Initialization of " + simId.toString() + " failed:\n" + ex.getMessage())
                    orchTest15817.setSuccess(false)
                }
            }

            response.config = simConfig
            response.regSite = SimCache.getSite(simId.toString(), request.testSession)
            orchProps.save()

            return response

        } catch (Exception e) {
            return RawResponseBuilder.build(e)
        }
    }


}
