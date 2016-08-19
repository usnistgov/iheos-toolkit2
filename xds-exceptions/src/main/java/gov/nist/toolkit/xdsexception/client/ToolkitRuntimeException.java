package gov.nist.toolkit.xdsexception.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class ToolkitRuntimeException extends RuntimeException implements IsSerializable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ToolkitRuntimeException() {}

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
