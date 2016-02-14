package gov.nist.toolkit.toolkitServicesCommon.resource;

import java.util.List;

/**
 *
 */

public interface QueryParameters {
    String getValues(String paramName);
    List<String> getParameterNames();
    void setQueryParameters(QueryParametersResource queryParameters);
}
