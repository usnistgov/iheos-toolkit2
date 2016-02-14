package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public interface StoredQueryRequest extends SimId {
    void setQueryId(String queryId);
    String getQueryId();
//    void setQueryParameters(QueryParametersResource queryParameters);
//    QueryParameters getQueryParameters();
    boolean isTls();
    void setTls(boolean tls);
}
