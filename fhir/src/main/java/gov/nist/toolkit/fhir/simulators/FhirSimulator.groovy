package gov.nist.toolkit.fhir.simulators

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.log4j.Logger
/**
 * Right now this is pretty useless, a place holder.  Incoming
 * messages go right to the ResourceProvider.
 */
class FhirSimulator extends BaseFhirSimulator {
    private static Logger logger = Logger.getLogger(FhirSimulator.class);

    static List<TransactionType> transactions = new ArrayList<>();

    static {
        transactions.add(TransactionType.FHIR);
    }

    public RegistryActorSimulator() {}

    // This constructor can be used to implement calls to onCreate(), onDelete(),
    // onServiceStart(), onServiceStop()
    public RegistryActorSimulator(SimulatorConfig simulatorConfig) {
        setSimulatorConfig(simulatorConfig);
    }


    @Override
    boolean run(TransactionType transactionType, MessageValidatorEngine mvc) throws IOException {
        return false
    }
}
