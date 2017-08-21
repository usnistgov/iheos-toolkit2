package gov.nist.toolkit.simcommon.server;


import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import java.io.IOException;

/**
 *
 */
abstract public class BaseActorSimulator {
    public SimDb  db;
    public SimCommon common;
    SimulatorConfig config;

    abstract  public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException;

    public BaseActorSimulator() {}

    public BaseActorSimulator(SimCommon simCommon) {
        this.common = simCommon;
        db = simCommon.db;
    }

    public void init(SimulatorConfig config) {
        this.config = config;
    }

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
