package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.Pid
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.SiteBuilder
import gov.nist.toolkit.results.client.SiteSpec
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RgOrchestrationRequest
import gov.nist.toolkit.services.client.RgOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import groovy.transform.TypeChecked

/**
 * Build environment for testing Responding Gateway SUT.
 */
@TypeChecked
class RgOrchestrationBuilder {
    Session session
    RgOrchestrationRequest request
    Pid pid
    ToolkitApi api
    Util util

    public RgOrchestrationBuilder(ToolkitApi api, Session session, RgOrchestrationRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        try {
            RgOrchestrationResponse response = new RgOrchestrationResponse()

            // clear out test session
            new SimDb().getSimIdsForUser(request.userName).each { SimId simId -> api.deleteSimulator(simId) }

            String supportIdName = 'support'
            SimId supportId
            SimulatorConfig supportSimConfig
            SiteSpec rrSite

            if (request.useSimAsSUT) {
                // RG and RR defined in one site - a sim
                supportId = new SimId(request.userName, supportIdName, ActorType.RESPONDING_GATEWAY.name, request.environmentName)
                supportSimConfig = api.createSimulator(supportId).getConfig(0)
                rrSite = new SiteSpec(supportSimConfig.id.toString())
                response.siteUnderTest = rrSite
                response.regrepSite = rrSite
                response.sameSite = true
            } else {
                if (request.useExposedRR) {
                    // RG and RR in same site - verify site contents
                    Site site = SiteBuilder.siteFromSiteSpec(request.siteUnderTest, session.id)
                    if (site == null) return RawResponseBuilder.build(String.format("RG under Test (%s) does not exist in site configurations."))
                    // TODO - document that SUT with exposed RR must support PIF v2 or have PID validation disabled
                    if (site.hasTransaction(TransactionType.PROVIDE_AND_REGISTER)) return RawResponseBuilder.build("RG under test is not configured to accept a Provide and Register transaction.")
                    rrSite = request.siteUnderTest
                    response.siteUnderTest = rrSite
                    response.regrepSite = rrSite
                    response.sameSite = true
                } else {
                    // build RR sim - pass back details for configuration of SUT
                    // SUT and supporting RR are defined by different sites
                    supportId = new SimId(request.userName, supportIdName, ActorType.REPOSITORY_REGISTRY.name, request.environmentName)
                    supportSimConfig = api.createSimulator(supportId).getConfig(0)
                    rrSite = new SiteBuilder().siteSpecFromSimId(supportId)
                    response.siteUnderTest =request.siteUnderTest
                    response.regrepSite = rrSite
                    response.sameSite = false
                }
            }

            if (supportSimConfig)
                SimCache.addToSession(Installation.defaultSessionName(), supportSimConfig)

            pid = session.allocateNewPid()

            // register patient id with registry
            util.submit(request.userName, rrSite, new TestInstance("15804"), 'section', pid, null)

            response.pid = pid

            return response;
        }
        catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }



}

