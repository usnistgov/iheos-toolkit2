package gov.nist.toolkit.xdsexception;

/**
 * Created by bill on 6/15/15.
 */
public class ToolkitRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public ToolkitRuntimeException(String msg) {
        super(msg);
    }


    public ToolkitRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ToolkitRuntimeException(Throwable cause) {
        super(cause);
    }
}
