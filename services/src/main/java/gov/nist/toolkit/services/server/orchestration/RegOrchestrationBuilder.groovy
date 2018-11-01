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
        RegOrchestrationResponse response = new RegOrchestrationResponse()
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

        TestInstance testInstance12346 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12346", request.testSession))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "");

        TestInstance testInstance12374 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12374", request.testSession))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "");

        TestInstance testInstance12361 = TestInstanceManager.initializeTestInstance(request.testSession, new TestInstance("12361"))
        MessageItem item12361 = response.addMessage(testInstance12361, true, "");

        if (orchProps.updated()) {
            // send necessary Patient ID Feed messages
            new PifSender(api, request.testSession, request.registrySut, orchProps).send(PifType.V2, pidNameMap)

            // Initialize Registry for Stored Query testing
            Map<String, String> parms = new HashMap<>();
            parms.put('$patientid$', sqPid.toString())

            try {
                request.registrySut.isTls = request.isUseTls()
                util.submit(request.testSession.value, request.registrySut, testInstance12346, parms);

            } catch (Exception e) {
                item12346.setSuccess(false);
            }

            try {
                request.registrySut.isTls = request.isUseTls()
                util.submit(request.testSession.value, request.registrySut, testInstance12374, parms);

            } catch (Exception e) {
                item12374.setSuccess(false);
            }

            try {
                request.registrySut.isTls = request.isUseTls()
                util.submit(request.testSession.value, request.registrySut, testInstance12361, parms);

            } catch (Exception e) {
                item12361.setSuccess(false);
            }
        } else {
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess());
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess());
            item12361.setSuccess(api.getTestLogs(testInstance12361).isSuccess());
        }

        orchProps.save();

        return response
    }



}
