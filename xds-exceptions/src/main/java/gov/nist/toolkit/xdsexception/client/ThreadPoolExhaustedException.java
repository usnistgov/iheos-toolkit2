package gov.nist.toolkit.xdsexception.client;

/**
 *
 */
public class ThreadPoolExhaustedException extends ToolkitRuntimeException {
    public ThreadPoolExhaustedException(String msg) {
        super(msg);
    }

    public ThreadPoolExhaustedException() { super(); }
}
