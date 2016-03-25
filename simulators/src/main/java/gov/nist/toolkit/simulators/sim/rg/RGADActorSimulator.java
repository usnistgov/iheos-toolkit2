package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simulators.servlet.SimServlet;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */
public class RGADActorSimulator extends BaseDsActorSimulator {
    static final Logger logger = Logger.getLogger(RGADActorSimulator.class);
    RegistryActorSimulator reg;
    RepositoryActorSimulator rep;
    RGActorSimulator rg;

    public RGADActorSimulator() {
        rep = new RepositoryActorSimulator();
        reg = new RegistryActorSimulator();
        rg = new RGActorSimulator();
    }

    @Override
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        if (rep.supports(transactionType) || transactionType.isIdentifiedBy("xdrpr"))
            return rep.run(transactionType, mvc, validation);
        if (reg.supports(transactionType))
            return reg.run(transactionType, mvc, validation);
        return rg.run(transactionType, mvc, validation);
    }

    @Override
    public void init() {
        rep.init();
        reg.init();
        rg.init();
    }

    public void init(DsSimCommon c, SimulatorConfig config) {
        rep.init(c, config);
        reg.init(c, config);
        rg.init(c, config);
    }

    static public SimulatorStats getSimulatorStats(SimId simId) throws IOException, NoSimException {
        RegIndex regIndex = SimServlet.getRegIndex(simId);
        return regIndex.getSimulatorStats();
    }


    @Override
    public void onCreate(SimulatorConfig config) {
        rep.onCreate(config);
        reg.onCreate(config);
        rg.onCreate(config);
    }

    @Override
    public void onDelete(SimulatorConfig config) {
        rep.onDelete(config);
        reg.onDelete(config);
        rg.onDelete(config);
    }

    @Override
    public void onServiceStart(SimulatorConfig config) {
        rep.onServiceStart(config);
        reg.onServiceStart(config);
        rg.onServiceStart(config);
    }

    @Override
    public void onServiceStop(SimulatorConfig config) {
        rep.onServiceStop(config);
        reg.onServiceStop(config);
        rg.onServiceStop(config);
    }

}
