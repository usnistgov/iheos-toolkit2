package gov.nist.toolkit.services.server;

/**
 * Created by bill on 10/22/15.
 */
public class EngineInitializationException extends RuntimeException {
    public EngineInitializationException(String msg) { super(msg); }
    public EngineInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public EngineInitializationException(Throwable cause) {
        super(cause);
    }
}
