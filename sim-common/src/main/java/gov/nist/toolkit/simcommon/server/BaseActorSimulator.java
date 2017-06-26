package gov.nist.toolkit.simcommon.server;


import gov.nist.toolkit.simcommon.client.SimulatorConfig;

/**
 *
 */
public class BaseActorSimulator {
    // Services may need extension via hooks.  These are the hooks
    // They are meant to be overloaded
    public void onCreate(SimulatorConfig config) {}
    public void onDelete(SimulatorConfig config) {}

    public void onTransactionBegin(SimulatorConfig config) {}
    public void onTransactionEnd(SimulatorConfig config) {}

    // simulatorConfig guaranteed to be initialized
    public void onServiceStart(SimulatorConfig config) {}  // these two refer to Servlet start/stop
    public void onServiceStop(SimulatorConfig config) {}

}
