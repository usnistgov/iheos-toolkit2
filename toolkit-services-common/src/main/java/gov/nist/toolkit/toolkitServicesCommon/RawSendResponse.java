package gov.nist.toolkit.toolkitServicesCommon;

/**
 *
 */
public interface RawSendResponse {
    String getRequestSoapHeader();

    void setRequestSoapHeader(String requestSoapHeader);

    String getRequestSoapBody();

    void setRequestSoapBody(String requestSoapBody);

    String getResponseSoapHeader();

    void setResponseSoapHeader(String responseSoapHeader);

    String getResponseSoapBody();

    void setResponseSoapBody(String responseSoapBody);
}
