package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.*
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.results.client.SiteBuilder
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked
/**
 * Build environment for testing Initiating Gateway SUT.
 *
 */
@TypeChecked
class IgxOrchestrationBuilder extends AbstractOrchestrationBuilder {
    Session session
    IgxOrchestrationRequest request
    ToolkitApi api
    Util util
    List<SimulatorConfig> rgConfigs = []
    SimulatorConfig igConfig = null
    OrchestrationProperties orchProps

    public IgxOrchestrationBuilder(ToolkitApi api, Session session, IgxOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            IgxOrchestrationResponse response = new IgxOrchestrationResponse()

//            boolean forceNewPatientIds = false

            orchProps = new OrchestrationProperties(session, request.testSession, ActorType.INITIATING_GATEWAY, null, false)

            List<SimId> simIds = buildRGs(null)

            Site rg1Site = SimCache.getSite(session.getId(), simIds[0].toString(), simIds[0].testSession)
            Site rg2Site = SimCache.getSite(session.getId(), simIds[1].toString(), simIds[1].testSession)

            String home0 = rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString()
            String home1 = rgConfigs.get(1).get(SimulatorProperties.homeCommunityId).asString()

            TestInstance testInstanceIG_Init_Community1 = new TestInstance('IG.Init.Community1', request.testSession)
            MessageItem itemRG_Init1 = response.addMessage(testInstanceIG_Init_Community1, true, "")
            TestInstance testInstanceIG_Init_Community2 = new TestInstance('IG.Init.Community2', request.testSession)
            MessageItem itemRG_Init2 = response.addMessage(testInstanceIG_Init_Community2, true, "")

            if (!request.useExistingState) {
                // send necessary Patient ID Feed messages
                request.setPifType(PifType.V2)

                // Submit test data
                SiteBuilder.siteSpecFromSimId(rgConfigs.get(0).id).isTls = false
                try {
                    Pid dummyPid = new Pid("NA", "NA");
                    SiteSpec rgCommunity1 = SiteBuilder.siteSpecFromSimId(rgConfigs.get(0).id)
                    util.submit(request.testSession.value, rgCommunity1, testInstanceIG_Init_Community1, dummyPid, home0)

                    SiteSpec rgCommunity2 = SiteBuilder.siteSpecFromSimId(rgConfigs.get(1).id)
                    util.submit(request.testSession.value, rgCommunity2, testInstanceIG_Init_Community2, dummyPid, home1)

//                    itemRG_Init.setSuccess(api.getTestLogs(testInstanceIG_Init_Community1).isSuccess())
//                    util.submit(request.testSession, rgCommunity1, new TestInstance("IG.Init.Community1", request.testSession), 'ccd', null, home0)
                } catch (Exception e) {
                    // TODO Failure
                    itemRG_Init1.setMessage("Initialization of RG Community 1  failed:\n" + e.getMessage())
                    itemRG_Init1.setSuccess(false)
                    itemRG_Init2.setMessage("Initialization of RG Community 2  failed:\n" + e.getMessage())
                    itemRG_Init2.setSuccess(false)
                }

            }

            response.oneDocPid = null
            response.twoDocPid = null
            response.twoRgPid = null
            response.unknownPid = null
            response.noAdOptionPid = null
            response.simulatorConfigs = rgConfigs
            response.igSimulatorConfig = igConfig
            response.supportRG1 = SimCache.getSite(simIds[0].toString(), simIds[0].testSession)
            response.supportRG2 = SimCache.getSite(simIds[1].toString(), simIds[1].testSession)

            orchProps.save();

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

    List<SimId> buildRGs(Pid unknownPid) {
        // build and initialize remote communities
        String id1 = 'community1'
        String id2 = 'community2'
        SimId rgSimId1 = new SimId(request.testSession, id1, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        SimId rgSimId2 = new SimId(request.testSession, id2, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        SimulatorConfig rgSimConfig1
        SimulatorConfig rgSimConfig2
        boolean reuse = false  // updated as we progress

        if (!request.isUseExistingState()) {
            try {
                api.deleteSimulator(rgSimId1);
            } catch (Exception e) {
                // ignore
            }
            try {
                api.deleteSimulator(rgSimId2);
            } catch (Exception e) {
                // ignore
            }
        }

        if (api.simulatorExists(rgSimId1)) {
            rgSimConfig1 = api.getConfig(rgSimId1)
            rgSimConfig2 = api.getConfig(rgSimId2)
            reuse = true
        } else {
            rgSimConfig1 = api.createSimulator(rgSimId1).getConfig(0)
            rgSimConfig2 = api.createSimulator(rgSimId2).getConfig(0)
        }

//        SimulatorConfigElement rgEle

        //
        // Initialize both supporting Responding Gateways
        //

        if (!reuse) {
            // disable checking of Patient Identity Feed
            rgSimConfig1.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED).setBooleanValue(false)

            // set fixed homeCommunityId
            rgSimConfig1.getConfigEle(SimulatorProperties.homeCommunityId).setStringValue('urn:oid:1.2.34.567.8.1')

            // set fixed repositoryUniqueId
            rgSimConfig1.getConfigEle(SimulatorProperties.repositoryUniqueId).setStringValue('1.2.34.567.10.101')

            api.saveSimulator(rgSimConfig1)

            // disable checking of Patient Identity Feed
            rgSimConfig2.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED).setBooleanValue(false)

            // set fixed homeCommunityId
            rgSimConfig2.getConfigEle(SimulatorProperties.homeCommunityId).setStringValue('urn:oid:1.2.34.567.8.2')
            // set fixed repositoryUniqueId

            rgSimConfig1.getConfigEle(SimulatorProperties.repositoryUniqueId).setStringValue('1.2.34.567.10.102')

            api.saveSimulator(rgSimConfig2)
        }

        rgConfigs << rgSimConfig1
        rgConfigs << rgSimConfig2

        if (request.includeLinkedIGX) {
            // create initiating gateway (SUT) to allow self test
            String igId = 'ig'
            SimId igSimId = new SimId(request.testSession, igId, ActorType.INITIATING_GATEWAY.name, request.environmentName)
            igConfig = api.createSimulator(igSimId).getConfig(0);

            // link all responding gateways to initiating gateway
            List<String> rgConfigIds = rgConfigs.collect() { SimulatorConfig rg -> rg.id.toString() }
            SimulatorConfigElement rgs = igConfig.getConfigEle(SimulatorProperties.respondingGateways)
            rgs.setStringListValue(rgConfigIds)
            api.saveSimulator(igConfig)
        }

        return [rgSimId1, rgSimId2]
    }
}
