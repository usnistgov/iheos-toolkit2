package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a test instance which will be run to initialize a SUT or a
 * simulator that is part of orchestration for conformance testing.
 */
public class MessageItem implements Serializable, IsSerializable {
    private TestInstance testInstance;
    private Map<String,String> params = new HashMap<>();
    private String message;
    private boolean success;

    public MessageItem() {
    }

    MessageItem(TestInstance testInstance, boolean success, String message) {
        this.testInstance = testInstance;
        this.success = success;
        this.message = message;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
