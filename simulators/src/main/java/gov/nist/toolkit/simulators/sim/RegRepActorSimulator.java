package gov.nist.toolkit.simulators.sim;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import java.io.IOException;

/**
 * Created by bill on 9/14/15.
 */

public class RegRepActorSimulator extends BaseDsActorSimulator {
    RegistryActorSimulator reg;
    RepositoryActorSimulator rep;

    public RegRepActorSimulator() {
        rep = new RepositoryActorSimulator();
        reg = new RegistryActorSimulator();
    }

    public void init() {
        rep.init();
        reg.init();
    }

    public void init(DsSimCommon c, SimulatorConfig config) {
        rep.init(c, config);
        reg.init(c, config);
    }

    @Override
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        if (rep.supports(transactionType))
            return rep.run(transactionType, mvc, validation);
        return reg.run(transactionType, mvc, validation);
    }

    @Override
    public void onCreate(SimulatorConfig config) {
        rep.onCreate(config);
        reg.onCreate(config);
    }

    @Override
    public void onDelete(SimulatorConfig config) {
        rep.onDelete(config);
        reg.onDelete(config);
    }

    @Override
    public void onServiceStart(SimulatorConfig config) {
        rep.onServiceStart(config);
        reg.onServiceStart(config);
    }

    @Override
    public void onServiceStop(SimulatorConfig config) {
        rep.onServiceStop(config);
        reg.onServiceStop(config);
    }

}
