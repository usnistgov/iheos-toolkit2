package gov.nist.toolkit.fhir.simulators.sim.rg;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.fhir.simulators.servlet.SimServlet;
import gov.nist.toolkit.fhir.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.metadataModel.RegIndex;
import gov.nist.toolkit.fhir.simulators.sim.rep.od.OddsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */
public class ODRGActorSimulator extends BaseDsActorSimulator {
    static final Logger logger = Logger.getLogger(ODRGActorSimulator.class);
    RegistryActorSimulator reg;
    OddsActorSimulator odds;
    RGActorSimulator rg;

    public ODRGActorSimulator() {
        odds = new OddsActorSimulator();
        reg = new RegistryActorSimulator();
        rg = new RGActorSimulator();
    }

    @Override
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        if (odds.supports(transactionType))
            return odds.run(transactionType, mvc, validation);
        if (reg.supports(transactionType))
            return reg.run(transactionType, mvc, validation);
        return rg.run(transactionType, mvc, validation);
    }

    @Override
    public void init() {
        odds.init();
        reg.init();
        rg.init();
    }

    @Override
   public void init(DsSimCommon c, SimulatorConfig config) {
        odds.init(c, config);
        reg.init(c, config);
        rg.init(c, config);
    }

    static public SimulatorStats getSimulatorStats(SimId simId) throws IOException, NoSimException {
        RegIndex regIndex = SimServlet.getRegIndex(simId);
        return regIndex.getSimulatorStats();
    }


    @Override
    public void onCreate(SimulatorConfig config) {
        odds.onCreate(config);
        reg.onCreate(config);
        rg.onCreate(config);
    }

    @Override
    public void onDelete(SimulatorConfig config) {
        odds.onDelete(config);
        reg.onDelete(config);
        rg.onDelete(config);
    }

    @Override
    public void onServiceStart(SimulatorConfig config) {
        odds.onServiceStart(config);
        reg.onServiceStart(config);
        rg.onServiceStart(config);
    }

    @Override
    public void onServiceStop(SimulatorConfig config) {
        odds.onServiceStop(config);
        reg.onServiceStop(config);
        rg.onServiceStop(config);
    }

}
