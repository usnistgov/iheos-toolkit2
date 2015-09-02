package gov.nist.toolkit.adt;

import org.apache.log4j.Logger;

/**
 * Created by bill on 9/2/15.
 */
public class ThreadPoolItem {
    static Logger logger = Logger.getLogger(ThreadPoolItem.class);

    int port;
    boolean inUse = false;
    Thread thread = null;
    String simId;
    int timeoutInMilli = 0;

    public void release() {
        logger.info("Release port " + port);
        inUse = false;
        thread = null;
        simId = null;
    }
}
