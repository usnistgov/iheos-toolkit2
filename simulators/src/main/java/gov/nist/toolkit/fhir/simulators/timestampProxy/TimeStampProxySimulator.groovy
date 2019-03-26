package gov.nist.toolkit.fhir.simulators.timestampProxy;

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked;

import java.io.IOException;

@TypeChecked
class TimeStampProxySimulator extends BaseActorSimulator  {
    protected SimCommon simCommon
    protected ErrorRecorder er = null

    void init(SimCommon common) {
        simCommon = common
        er = simCommon.getCommonErrorRecorder()
    }

    void init(SimulatorConfig asc) {
    }

    @Override
    boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        return false
    }
}
