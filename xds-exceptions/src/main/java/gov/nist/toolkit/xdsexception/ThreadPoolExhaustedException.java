package gov.nist.toolkit.xdsexception;

import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

/**
 * Created by bill on 10/9/15.
 */
public class ThreadPoolExhaustedException extends ToolkitRuntimeException {
    public ThreadPoolExhaustedException(String msg) {
        super(msg);
    }
}
