package gov.nist.toolkit.adt;

import org.apache.log4j.Logger;

/**
 * Created by bill on 9/2/15.
 */
public class ThreadPoolItem {
    static Logger logger = Logger.getLogger(ThreadPoolItem.class);

    int port = 0;
    boolean inUse = false;
    Thread thread = null;
    String simId = null;
    int timeoutInMilli = 0;
    PifCallback pifCallback = null;

    public ThreadPoolItem() {}

    public ThreadPoolItem(int port) {
        this.port = port;
        logger.info("Allocate thread pool item for " + port);
    }

    public int getPort() { return port; }
    public boolean getInUse() { return inUse; }

    public void release() {
        logger.info("Release port " + port);
        inUse = false;
        thread = null;
        simId = null;
    }
}
