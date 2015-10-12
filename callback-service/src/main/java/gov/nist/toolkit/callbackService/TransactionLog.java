package gov.nist.toolkit.callbackService;

/**
 * Created by bill on 10/10/15.
 */
public interface TransactionLog {
    String getRequestMessageHeader();
    String getRequestMessageBody();
    String getResponseMessageHeader();
    String getResponseMessageBody();
    String getCallbackClassName();
}
