package gov.nist.toolkit.fhir.simulators.timestampProxy;

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

@TypeChecked
class FilterProxySimulator extends BaseDsActorSimulator  {
    private static Logger logger = Logger.getLogger(FilterProxySimulator.class);

    protected ErrorRecorder er = null
    SimulatorConfig simulatorConfig = null

    boolean supports(TransactionType transactionType) {
        return true
    }

    FilterProxySimulator() {}

    FilterProxySimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
        super(dsSimCommon.simCommon, dsSimCommon);
        this.db = dsSimCommon.simCommon.db;
        this.response = dsSimCommon.simCommon.response;
        this.setSimulatorConfig(simulatorConfig);
        init();
    }

    void init() {

    }

    @Override
    boolean run(TransactionType transactionType /* ignored */, MessageValidatorEngine mvc, String validation) throws IOException {
        // re-send without using Axiom and Axis2

        String header = dsSimCommon.simDb().getRequestMessageHeader();
        byte[] bodyBytes = dsSimCommon.simDb().getRequestMessageBody();
        String body = new String(bodyBytes);

        return false
    }
}
