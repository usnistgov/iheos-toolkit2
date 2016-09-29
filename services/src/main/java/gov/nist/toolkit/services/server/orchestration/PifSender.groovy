package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.services.client.AbstractOrchestrationRequest
import gov.nist.toolkit.services.client.PifType
import gov.nist.toolkit.services.server.ToolkitApi

/**
 *
 */
class PifSender {
    private OrchestrationProperties orchProps
    AbstractOrchestrationRequest request
    ToolkitApi api
    private Util util

    public PifSender(ToolkitApi api,  OrchestrationProperties orchProps, AbstractOrchestrationRequest request) {
        this.api = api
        this.orchProps = orchProps
        this.request = request
        util = new Util(api)
    }

    def send(Map<String, TestInstanceManager> pidNameMap) {
        if (orchProps.updated()) {
            if (request.pifType == PifType.V2) {
                // register patient ids with registry
                pidNameMap.each {
                    String pidId = it.key
                    TestInstanceManager testInstanceManager = it.value
                    try {
                        util.submit(request.userName, request.registrySut, testInstanceManager.testInstance, 'pif', PidBuilder.createPid(orchProps.getProperty(pidId)), null)
                    }
                    catch (Exception e) {
                        testInstanceManager.messageItem.setMessage("V2 Patient Identity Feed to " + request.registrySut.name + " failed\n" + e.getMessage());
                        testInstanceManager.messageItem.setSuccess(false);
                    }
                }
            }
        } else {  // pass back PIDs to client along with PIF status
            pidNameMap.each {
                TestInstanceManager testInstanceManager = it.value
                testInstanceManager.messageItem.setSuccess(api.getTestLogs(testInstanceManager.testInstance).isSuccess())
            }
        }
    }
}
