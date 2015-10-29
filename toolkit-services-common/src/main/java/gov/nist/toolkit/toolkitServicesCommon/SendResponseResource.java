package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class SendResponseResource {
    String requestSoapHeader = null;
    String requestSoapBody = null;
    String responseSoapHeader = null;
    String responseSoapBody = null;

    public SendResponseResource() {}

    public String getRequestSoapHeader() {
        return requestSoapHeader;
    }

    public void setRequestSoapHeader(String requestSoapHeader) {
        this.requestSoapHeader = requestSoapHeader;
    }

    public String getRequestSoapBody() {
        return requestSoapBody;
    }

    public void setRequestSoapBody(String requestSoapBody) {
        this.requestSoapBody = requestSoapBody;
    }

    public String getResponseSoapHeader() {
        return responseSoapHeader;
    }

    public void setResponseSoapHeader(String responseSoapHeader) {
        this.responseSoapHeader = responseSoapHeader;
    }

    public String getResponseSoapBody() {
        return responseSoapBody;
    }

    public void setResponseSoapBody(String responseSoapBody) {
        this.responseSoapBody = responseSoapBody;
    }
}
