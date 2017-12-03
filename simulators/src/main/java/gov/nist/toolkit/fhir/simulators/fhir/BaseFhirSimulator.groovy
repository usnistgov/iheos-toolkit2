package gov.nist.toolkit.fhir.simulators.fhir

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine

/**
 *
 */
abstract class BaseFhirSimulator extends BaseActorSimulator {
    protected SimCommon simCommon;
    protected ErrorRecorder er = null;

    public void init(SimCommon common) {
        simCommon = common;
        er = simCommon.getCommonErrorRecorder();
    }

    /**
     * @param transactionType
     * @param mvc
     * @throws IOException
     */
    abstract public boolean run(TransactionType transactionType, MessageValidatorEngine mvc) throws IOException;

    /**
     * @param asc
     */
    public void init(SimulatorConfig asc) {
    }
}
