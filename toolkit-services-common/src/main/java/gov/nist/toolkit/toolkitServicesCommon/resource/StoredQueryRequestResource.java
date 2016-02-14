package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.StoredQueryRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 */
@XmlRootElement
public class StoredQueryRequestResource  extends SimIdResource implements StoredQueryRequest, QueryParameters {
    String queryId;
    boolean tls = false;
    String key1 = null;
    //    List<String> values1 = new ArrayList<String>();
    String valuea1;

    public StoredQueryRequestResource() {}

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getValuea1() {
        return valuea1;
    }

    public void setValuea1(String valuea1) {
        this.valuea1 = valuea1;
    }

    @Override
    public String getQueryId() {
        return queryId;
    }
//
//    @Override
//    public void setQueryParameters(QueryParametersResource queryParameters) {
//        if (queryParameters.getParameterNames().size() > 0) {
//            key1 = queryParameters.getParameterNames().iterator().next();
//            valuea1 = queryParameters.getValues(key1).get(0);
//        }
//    }

//    @Override
//    public QueryParameters getQueryParameters() {
//        return null;
//    }

    @Override
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public boolean isTls() {
        return tls;
    }

    @Override
    public void setTls(boolean tls) {
        this.tls = tls;
    }

    @Override
    public String getValues(String paramName) {
        return null;
    }

    @Override
    public List<String> getParameterNames() {
        return null;
    }
}
