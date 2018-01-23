package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.results.client.LogIdType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.AbstractOrchestrationRequest
import gov.nist.toolkit.services.client.AbstractOrchestrationResponse
import gov.nist.toolkit.services.client.MessageItem
/**
 * Create and initialize a TestInstance
 */
class TestInstanceManager {
    TestInstance testInstance
    MessageItem messageItem

    public TestInstanceManager(AbstractOrchestrationRequest request, AbstractOrchestrationResponse response, String testId) {
        testInstance = initializeTestInstance(request.testSession, new TestInstance(testId))
        messageItem = response.addMessage(testInstance, true, "");  // all default to success until shown otherwise
    }

    static public TestInstance initializeTestInstance(TestSession testSession, TestInstance testInstance) {
        testInstance.setTestSession(testSession);
        testInstance.setLocation(Installation.instance().testLogCache(testSession).toString())
        testInstance.setIdType(LogIdType.SPECIFIC_ID)
        return testInstance;
    }

}
