package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.SiteBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.IgOrchestrationRequest
import gov.nist.toolkit.services.client.IgOrchestrationResponse
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
class IgOrchestrationBuilder {
    Session session
    IgOrchestrationRequest request
    Pid oneDocPid
    Pid twoDocPid
    Pid twoRgPid
    ToolkitApi api
    Util util
    List<SimulatorConfig> rgConfigs = []
    SimulatorConfig igConfig = null

    public IgOrchestrationBuilder(ToolkitApi api, Session session, IgOrchestrationRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            new SimDb().getSimIdsForUser(request.userName).each { SimId simId -> api.deleteSimulator(simId) }

            oneDocPid = session.allocateNewPid()
            twoDocPid = session.allocateNewPid()
            twoRgPid = session.allocateNewPid()

            buildRGs()

            String home1 = rgConfigs.get(0).get(SimulatorProperties.homeCommunityId).asString()

            // Submit test data
            util.submit(request.userName, SiteBuilder.siteSpecFromSimId(rgConfigs.get(0).id), new TestInstance("15807"), 'onedoc1', oneDocPid, home1)
            util.submit(request.userName, SiteBuilder.siteSpecFromSimId(rgConfigs.get(0).id), new TestInstance("15807"), 'twodoc', twoDocPid, home1)

            Map<String, String> params
            params = [
                    '$patientid$': twoRgPid.asString(),
                    '$testdata_home$': home1,
                    '$testdata_repid$': rgConfigs[0].getConfigEle(SimulatorProperties.repositoryUniqueId).asString()]
            util.submit(request.userName, SiteBuilder.siteSpecFromSimId(rgConfigs.get(0).id), new TestInstance("15807"), 'onedoc2', params)

            String home2 = rgConfigs.get(1).get(SimulatorProperties.homeCommunityId).asString()
            params = [
                    '$patientid$': twoRgPid.asString(),
                    '$testdata_home$': home2,
                    '$testdata_repid$': rgConfigs[1].getConfigEle(SimulatorProperties.repositoryUniqueId).asString()]
            util.submit(request.userName, SiteBuilder.siteSpecFromSimId(rgConfigs.get(1).id), new TestInstance("15807"), 'onedoc3', params)

            IgOrchestrationResponse response = new IgOrchestrationResponse()
            response.oneDocPid = oneDocPid
            response.twoDocPid = twoDocPid
            response.twoRgPid = twoRgPid
            response.simulatorConfigs = rgConfigs
            response.igSimulatorConfig = igConfig

            return response
        } catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }

    void buildRGs() {
        // build and initialize remote communities
        String id1 = 'rg1'
        String id2 = 'rg2'
        SimId rgSimId1 = new SimId(request.userName, id1, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        SimId rgSimId2 = new SimId(request.userName, id2, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
        println "creating rg1 sim"
        SimulatorConfig rgSimConfig1 = api.createSimulator(rgSimId1).getConfig(0)
        println "creating rg2 sim"
        SimulatorConfig rgSimConfig2 = api.createSimulator(rgSimId2).getConfig(0)

        // this expects full server version of simulator config
        // this call makes the configuration available as a site for the test client
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig1)
        SimCache.addToSession(Installation.defaultSessionName(), rgSimConfig2)

        SimulatorConfigElement rgEle
        // disable checking of Patient Identity Feed
        rgEle = rgSimConfig1.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
        rgEle.setValue(false)
        // set fixed homeCommunityId
        rgEle = rgSimConfig1.getConfigEle(SimulatorProperties.homeCommunityId)
        rgEle.setValue('urn:oid:1.2.34.567.8.1')
        api.saveSimulator(rgSimConfig1)

        // disable checking of Patient Identity Feed
        rgEle = rgSimConfig2.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
        rgEle.setValue(false)
        // set fixed homeCommunityId
        rgEle = rgSimConfig2.getConfigEle(SimulatorProperties.homeCommunityId)
        rgEle.setValue('urn:oid:1.2.34.567.8.2')
        api.saveSimulator(rgSimConfig2)

        rgConfigs << rgSimConfig1
        rgConfigs << rgSimConfig2

        if (request.includeLinkedIG) {
            // create initiating gateway
            String igId = 'ig'
            SimId igSimId = new SimId(request.userName, igId, ActorType.INITIATING_GATEWAY.name, request.environmentName)
            igConfig = api.createSimulator(igSimId).getConfig(0);

            // link all responding gateways to initiating gateway
            List<String> rgConfigIds = rgConfigs.collect() { SimulatorConfig rg -> rg.id.toString() }
            SimulatorConfigElement rgs = igConfig.getConfigEle(SimulatorProperties.respondingGateways)
            rgs.setValue(rgConfigIds)
            api.saveSimulator(igConfig)
        }
    }
}