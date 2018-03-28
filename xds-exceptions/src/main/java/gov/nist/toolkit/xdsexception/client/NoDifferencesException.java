package gov.nist.toolkit.xdsexception.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class NoDifferencesException extends RuntimeException implements IsSerializable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NoDifferencesException() {}

    public NoDifferencesException(String msg) {
        super(msg);
    }


    public NoDifferencesException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoDifferencesException(Throwable cause) {
        super(cause);
    }
}
