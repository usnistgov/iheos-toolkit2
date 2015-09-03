package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 9/1/15.
 */
public class ListenerFactory {
    static Logger logger = Logger.getLogger(ListenerFactory.class);

    static int firstPort = 0;
    static int lastPort = 0;
    static int nextPort = 0;
    static List<ThreadPoolItem> threadPool = new ArrayList<>();
    static boolean inited = false;
    static int timeoutinMilli = 5*1000;

    static void init(int _firstPort, int _lastPort) {
        firstPort = _firstPort;
        nextPort = _firstPort;
        lastPort = _lastPort;
    }

    static public void init() {
        if (inited) return;
        inited = true;
        List<String> portRange = Installation.installation().propertyServiceManager().getListenerPortRange();
        int from = Integer.parseInt(portRange.get(0));
        int to = Integer.parseInt(portRange.get(1));
        init(from, to);
    }

    public static void generateListeners(List<String> simIds) {
        init();
        for (String simId : simIds) {
            generateListener(simId);
        }
    }

    public static void generateListener(String simId) {
        init();
        ThreadPoolItem tpi = allocateThreadPoolItem();
        tpi.simId = simId;
        tpi.timeoutInMilli = timeoutinMilli;
        threadPool.add(tpi);
        Thread thread = new Thread(new AdtSocketListener(tpi));
        tpi.thread = thread;
        thread.start();
    }

    public static void terminate(String simId) {
        logger.info("terminate listener for sim " + simId + "...");
        ThreadPoolItem tpi = getItem(simId);
        if (tpi == null) return;
        logger.info("...which is port " + tpi.port);
        tpi.thread.interrupt();
    }

    static ThreadPoolItem getItem(String simId) {
        for (ThreadPoolItem tpi : threadPool) {
            if (tpi.simId != null && tpi.simId.equals(simId))
                return tpi;
        }
        return null;
    }

    static boolean hasNextPort() { return nextPort <= lastPort; }

    static int getNextPort() {
        if (nextPort > lastPort) return 0;
        int ret = nextPort;
        nextPort++;
        return ret;
    }

    static ThreadPoolItem allocateThreadPoolItem() {
        for (ThreadPoolItem tm : threadPool)
            if (!tm.inUse) {
                tm.inUse = true;
                // Uses already allocated/assigned port
                return tm;
            }
        if (hasNextPort()) {
            ThreadPoolItem tm = new ThreadPoolItem();
            tm.inUse = true;
            tm.port = getNextPort();
            return tm;
        }
        throw new ToolkitRuntimeException("Thread pool exhausted - cannot launch ADT listener");
    }

}
