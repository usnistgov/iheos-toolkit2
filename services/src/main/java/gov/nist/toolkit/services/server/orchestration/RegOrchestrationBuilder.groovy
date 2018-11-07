package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RegOrchestrationBuilder extends AbstractOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private RegOrchestrationRequest request
    private Util util

    public RegOrchestrationBuilder(ToolkitApi api, Session session, RegOrchestrationRequest request) {
        super(session, request)
        this.api = api
        this.request = request
        this.session = session
        this.util = new Util(api)
    }

    RawResponse buildTestEnvironment() {
        // The response contains "messageItems" that really mean a set of orchestration tests.
        // Depending on the orchestration options selected the elements of a set, or tests, can vary.
        RegOrchestrationResponse response = new RegOrchestrationResponse()

        // TODO: Manual PIF
        // FIXME: If Manual PIF, exclude the PIF tests from being added to the Response through the TestInstanceManager constructor
        // If v2 PIF, every call to this TestInstanceManager constructor adds a TestInstance to the response object
        // If No PIF, no TI is added to the response object
        Map<String, TestInstanceManager> pidNameMap = [
                registerPid:  new TestInstanceManager(request, response, '15817'),
                registerAltPid:  new TestInstanceManager(request, response, '15817b'),
                sqPid:        new TestInstanceManager(request, response, '15818'),
                mpq1Pid:      new TestInstanceManager(request, response, '15819'),
                mpq2Pid:      new TestInstanceManager(request, response, '15820')
        ]

        SimId selfTestRegId = null
        if (request.selfTest()) {
            selfTestRegId = new SimId(request.testSession, 'reg_sut', ActorType.REGISTRY.name, request.environmentName)
        }

        boolean forceNewPatientIds = !request.isUseExistingState()

        // Step 1. Create Patient ID Strings if forceNewPatientIds is set to True
        OrchestrationProperties orchProps = new OrchestrationProperties(session, request.testSession, ActorType.REGISTRY, pidNameMap.keySet(), forceNewPatientIds)

        // TODO: Persist the PIF setting, otherwise, V2 is always selected and the NoPIF mode, if it were previously selected, is lost.
        orchProps.setProperty("pifType", request.pifType.name())

        SiteSpec registrySut
        if (selfTestRegId) {
            registrySut = selfTestRegId.siteSpec
            api.deleteSimulatorIfItExists(selfTestRegId)
            api.createSimulator(selfTestRegId)
        } else {
            registrySut = request.registrySut
        }
        response.setSut(registrySut)

        // Make Pid objects from String created in Step 1.
        Pid registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
        Pid registerAltPid = PidBuilder.createPid(orchProps.getProperty("registerAltPid"))
        Pid sqPid       = PidBuilder.createPid(orchProps.getProperty("sqPid"))
        Pid mpq1Pid     = PidBuilder.createPid(orchProps.getProperty("mpq1Pid"))
        Pid mpq2Pid     = PidBuilder.createPid(orchProps.getProperty("mpq2Pid"))

        // Assign Pid to the response
        response.setRegisterPid(registerPid)
        response.setRegisterAltPid(registerAltPid)
        response.setSqPid(sqPid)
        response.setMpq1Pid(mpq1Pid)
        response.setMpq2Pid(mpq2Pid)

        TestInstance testInstanceReadme = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("ManualPif-Readme", request.testSession))
        if (PifType.NONE == request.pifType) {
            MessageItem itemReadme = response.addMessage(testInstanceReadme, true, "")
        }

        TestInstance testInstance12346 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12346", request.testSession))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "")

        TestInstance testInstance12374 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12374", request.testSession))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "")

        TestInstance testInstance12361 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12361"))
        MessageItem item12361 = response.addMessage(testInstance12361, true, "")

        if (orchProps.updated()) {
            if (PifType.NONE == request.pifType) {
                List<TestInstance> tILogToBeDeleted = new ArrayList<>()
                tILogToBeDeleted.add(testInstanceReadme)
                tILogToBeDeleted.add(testInstance12346)
                tILogToBeDeleted.add(testInstance12374)
                tILogToBeDeleted.add(testInstance12361)
                session.getXdsTestServiceManager().delTestResults(tILogToBeDeleted, request.getEnvironmentName(), request.getTestSession())
            }

            // send necessary Patient ID Feed messages
            // FIXME: The .send method below always uses the V2 Option, need to use request.pifType
            // If Manual PIF, then do not Run any of the Load tests, just add the Load tests to the response.
            // Instruct the user to manuall Run the Load test.
            // Move the auto-generated PID section to the top of the Load tests.
            if (PifType.V2 == request.pifType) {
                new PifSender(api, request.testSession, request.registrySut, orchProps)
                    .send(PifType.V2, pidNameMap)

                // Initialize Registry for Stored Query testing
                Map<String, String> parms = new HashMap<>()
                parms.put('$patientid$', sqPid.toString())

                try {
                    request.registrySut.isTls = request.isUseTls()
                    util.submit(request.testSession.value, request.registrySut, testInstance12346, parms)
                } catch (Exception e) {
                    item12346.setSuccess(false)
                }

                try {
                    request.registrySut.isTls = request.isUseTls()
                    // This test submits/uses an separate "alternate" PID that is not part of the PidNameMap above
                    util.submit(request.testSession.value, request.registrySut, testInstance12374, parms)
                } catch (Exception e) {
                    item12374.setSuccess(false)
                }

                try {
                    request.registrySut.isTls = request.isUseTls()
                    // This test uses a patient Id from its Test plan UseReport instruction
                    util.submit(request.testSession.value, request.registrySut, testInstance12361, parms)
                } catch (Exception e) {
                    item12361.setSuccess(false)
                }
            }
        } else {
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess())
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess())
            item12361.setSuccess(api.getTestLogs(testInstance12361).isSuccess())
        }

        orchProps.save()

        return response
    }



}
