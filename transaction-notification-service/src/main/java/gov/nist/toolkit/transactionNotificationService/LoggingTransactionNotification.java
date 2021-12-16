package gov.nist.toolkit.transactionNotificationService;

import java.util.logging.Logger;

/**
 * Created by bill on 10/11/15.
 */
public class LoggingTransactionNotification implements TransactionNotification {
    static final Logger logger = Logger.getLogger(LoggingTransactionNotification.class.getName());

    @Override
    public void notify(TransactionLog log) {
        logger.info("Toolkit transaction notify called...\n"
                + "...User " + log.getSimulatorUser() + "\n"
                + "...SimulatorId " + log.getSimulatorId() + "\n"
                + log.getRequestMessageHeader());
    }
}
