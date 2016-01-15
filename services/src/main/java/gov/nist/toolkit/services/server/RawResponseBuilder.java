package gov.nist.toolkit.services.server;

import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

/**
 *
 */
public class RawResponseBuilder {

    public static RawResponse build(String errorMessage) {
        return new RawResponse(errorMessage);
    }

    public static RawResponse build(Exception e) {
        return new RawResponse(e.getMessage()).setStackTrace(ExceptionUtil.stack_trace(e));
    }

    public static RawResponse build() { return new RawResponse(); }
}
