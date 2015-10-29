package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Created by bill on 10/28/15.
 */
public interface SendRequest  extends SimId {
    String getTransactionName();

    void setTransactionName(String transactionName);

    boolean isTls();

    void setTls(boolean tls);

    String getMetadata();

    void setMetadata(String metadata);

    String getExtraHeaders();

    void setExtraHeaders(String extraHeaders);

}
