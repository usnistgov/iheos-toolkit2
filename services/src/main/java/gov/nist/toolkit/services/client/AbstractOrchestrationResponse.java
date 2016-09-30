package gov.nist.toolkit.services.client;

import gov.nist.toolkit.results.client.TestInstance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
abstract public class AbstractOrchestrationResponse extends RawResponse {
//    private List<TestInstance> orchestrationTests = new ArrayList<>();  // test definitions used to build the orchestration
    private  String message = "";
    private List<MessageItem> messages = new ArrayList<>();

    public MessageItem addMessage(TestInstance testInstance, boolean success, String message) {
        MessageItem item = new MessageItem(testInstance, success, message);
        messages.add(item);
        return item;
    }

    public int getMessageItemCount() { return messages.size(); }

    public MessageItem getMessageItem(int i) {
        return messages.get(i);
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
