package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class RawSendResponseResource implements RawSendResponse {
    String requestSoapHeader = null;
    String requestSoapBody = null;
    String responseSoapHeader = null;
    String responseSoapBody = null;

    public RawSendResponseResource() {}

    @Override
    public String getRequestSoapHeader() {
        return requestSoapHeader;
    }

    @Override
    public void setRequestSoapHeader(String requestSoapHeader) {
        this.requestSoapHeader = requestSoapHeader;
    }

    @Override
    public String getRequestSoapBody() {
        return requestSoapBody;
    }

    @Override
    public void setRequestSoapBody(String requestSoapBody) {
        this.requestSoapBody = requestSoapBody;
    }

    @Override
    public String getResponseSoapHeader() {
        return responseSoapHeader;
    }

    @Override
    public void setResponseSoapHeader(String responseSoapHeader) {
        this.responseSoapHeader = responseSoapHeader;
    }

    @Override
    public String getResponseSoapBody() {
        return responseSoapBody;
    }

    @Override
    public void setResponseSoapBody(String responseSoapBody) {
        this.responseSoapBody = responseSoapBody;
    }
}
