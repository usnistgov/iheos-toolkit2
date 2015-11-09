package gov.nist.toolkit.transactionNotificationService;

/**
 * Notification message.  A software component that is to be notified when a transaction is received implements
 * this interface in one of its classes.  The name of that class is added to the configuration of the simulator that
 * will receive the messages. When a message is received and after the ackowledgement is sent an instance of this
 * class will be built (must have a no-argument constructor) and the notify() method is called.
 */
public interface TransactionNotification {
    void notify(TransactionLog log);
}
