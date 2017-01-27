package gov.nist.toolkit.simulators.sim;

import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */

public class RegRepActorSimulator extends BaseDsActorSimulator {
    static final Logger logger = Logger.getLogger(RegRepActorSimulator.class);
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
        if (transactionType.isIdentifiedBy("xdrpr")) {
            GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
            rep.setForward(false);  // do not forward Register
            boolean ok = rep.run(transactionType, mvc, validation);
            if (!ok) return ok;
            ValidationContext vc = rep.getCommon().getValidationContext();
            vc.isXDRMinimal = rep.getValidationContext().isXDRMinimal;
            reg.setValidationContext(vc);
            reg.setGenerateResponse(false);
            reg.run(TransactionType.REGISTER, mvc, validation);
            DsSimCommon registerDsCommon = reg.getDsSimCommon();
            RegistryResponseGeneratorSim rrg = new RegistryResponseGeneratorSim(rep.getCommon(), registerDsCommon);

            mvc.addMessageValidator("Attach Errors", rrg, gerb.buildNewErrorRecorder());

            // wrap in soap wrapper and http wrapper
            // auto-detects need for multipart/MTOM
            mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(rep.getCommon(), registerDsCommon, rrg), gerb.buildNewErrorRecorder());

            mvc.run();
            return true;
        }
        if (rep.supports(transactionType) || transactionType.isIdentifiedBy("xdrpr") ) {
            return rep.run(transactionType, mvc, validation);
        }
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
