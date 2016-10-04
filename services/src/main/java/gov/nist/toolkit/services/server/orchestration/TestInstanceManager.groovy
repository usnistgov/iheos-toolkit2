package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.LogIdType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.AbstractOrchestrationRequest
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse
import gov.nist.toolkit.services.client.MessageItem

/**
 *
 */
class TestInstanceManager {
    TestInstance testInstance
    MessageItem messageItem

    public TestInstanceManager(AbstractOrchestrationRequest request, AbstractOrchestrationResponse response, String testId) {
        testInstance = initializeTestInstance(request, new TestInstance(testId))
        messageItem = response.addMessage(testInstance, true, "");  // all default to success until shown otherwise
    }

    static public TestInstance initializeTestInstance(AbstractOrchestrationRequest request, TestInstance testInstance) {
        testInstance.setUser(request.getUserName());
        testInstance.setLocation(Installation.instance().testLogCache().toString())
        testInstance.setIdType(LogIdType.SPECIFIC_ID)
        return testInstance;
    }

}
