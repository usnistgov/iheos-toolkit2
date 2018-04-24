package gov.nist.toolkit.simulators.sim.fixed

import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked
import org.apache.log4j.Logger

@TypeChecked
class FixedReplySimulator extends BaseDsActorSimulator {
    static Logger logger = Logger.getLogger(FixedReplySimulator.class)

    static List<TransactionType> transactions = new ArrayList<>();

    static {
        transactions.add(TransactionType.ANY)
    }


    FixedReplySimulator() {}

    FixedReplySimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
        super(dsSimCommon.simCommon, dsSimCommon)
        this.db = dsSimCommon.simCommon.db;
        this.response = dsSimCommon.simCommon.response;
        setSimulatorConfig(simulatorConfig)
        init()
    }

    FixedReplySimulator(SimulatorConfig simulatorConfig) {
        setSimulatorConfig(simulatorConfig)
    }


    @Override
    boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        dsSimCommon.simCommon.vc.isPnR = true
        dsSimCommon.simCommon.vc.isResponse = true
        String resultFileName = getSimulatorConfig().getConfigEle(SimulatorProperties.replyFile).asString()
        File resultFile = new File(resultFileName)
        assert resultFile.exists()

        dsSimCommon.sendHttpResponse(Util.parse_xml(resultFile), er, true)
        return false
    }

    @Override
    void init() {

    }
}
