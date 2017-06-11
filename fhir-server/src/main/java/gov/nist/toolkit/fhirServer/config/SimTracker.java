package gov.nist.toolkit.fhirServer.config;

import gov.nist.toolkit.fhir.support.SimContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Each servlet request is against a single simulator. The HAPI
 * code structure does not allow us to pass additional context
 * around so we created this HACK.
 *
 * We define a context class SimContext.  Each simulator request has one.
 * Since we cannot pass it to the *ResourceProvider classes where it is
 * needed, we index it off the Thread.  Each servlet request is handled
 * by its own thread.  The thread.id is used to lookup the SimContext
 * instance.
 *
 * In theory this is similar to the Request Context of the Servlet specification.
 * Content is maintained that is relevant to the current request.
 */
public class SimTracker {

    static private final Map<Long, SimContext> contextMap = new HashMap<Long, SimContext>();

    static public void setContext(SimContext cxt) {
        Long threadId = Thread.currentThread().getId();
        contextMap.put(threadId, cxt);
    }

    static public SimContext getContext() {
        Long threadId = Thread.currentThread().getId();
        return contextMap.get(threadId);
    }
}
