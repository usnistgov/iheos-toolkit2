package gov.nist.toolkit.fhirServer.config;

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
 */
public class SimTracker {

    static Map<Long, SimContext> contextMap = new HashMap<Long, SimContext>();

    static public void setContext(SimContext cxt) {
        Long threadId = Thread.currentThread().getId();
        contextMap.put(threadId, cxt);
    }

    static public SimContext getContext() {
        Long threadId = Thread.currentThread().getId();
        return contextMap.get(threadId);
    }
}
