package gov.nist.toolkit.adt;

import gov.nist.toolkit.xdsexception.ThreadPoolExhaustedException;
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

    static public void init(int _firstPort, int _lastPort) {
        firstPort = _firstPort;
        nextPort = _firstPort;
        lastPort = _lastPort;
        for (int i=firstPort; i<=lastPort; i++) {
            if (getThreadPoolItem(i) == null)
                threadPool.add(new ThreadPoolItem(i));
        }
    }

    static public void init(List<String> portRange) {
        init(Integer.parseInt(portRange.get(0)), Integer.parseInt(portRange.get(1)));
    }

    static public List<ThreadPoolItem> getAllRunningListeners() {
        List<ThreadPoolItem> listeners = new ArrayList<>();
        for(ThreadPoolItem threadPoolItem : threadPool) {
            if (threadPoolItem.inUse)
                listeners.add(threadPoolItem);
        }

        return listeners;
    }

    static ThreadPoolItem getThreadPoolItem(int port) {
        for (ThreadPoolItem item : threadPool) {
            if (item.port == port)
                return item;
        }
        return null;
    }

    public static int generateListener(String simId) {
        ThreadPoolItem tpi = allocateThreadPoolItem();
        tpi.simId = simId;
        tpi.timeoutInMilli = timeoutinMilli;
//        threadPool.add(tpi);
        if (tpi.thread == null) { // not started
            Thread thread = new Thread(new AdtSocketListener(tpi));
            tpi.thread = thread;
            thread.start();
        }
        logger.info("Available ports are " + ListenerFactory.availablePorts());
        return tpi.port;
    }

    public static int generateListener(String simId, int port, PifCallback pifCallback) {
        ThreadPoolItem tpi = getThreadPoolItem(port);
        if (tpi == null)
            tpi = allocateThreadPoolItem(port);
        tpi.simId = simId;
        tpi.pifCallback = pifCallback;
        tpi.timeoutInMilli = timeoutinMilli;
//        threadPool.add(tpi);
        logger.info("Launching listener for simId " + simId + " on port " + tpi.port);
        tpi.inUse = true;
        logger.info("Available ports are " + ListenerFactory.availablePorts());
        if (tpi.thread == null) {  // not started
            Thread thread = new Thread(new AdtSocketListener(tpi));
            tpi.thread = thread;
            thread.start();
        }
        return tpi.port;
    }

    public static int allocatePort(String simId) {
        ThreadPoolItem threadPoolItem =  allocateThreadPoolItem();
        threadPoolItem.simId = simId;
        return threadPoolItem.port;
    }

    public static void terminate(String simId) {
        logger.info("terminate patientIdentityFeed listener for sim " + simId + "...");
        ThreadPoolItem tpi = getItem(simId);
        if (tpi == null) return;
        logger.info("...which is port " + tpi.port);
        tpi.thread.interrupt();
        tpi.inUse = false;
    }

    public static void terminateAll() {
        for (ThreadPoolItem tpi : threadPool) {
            if (tpi.getInUse()) {
                tpi.thread.interrupt();
                tpi.release();
            }
        }
    }

    public static ThreadPoolItem getItem(String simId) {
        for (ThreadPoolItem tpi : threadPool) {
            if (tpi.simId != null && tpi.simId.equals(simId))
                return tpi;
        }
        return null;
    }

    static synchronized ThreadPoolItem allocateThreadPoolItem() {
        for (ThreadPoolItem tm : threadPool)
            if (!tm.inUse) {
                tm.inUse = true;
                return tm;
            }
        throw new ThreadPoolExhaustedException("Thread pool exhausted - cannot allocate ADT patientIdentityFeed");
    }

    public static List<String> availablePorts() {
        List<String> ports = new ArrayList<>();
        for (ThreadPoolItem tm : threadPool) {
            if (!tm.inUse)
                ports.add(Integer.toString(tm.getPort()));
        }
        return ports;
    }

    static ThreadPoolItem allocateThreadPoolItem(int port) {
        ThreadPoolItem item = getThreadPoolItem(port);
        if (item == null)
            throw new ToolkitRuntimeException("Cannot allocate patientIdentityFeed for port " + port + ". This is not a configured port for Toolkit");
        if (item.inUse)
            throw new ToolkitRuntimeException("Cannot allocate patientIdentityFeed for port " + port + ". This port is already in use.");
        item.inUse = true;
        return item;
    }
}
