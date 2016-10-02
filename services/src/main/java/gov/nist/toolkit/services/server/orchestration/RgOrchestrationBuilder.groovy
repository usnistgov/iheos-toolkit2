package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.results.shared.SiteBuilder
import gov.nist.toolkit.services.client.MessageItem
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.client.RgOrchestrationRequest
import gov.nist.toolkit.services.client.RgOrchestrationResponse
import gov.nist.toolkit.services.server.RawResponseBuilder
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked

/**
 * Build environment for testing Responding Gateway SUT.
 */
@TypeChecked
class RgOrchestrationBuilder {
    Session session
    RgOrchestrationRequest request
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
            String home
            RgOrchestrationResponse response = new RgOrchestrationResponse()
            Map<String, TestInstanceManager> pidNameMap = [
                    simplePid:  new TestInstanceManager(request, response, '15823'),
//                    oneDocPid:  new TestInstanceManager(request, response, '15821'),
//                    twoDocPid:  new TestInstanceManager(request, response, '15822'),
//                    t12306Pid:  new TestInstanceManager(request, response, '12306'),
            ]

            OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.RESPONDING_GATEWAY, pidNameMap.keySet())

            Pid simplePid = PidBuilder.createPid(orchProps.getProperty("simplePid"))
//            Pid singleDocPid = PidBuilder.createPid(orchProps.getProperty("oneDocPid"))
//            Pid doubleDocPid = PidBuilder.createPid(orchProps.getProperty("twoDocPid"))
//            Pid t12306Pid = PidBuilder.createPid(orchProps.getProperty("t12306Pid"))

            response.setSimplePid(simplePid)
//            response.setOneDocPid(singleDocPid)
//            response.setTwoDocPid(doubleDocPid)
//            response.setT12306Pid(t12306Pid)

            String supportIdName = 'rg_support'
            SimId supportSimId
            SimulatorConfig supportSimConfig
            SiteSpec rrSite
            boolean reuse = false  // updated as we progress

            if (request.useExposedRR) {
                // RG and RR in same site - verify site contents
                Site site = SiteBuilder.siteFromSiteSpec(request.siteUnderTest, session.id)
                if (site == null) return RawResponseBuilder.build(String.format("RG under Test (%s) does not exist in site configurations."))
                // TODO - document that SUT with exposed RR must support PIF v2 or have PID validation disabled
                if (!site.hasTransaction(TransactionType.PROVIDE_AND_REGISTER)) return RawResponseBuilder.build("RG under test is not configured to accept a Provide and Register transaction.")
                rrSite = request.siteUnderTest
                request.registrySut = request.siteUnderTest  // PifSender expects this
                response.siteUnderTest = rrSite
                response.regrepSite = rrSite
                response.sameSite = true
                home = site.home
            } else {  // use external RR
                // build RR sim - pass back details for configuration of SUT
                // SUT and supporting RR are defined by different sites

                supportSimId = new SimId(request.userName, supportIdName, ActorType.REPOSITORY_REGISTRY.name, request.environmentName)
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

                // disable checking of Patient Identity Feed
                if (!reuse) {
                    SimulatorConfigElement idsEle = supportSimConfig.getConfigEle(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED)
                    idsEle.setValue(false)

                    api.saveSimulator(supportSimConfig)
                }
//                orchProps.save()

                rrSite = new SiteBuilder().siteSpecFromSimId(supportSimId)
                response.siteUnderTest = request.siteUnderTest
                response.regrepSite = rrSite
                response.sameSite = false
                response.regrepConfig = supportSimConfig;
                home = rrSite.homeId
            }

            if (supportSimConfig)
                SimCache.addToSession(Installation.defaultSessionName(), supportSimConfig)

            TestInstance testInstance12318 = new TestInstance('12318')
            MessageItem item12318 = response.addMessage(testInstance12318, true, "")

//            TestInstance testInstance15807 = new TestInstance("15807")
//            MessageItem item15807 = response.addMessage(testInstance15807, true, "")
//
//            TestInstance testInstance12306 = new TestInstance("12306")
//            MessageItem item12306 = response.addMessage(testInstance12306, true, "")

            if (orchProps.updated()) {
                // send necessary Patient ID Feed messages
                new PifSender(api, orchProps, request).send(pidNameMap)

                // Submit test data
                try {
                    util.submit(request.userName, rrSite, testInstance12318, simplePid, home)
                } catch (Exception e) {
//                response.addMessage(testInstance12318, false, "Provide and Register to " + rrSite.name + " failed");
                    item12318.setMessage("Initialization of " + request.siteUnderTest.name + " failed:\n" + e.getMessage())
                    item12318.setSuccess(false)
                }

//                try {
//                    util.submit(request.userName, rrSite, testInstance15807, 'onedoc1', singleDocPid, home)
//                } catch (Exception e) {
////                response.addMessage(testInstance15807, false, "Provide and Register to " + rrSite.name + " failed");
//                    item15807.setMessage("Initialization of " + request.siteUnderTest.name + " failed:\n" + e.getMessage())
//                    item15807.setSuccess(false)
//                }
//                try {
//                    util.submit(request.userName, rrSite, testInstance15807, 'twodoc', doubleDocPid, home)
//                } catch (Exception e) {
////                response.addMessage(testInstance15807, false, "Provide and Register to " + rrSite.name + " failed");
//                    item15807.setMessage("Initialization of " + request.siteUnderTest.name + " failed:\n" + e.getMessage())
//                    item15807.setSuccess(false)
//                }
//                try {
//                    util.submit(request.userName, rrSite, testInstance12306, t12306Pid)
//                } catch (Exception e) {
////                response.addMessage(testInstance15807, false, "Provide and Register to " + rrSite.name + " failed");
//                    item12306.setMessage("Initialization of " + request.siteUnderTest.name + " failed:\n" + e.getMessage())
//                    item12306.setSuccess(false)
//                }
            } else {
                item12318.setSuccess(api.getTestLogs(testInstance12318).isSuccess());
//                item15807.setSuccess(api.getTestLogs(testInstance15807).isSuccess());
//                item12306.setSuccess(api.getTestLogs(testInstance12306).isSuccess());
            }

            return response;
        }
        catch (Exception e) {
            return RawResponseBuilder.build(e);
        }
    }



}

