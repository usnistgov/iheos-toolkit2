package gov.nist.toolkit.session.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class ConformanceSessionValidationStatus implements Serializable, IsSerializable {
    private boolean pass = true;
    private String message = "";

    public ConformanceSessionValidationStatus() {
    }

    public ConformanceSessionValidationStatus(boolean pass, String message) {
        this.pass = pass;
        this.message = message;
    }

    public boolean isPass() {
        return pass;
    }

    public String getMessage() {
        return message;
    }
}
