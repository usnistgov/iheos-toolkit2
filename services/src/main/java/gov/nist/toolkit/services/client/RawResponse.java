package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Base class for Toolkit service responses. Encapsulates data for error returns
 */
public class RawResponse implements Serializable, IsSerializable {
    boolean error = false;
    String errorMessage = null;
    String stackTrace = null;

    public RawResponse() { }

    public RawResponse(String errorMessage) {
        setErrorMessage(errorMessage);
    }

    @Override
    public String toString() {
        return "RawResponse: " + ((error) ? "ERROR: " : "ok") + ((error) ? errorMessage : "");
    }

    public boolean isError() {
        return error;
    }

    public RawResponse setError(boolean error) {
        this.error = error;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public RawResponse setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        error = true;
        return this;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public RawResponse setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        error = true;
        return this;
    }
}
