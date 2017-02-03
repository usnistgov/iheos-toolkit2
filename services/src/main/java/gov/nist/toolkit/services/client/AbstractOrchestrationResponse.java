package gov.nist.toolkit.services.client;

import gov.nist.toolkit.results.client.TestInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for Toolkit service responses for orchestration building.
 * Encapsulates MessageItems, tests which are run to initialize a SUT or sim
 * for conformance testing.
 */
abstract public class AbstractOrchestrationResponse extends RawResponse {
//    private List<TestInstance> orchestrationTests = new ArrayList<>();  // test definitions used to build the orchestration
    private List<MessageItem> messages = new ArrayList<>();

    /**
     * Does vendor initiate first message of test?
     * @return true if they do.
     */
    abstract public boolean isExternalStart();

    public MessageItem addMessage(TestInstance testInstance, boolean success, String message) {
        MessageItem item = new MessageItem(testInstance, success, message);
        messages.add(item);
        return item;
    }

    public Collection<MessageItem> getMessages() { return messages; }

    /**
     * Tests used to build up test environment
     * @return
     */
    public List<TestInstance> getTestInstances() {
        List<TestInstance> testInstances = new ArrayList<>();
        for (MessageItem item : messages) {
            testInstances.add(item.getTestInstance());
        }
        return testInstances;
    }

    public MessageItem getItemForTest(TestInstance testInstance) {
        for (MessageItem item : messages) {
            if (item.getTestInstance().equals(testInstance))
                return item;
        }
        return null;
    }

}
