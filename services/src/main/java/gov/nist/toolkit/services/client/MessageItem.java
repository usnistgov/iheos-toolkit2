package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.Serializable;

/**
 *
 */
public class MessageItem implements Serializable, IsSerializable {
    private TestInstance testInstance;
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
}
