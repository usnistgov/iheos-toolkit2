package gov.nist.toolkit.transactionNotificationService;

/**
 * Created by bill on 10/10/15.
 */
public interface TransactionLog {
    String getSimulatorUser();
    String getSimulatorId();
    String getRequestMessageHeader();
    String getRequestMessageBody();
    String getResponseMessageHeader();
    String getResponseMessageBody();
}
