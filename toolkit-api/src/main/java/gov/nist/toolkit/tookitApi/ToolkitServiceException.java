package gov.nist.toolkit.tookitApi;

/**
 * Created by bill on 10/9/15.
 */
public class ToolkitServiceException extends Exception {
    int code;

    public ToolkitServiceException(String msg) {
        super(msg);
    }

    public ToolkitServiceException(int code) {
        super("Error Code " + Integer.toString(code));
    }
}
