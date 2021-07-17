package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorOption
import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.shared.IheItiProfile
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.results.client.SiteBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.MessageItem
import gov.nist.toolkit.services.client.PifType
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.SrcOrchestrationRequest
import gov.nist.toolkit.services.client.SrcOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimCache
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class SrcOrchestrationBuilder extends AbstractOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private SrcOrchestrationRequest request
    private Util util
    private ActorOption actorOption = new ActorOption()

    public SrcOrchestrationBuilder(ToolkitApi api, Session session, SrcOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
        this.actorOption.copyFrom(request.actorOption)
    }

    RawResponse buildTestEnvironment() {
        /* if (IheItiProfile.MHD.equals(actorOption.profileId)) {
           return buildMhdTestEnvironment();
        } else */
        if (IheItiProfile.XDS.equals(actorOption.profileId)) {
            return buildXdsTestEnvironment();
        } else {
            return RawResponseBuilder.build(new Exception("Unrecognized profile: " + actorOption.toString()));
        }
    }

    RawResponse buildXdsTestEnvironment() {
        try {
            String supportIdName = "docsrc_support"
            SimId simId
            SimulatorConfig simConfig

            SrcOrchestrationResponse response = new SrcOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid: new TestInstanceManager(request, response, '15817'),
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            simId = new SimId(request.testSession, supportIdName, ActorType.REPOSITORY_REGISTRY.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.DOC_SOURCE, pidNameMap.keySet(), forceNewPatientIds)

            if (!request.useExistingState) {
                api.deleteSimulatorIfItExists(simId)
                orchProps.clear()
            }

            if (api.simulatorExists(simId)) {
                simConfig = api.getConfig(simId)
            } else {
                simConfig = api.createSimulator(simId).getConfig(0)
            }

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
            response.regrepSite = SimCache.getSite(simId.toString(), request.testSession)
            orchProps.save()

            return response

        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

    /*
    RawResponse buildMhdTestEnvironment() {
        try {
            String supportIdName = 'mhdrec_support'
            SimId simId;
            SimulatorConfig simConfig

            SrcOrchestrationResponse response = new SrcOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    pid: new TestInstanceManager(request, response, ''), // No testId needed since PIF won't be sent
            ]

            boolean forceNewPatientIds = !request.isUseExistingState()

            simId = new SimId(request.testSession, supportIdName, ActorType.MHD_DOC_RECIPIENT.name, request.environmentName)
            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.MHD_DOC_RECIPIENT, pidNameMap.keySet(), forceNewPatientIds)

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
            response.simProxySite = SimCache.getSite(simId.toString(), request.testSession)
            response.simProxyBeSite = SimCache.getSite(simId.toString() + "_be", request.testSession)
            response.regrepSite = SimCache.getSite(simId.toString() + "_regrep", request.testSession)

            return response

        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }
     */

}
