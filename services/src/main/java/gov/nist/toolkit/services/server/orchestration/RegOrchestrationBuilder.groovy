package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.LogIdType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.*
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
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

        boolean initialize = false;

        File orchestrationPropFile = Installation.instance().orchestrationPropertiesFile(request.userName, ActorType.REPOSITORY.shortName)
        Properties orchProps = new Properties()
        if (orchestrationPropFile.exists())
            orchProps.load(new FileInputStream(orchestrationPropFile))

        Pid registerPid
        Pid sqPid
        Pid mpq1Pid
        Pid mpq2Pid

        if (request.isUseExistingSimulator() && orchProps.getProperty("registerPid") != null) {
            registerPid = PidBuilder.createPid(orchProps.getProperty("registerPid"))
            sqPid = PidBuilder.createPid(orchProps.getProperty("sqPid"))
            mpq1Pid = PidBuilder.createPid(orchProps.getProperty("mpq1Pid"))
            mpq2Pid = PidBuilder.createPid(orchProps.getProperty("mpq2Pid"))
        } else {
            orchProps.clear()

            registerPid  = session.allocateNewPid()
            orchProps.setProperty("registerPid", registerPid.asString())

            sqPid  = session.allocateNewPid()
            orchProps.setProperty("sqPid", sqPid.asString())

            mpq1Pid  = session.allocateNewPid()
            orchProps.setProperty("mpq1Pid", mpq1Pid.asString())

            mpq2Pid  = session.allocateNewPid()
            orchProps.setProperty("mpq2Pid", mpq2Pid.asString())

            orchestrationPropFile.parentFile.mkdirs()
            orchProps.store(new FileOutputStream(orchestrationPropFile), null)
            initialize = true;
        }
        response.setRegisterPid(registerPid)
        response.setSqPid(sqPid)
        response.setMpq1Pid(mpq1Pid)
        response.setMpq2Pid(mpq2Pid)

        TestInstance testInstance15817 = initializeTestInstance(new TestInstance("15817"))
        MessageItem item15817 = response.addMessage(testInstance15817, true, "");

        TestInstance testInstance15818 = initializeTestInstance(new TestInstance("15818"))
        MessageItem item15818 = response.addMessage(testInstance15818, true, "");

        TestInstance testInstance15819 = initializeTestInstance(new TestInstance("15819"))
        MessageItem item15819 = response.addMessage(testInstance15819, true, "");

        TestInstance testInstance15820 = initializeTestInstance(new TestInstance("15820"))
        MessageItem item15820 = response.addMessage(testInstance15820, true, "");

        TestInstance testInstance12346 = initializeTestInstance(new TestInstance("12346"))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "");

        TestInstance testInstance12374 = initializeTestInstance(new TestInstance("12374"))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "");

        if (initialize) {
            // register patient id with registry
            if (request.pifType == PifType.V2) {
                try {
                    util.submit(request.userName, request.registrySut, testInstance15817, 'pif', registerPid, null)
                }
                catch (Exception e) {
                    item15817.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                    item15817.setSuccess(false);
                }
                try {
                    util.submit(request.userName, request.registrySut, testInstance15818, 'pif', sqPid, null)
                }
                catch (Exception e) {
                    item15818.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                    item15818.setSuccess(false);
                }
                try {
                    util.submit(request.userName, request.registrySut, testInstance15819, 'pif', mpq1Pid, null)
                }
                catch (Exception e) {
                    item15819.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                    item15819.setSuccess(false);
                }
                try {
                    util.submit(request.userName, request.registrySut, testInstance15820, 'pif', mpq2Pid, null)
                }
                catch (Exception e) {
                    item15820.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                    item15820.setSuccess(false);
                }
            }

            // Initialize Registry for Stored Query testing
            Map<String, String> parms = new HashMap<>();
            parms.put('$patientid$', sqPid.toString())

            try {
                util.submit(request.userName, request.registrySut, testInstance12346, parms);

            } catch (Exception e) {
                item12346.setMessage("Initialization of " + request.registrySut.name + " failed:\n" + e.getMessage());
                item12346.setSuccess(false);
            }

            try {
                util.submit(request.userName, request.registrySut, testInstance12374, parms);

            } catch (Exception e) {
                item12374.setMessage("Initialization of " + request.registrySut.name + " failed:\n" + e.getMessage());
                item12374.setSuccess(false);
            }
        } else {
            item15817.setSuccess(api.getTestLogs(testInstance15817).isSuccess());
            item15818.setSuccess(api.getTestLogs(testInstance15818).isSuccess());
            item15819.setSuccess(api.getTestLogs(testInstance15819).isSuccess());
            item15820.setSuccess(api.getTestLogs(testInstance15820).isSuccess());
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess());
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess());
        }

        return response
    }

    TestInstance initializeTestInstance(TestInstance testInstance) {
        testInstance.setUser(request.getUserName());
        testInstance.setLocation(Installation.instance().testLogCache().toString())
        testInstance.setIdType(LogIdType.SPECIFIC_ID)
        return testInstance;
    }


}
