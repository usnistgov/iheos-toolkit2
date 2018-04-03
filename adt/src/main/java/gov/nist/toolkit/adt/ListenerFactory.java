package gov.nist.toolkit.adt;

import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.xdsexception.client.ThreadPoolExhaustedException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 9/1/15.
 */
public class ListenerFactory {
    private static Logger logger = Logger.getLogger(ListenerFactory.class);


    private static int firstPort = 0;
    private static int lastPort = 0;
    private static int nextPort = 0;
    private static List<ThreadPoolItem> threadPool = new ArrayList<>();
    private static int timeoutinMilli = 5*1000;
    private static boolean isInitialized = false;

    static public void init(int _firstPort, int _lastPort) {
        firstPort = _firstPort;
        nextPort = _firstPort;
        lastPort = _lastPort;
        for (int i=firstPort; i<=lastPort; i++) {
            if (getThreadPoolItem(i) == null)
                threadPool.add(new ThreadPoolItem(i));
        }
        isInitialized = true;
    }

    static public void init(List<String> portRange) {
        init(Integer.parseInt(portRange.get(0)), Integer.parseInt(portRange.get(1)));
    }

    static public List<ThreadPoolItem> getAllRunningListeners() {
        List<ThreadPoolItem> listeners = new ArrayList<>();
        for(ThreadPoolItem threadPoolItem : threadPool) {
            if (threadPoolItem.isInUse())
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
        if (tpi.thread == null || !tpi.isInUse()) { // not started
            Thread thread = new Thread(new AdtSocketListener(tpi));
            tpi.thread = thread;
            thread.start();
        }
        logger.info("Available ports are " + ListenerFactory.availablePorts());
        return tpi.port;
    }

    public static int generateListener(String simId, int port, PifCallback pifCallback) {
        if (Installation.isTestRunning() && !isInitialized) {
            return 0;
        }
        ThreadPoolItem tpi = getThreadPoolItem(port);
        if (tpi == null)
            tpi = allocateThreadPoolItem(port);
        tpi.simId = simId;
        tpi.pifCallback = pifCallback;
        tpi.timeoutInMilli = timeoutinMilli;
//        threadPool.add(tpi);
        logger.info("Launching listener for simId " + simId + " on port " + tpi.port);
        tpi.setInUse(true);
        logger.info("Available ports are " + ListenerFactory.availablePorts());
        if (tpi.thread == null) {  // not started
            Thread thread = new Thread(new AdtSocketListener(tpi));
            tpi.thread = thread;
            thread.start();
        }
        return tpi.port;
    }

    public static int allocatePort(String simId) {
        if (Installation.isTestRunning()) {
           if (!isInitialized) {
//                 Call the Pid service
               try {
                   String endpoint =
                           "http://"
                            + Installation.instance().propertyServiceManager().getToolkitHost()
                            + ":"
                            + Installation.instance().propertyServiceManager().getToolkitPort()
                            + "/testEnvPidPort?cmd=allocate&simId=" + simId;
                   logger.debug("Requesting new port from " + endpoint);
                   String pidPort = HttpClient.httpGet(endpoint);
                   pidPort = pidPort.trim();
                   return Integer.parseInt(pidPort);
               } catch (Exception e) {
                   throw new ToolkitRuntimeException(e.getCause());
               }
           }
        }

        ThreadPoolItem threadPoolItem =  allocateThreadPoolItem();
        threadPoolItem.simId = simId;
        return threadPoolItem.port;

    }

    public static void terminate(String simId) {
        if (Installation.isTestRunning()) {
            if (!isInitialized) {
//                 Call the Pid service
                try {
                    String endpoint =
                            "http://"
                                    + Installation.instance().propertyServiceManager().getToolkitHost()
                                    + ":"
                                    + Installation.instance().propertyServiceManager().getToolkitPort()
                                    + "/testEnvPidPort?cmd=terminate&simId=" + simId;
                    logger.debug("Requesting terminate port from " + endpoint);
                    HttpClient.httpGet(endpoint);
                    return;
                } catch (Exception e) {
                    throw new ToolkitRuntimeException(e.getCause());
                }
            }
        }

        logger.info("terminate patientIdentityFeed listener for sim " + simId + "...");
        ThreadPoolItem tpi = getItem(simId);
        if (tpi == null) {
            logger.info("...none");
            return;
        }
        logger.info("...which is port " + tpi.port + ", thread is not null? " + (tpi.thread!=null) + ", and is in use? " + tpi.isInUse());
        if (tpi.thread!=null) // Not started
            tpi.thread.interrupt();
        tpi.setInUse(false);
    }

    public static void terminateAll() {
        for (ThreadPoolItem tpi : threadPool) {
            if (tpi.isInUse()) {
                if (tpi.thread != null)
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
            if (!tm.isInUse()) {
                tm.setInUse(true);
                return tm;
            }

        for (ThreadPoolItem tm : threadPool)
            if (tm.isInUse())
                System.out.println(" in use: " + tm.simId);

        throw new ThreadPoolExhaustedException("Thread pool exhausted - cannot allocate ADT patientIdentityFeed. pool has " + threadPool.size() + " items. First port is " + firstPort + ", last port is " + lastPort + ". isInitialized? " + isInitialized);
    }

    public static List<String> availablePorts() {
        List<String> ports = new ArrayList<>();
        for (ThreadPoolItem tm : threadPool) {
            if (!tm.isInUse())
                ports.add(Integer.toString(tm.getPort()));
        }
        return ports;
    }

    static ThreadPoolItem allocateThreadPoolItem(int port) {
        ThreadPoolItem item = getThreadPoolItem(port);
        if (item == null)
            throw new ToolkitRuntimeException("Cannot allocate patientIdentityFeed for port " + port + ". This is not a configured port for Toolkit");
        if (item.isInUse())
            throw new ToolkitRuntimeException("Cannot allocate patientIdentityFeed for port " + port + ". This port is already in use.");
        item.setInUse(true);
        return item;
    }
}
