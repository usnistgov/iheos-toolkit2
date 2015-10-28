package gov.nist.toolkit.transactionNotificationService;

/**
 * Created by bill on 10/10/15.
 */
public interface TransactionNotification {
    void notify(TransactionLog log);
}
