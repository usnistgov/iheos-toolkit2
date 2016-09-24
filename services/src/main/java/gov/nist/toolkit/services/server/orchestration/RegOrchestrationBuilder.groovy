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

        File orchestrationPropFile = Installation.installation().orchestrationPropertiesFile(request.userName, ActorType.REPOSITORY.shortName)
        Properties orchProps = new Properties()
        if (orchestrationPropFile.exists())
            orchProps.load(new FileInputStream(orchestrationPropFile))

        Pid pid

        if (request.isUseExistingSimulator() && orchProps.getProperty("pid") != null) {
            pid = PidBuilder.createPid(orchProps.getProperty("pid"))
        } else {
            orchProps.clear()
            pid  = session.allocateNewPid()
            orchProps.setProperty("pid", pid.asString())
            orchestrationPropFile.parentFile.mkdirs()
            orchProps.store(new FileOutputStream(orchestrationPropFile), null)
            initialize = true;
        }
        response.setPid(pid)

        TestInstance testInstance15817 = initializeTestInstance(new TestInstance("15817"))
        MessageItem item15817 = response.addMessage(testInstance15817, true, "");

        TestInstance testInstance12346 = initializeTestInstance(new TestInstance("12346"))
        MessageItem item12346 = response.addMessage(testInstance12346, true, "");

        TestInstance testInstance12374 = initializeTestInstance(new TestInstance("12374"))
        MessageItem item12374 = response.addMessage(testInstance12374, true, "");

        if (initialize) {
            // register patient id with registry
            if (request.pifType == PifType.V2) {
                try {
                    util.submit(request.userName, request.registrySut, testInstance15817, 'pif', pid, null)
                }
                catch (Exception e) {
                    item15817.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed");
                    item15817.setSuccess(false);
                }
            }

            // Initialize Registry for Stored Query testing
            Map<String, String> parms = new HashMap<>();
            parms.put('$patientid$', pid.toString())

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
            item12346.setSuccess(api.getTestLogs(testInstance12346).isSuccess());
            item12374.setSuccess(api.getTestLogs(testInstance12374).isSuccess());
        }

        return response
    }

    TestInstance initializeTestInstance(TestInstance testInstance) {
        testInstance.setUser(request.getUserName());
        testInstance.setLocation(Installation.installation().testLogCache().toString())
        testInstance.setIdType(LogIdType.SPECIFIC_ID)
        return testInstance;
    }


}
