package gov.nist.toolkit.services.server.orchestration


import gov.nist.toolkit.configDatatypes.client.Pid

import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked
/**
 * Build environment for testing Responding Gateway SUT.
 */
@TypeChecked
class RgxOrchestrationBuilder extends AbstractOrchestrationBuilder {
    Session session
    RgxOrchestrationRequest request
    ToolkitApi api
    Util util

    public RgxOrchestrationBuilder(ToolkitApi api, Session session, RgxOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        boolean sutSaml = false
        SimId sutSimId = null
        try {
            String home
            RgxOrchestrationResponse response = new RgxOrchestrationResponse()

            OrchestrationProperties orchProps

            SiteSpec rrSite

            Site site = gov.nist.toolkit.results.server.SiteBuilder.siteFromSiteSpec(request.siteUnderTest, session.id)
            if (site == null) return RawResponseBuilder.build(String.format("Responding Gateway under Test (%s) does not exist in site configurations.", request.siteUnderTest.toString()))
            rrSite = request.siteUnderTest
            response.siteUnderTest = rrSite
            home = site.home

            TestInstance testInstanceRG_Init = new TestInstance('RG.Init', request.testSession)
            MessageItem itemRG_Init = response.addMessage(testInstanceRG_Init, true, "")
            if (!request.isUseExistingState()) {
                // Submit test data / request
                try {
                    // TODO This is not coming through when setting up System Under Test Configuration
                    // Need to figure out why not.
                    rrSite.isTls = request.isUseTls()
                    rrSite.isTls = true
                    session.isTls = true
                    Pid dummyPid = new Pid("NA", "NA");
                    util.submit(request.testSession.value, rrSite, testInstanceRG_Init, dummyPid, home)
                    itemRG_Init.setSuccess(api.getTestLogs(testInstanceRG_Init).isSuccess())
                } catch (Exception e) {
                    itemRG_Init.setMessage("Initialization of " + request.siteUnderTest.name + " failed:\n" + e.getMessage())
                    itemRG_Init.setSuccess(false)
                }
            }

            if (orchProps)
                orchProps.save()

            return response
        }
        catch (Exception e) {
            return RawResponseBuilder.build(e)
        } finally {
            if (sutSaml) {
//                stsSce.setBooleanValue(true)
//                api.saveSimulator(sutSimConfig)
            }
        }
    }
}

