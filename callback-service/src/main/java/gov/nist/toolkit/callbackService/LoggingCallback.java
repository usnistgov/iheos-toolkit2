package gov.nist.toolkit.callbackService;

import org.apache.log4j.Logger;

/**
 * Created by bill on 10/11/15.
 */
public class LoggingCallback implements Callback {
    static final Logger logger = Logger.getLogger(LoggingCallback.class);

    @Override
    public void callback(TransactionLog log) {
        logger.info("Toolkit callback called...\n"
                + "...User " + log.getSimulatorUser() + "\n"
                + "...SimulatorId " + log.getSimulatorId() + "\n"
                + log.getRequestMessageHeader());
    }
}
