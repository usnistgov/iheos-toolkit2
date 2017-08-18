package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.log4j.Logger

/**
 *
 */
class SimProxySimulator extends BaseActorSimulator {
    private static Logger logger = Logger.getLogger(SimProxySimulator.class)
    static List<TransactionType> transactions = TransactionType.asList()

    SimProxySimulator() {}
    
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        return false
    }

}
