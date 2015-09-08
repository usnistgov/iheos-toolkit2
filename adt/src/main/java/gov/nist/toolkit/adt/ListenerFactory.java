package gov.nist.toolkit.adt;

import gov.nist.toolkit.actorfactory.RegistryActorFactory;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimulatorFactory;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.IOException;
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
        for (int i=firstPort; i<=lastPort; i++) {
            threadPool.add(new ThreadPoolItem(i));
        }
    }

    static public void init() {
        if (inited) return;
        inited = true;
        List<String> portRange = Installation.installation().propertyServiceManager().getListenerPortRange();
        int from = Integer.parseInt(portRange.get(0));
        int to = Integer.parseInt(portRange.get(1));
        init(from, to);
    }

    static ThreadPoolItem getThreadPoolItem(int port) {
        for (ThreadPoolItem item : threadPool) {
            if (item.port == port)
                return item;
        }
        return null;
    }

    public static void generateCurrentlyConfiguredListeners() throws IOException, NoSimException, ClassNotFoundException {
        SimDb db = new SimDb();
        List<String> simIds = db.getSimulatorIdsforActorType(ATFactory.ActorType.REGISTRY);
        generateListeners(simIds);
    }

    public static void terminateCurrentlyConfiguredListeners() throws IOException, NoSimException {
        SimDb db = new SimDb();
        List<String> simIds = db.getSimulatorIdsforActorType(ATFactory.ActorType.REGISTRY);
        for (String simId : simIds)
            terminate(simId);
    }

    public static void generateListeners(List<String> simIds) throws NoSimException, IOException, ClassNotFoundException {
        init();
        for (String simId : simIds) {
            generateListener(SimulatorFactory.loadSimulator(simId));
        }
    }

    public static void generateListener(SimulatorConfig simulatorConfig) {
        init();
        SimulatorConfigElement sce = simulatorConfig.get(RegistryActorFactory.pif_port);
        if (sce == null)
            throw new ToolkitRuntimeException("Simulator " + simulatorConfig.getId() + " is a Registry simulator but has no Patient ID Feed port configured");
        String portString = sce.asString();
        if (portString == null || portString.equals(""))
            throw new ToolkitRuntimeException("Simulator " + simulatorConfig.getId() + " is a Registry simulator but has no Patient ID Feed port configured");
        int port = Integer.parseInt(portString);
        ThreadPoolItem tpi = allocateThreadPoolItem(port);

        tpi.simId = simulatorConfig.getId();
        tpi.timeoutInMilli = timeoutinMilli;
        threadPool.add(tpi);
        Thread thread = new Thread(new AdtSocketListener(tpi));
        tpi.thread = thread;
        thread.start();
    }

    public static int generateListener(String simId) {
        init();
        ThreadPoolItem tpi = allocateThreadPoolItem();
        tpi.simId = simId;
        tpi.timeoutInMilli = timeoutinMilli;
        threadPool.add(tpi);
        Thread thread = new Thread(new AdtSocketListener(tpi));
        tpi.thread = thread;
        thread.start();
        return tpi.port;
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

    static ThreadPoolItem allocateThreadPoolItem() {
        for (ThreadPoolItem tm : threadPool)
            if (!tm.inUse) {
                tm.inUse = true;
                // Uses already allocated/assigned port
                return tm;
            }
        throw new ToolkitRuntimeException("Thread pool exhausted - cannot launch ADT listener");
    }

    static ThreadPoolItem allocateThreadPoolItem(int port) {
        ThreadPoolItem item = getThreadPoolItem(port);
        if (item == null)
            throw new ToolkitRuntimeException("Cannot allocate listener for port " + port + ". This is not a configured port for Toolkit");
        if (item.inUse)
            throw new ToolkitRuntimeException("Cannot allocate listener for port " + port + ". This port is already in use.");
        item.inUse = true;
        return item;
    }
}
