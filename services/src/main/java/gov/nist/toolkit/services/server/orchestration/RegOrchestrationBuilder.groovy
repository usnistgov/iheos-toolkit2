package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import groovy.transform.TypeChecked
/**
 *
 */
@TypeChecked
class RegOrchestrationBuilder {
    ToolkitApi api
    private Session session
    private RegOrchestrationRequest request
    private Util util

    public RegOrchestrationBuilder(ToolkitApi api, Session session, RegOrchestrationRequest request) {
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
            selfTestRegId = new SimId(request.userName, 'reg_sut', ActorType.REGISTRY.name, request.environmentName)
        }

        boolean forceNewPatientIds = !request.isUseExistingState()

        OrchestrationProperties orchProps = new OrchestrationProperties(session, request.userName, ActorType.REGISTRY, pidNameMap.keySet(), forceNewPatientIds)

        SiteSpec registrySut
        if (selfTestRegId) {
            registrySut = selfTestRegId.siteSpec
            api.deleteSimulatorIfItExists(selfTestRegId)
            api.createSimulator(selfTestRegId)
        } else {
            registrySut = request.registrySut
        }
        response.setSut(registrySut)

        Pid registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
        Pid registerAltPid = PidBuilder.createPid(orchProps.getProperty("registerAltPid"))
        Pid sqPid       = PidBuilder.createPid(orchProps.getProperty("sqPid"))
        Pid mpq1Pid     = PidBuilder.createPid(orchProps.getProperty("mpq1Pid"))
        Pid mpq2Pid     = PidBuilder.createPid(orchProps.getProperty("mpq2Pid"))

        response.setRegisterPid(registerPid)
        response.setRegisterAltPid(registerAltPid)
        response.setSqPid(sqPid)
        response.setMpq1Pid(mpq1Pid)
        response.setMpq2Pid(mpq2Pid)

        TestInstance testInstance12346 = TestInstanceManager.initializeTestInstance(request.getUserName(), new TestInstance("12346"))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "");

        TestInstance testInstance12374 = TestInstanceManager.initializeTestInstance(request.getUserName(), new TestInstance("12374"))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "");

        if (orchProps.updated()) {
            // send necessary Patient ID Feed messages
            new PifSender(api, request.getUserName(), request.registrySut, orchProps).send(PifType.V2, pidNameMap)

            // Initialize Registry for Stored Query testing
            Map<String, String> parms = new HashMap<>();
            parms.put('$patientid$', sqPid.toString())

            try {
                util.submit(request.userName, request.registrySut, testInstance12346, parms);

            } catch (Exception e) {
//                item12346.setMessage("Initialization of " + request.registrySut.name + " failed:\n" + e.getMessage());
                item12346.setSuccess(false);
            }

            try {
                util.submit(request.userName, request.registrySut, testInstance12374, parms);

            } catch (Exception e) {
//                item12374.setMessage("Initialization of " + request.registrySut.name + " failed:\n" + e.getMessage());
                item12374.setSuccess(false);
            }
        } else {
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess());
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess());
        }

        orchProps.save();

        return response
    }



}
