package gov.nist.toolkit.fhir.simulators.proxy.sim

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.fhir.simulators.sim.RegRepActorSimulator
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.log4j.Logger

class MhdRecipientSimulator extends RegRepActorSimulator {
    static final Logger logger = Logger.getLogger(MhdRecipientSimulator.class);
    SimProxySimulator simProx;

    static List<TransactionType> transactions = new ArrayList<>();

    static {
        transactions.add(TransactionType.PROV_DOC_BUNDLE);
    }

    public boolean supports(TransactionType transactionType) {
        return transactions.contains(transactionType);
    }


    MhdRecipientSimulator() {
        super()
        simProx = new SimProxySimulator()
    }


    @Override
    boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        if (transactionType == TransactionType.PROV_DOC_BUNDLE) {

            return true
        }
        return super.run(transactionType, mvc, validation)
    }

    @Override
    void init() {
        super.init()
        simProx.init()
    }


    @Override
    public void onCreate(SimulatorConfig config) {
        super.onCreate(config)
        simProx.onCreate(config);
    }

    @Override
    public void onDelete(SimulatorConfig config) {
        super.onDelete(config)
        simProx.onDelete(config);
    }

    @Override
    public void onServiceStart(SimulatorConfig config) {
        super.onServiceStart(config)
        simProx.onServiceStart(config);
    }

    @Override
    public void onServiceStop(SimulatorConfig config) {
        super.onServiceStop(config)
        simProx.onServiceStop(config);
    }
}
