package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.shared.TestSession;
import org.apache.log4j.Logger;

/**
 * Created by bill on 9/2/15.
 */
public class ThreadPoolItem {
    static Logger logger = Logger.getLogger(ThreadPoolItem.class);

    int port = 0;

    private boolean inUse = false;
    Thread thread = null;
    String simId = null;  // string because SimId is not available on classpath from here
    TestSession testSession;
    int timeoutInMilli = 0;
    PifCallback pifCallback = null;

    public ThreadPoolItem() {}

    public ThreadPoolItem(int port) {
        this.port = port;
        logger.info("ThreadPoolItem: Allocate thread pool item for " + port);
    }

    public int getPort() { return port; }

    public void release() {
        logger.info("ThreadPoolItem: Release port " + port);
        setInUse(false);
        thread = null;
        simId = null;
        testSession = null;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }


}
