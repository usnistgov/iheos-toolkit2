package gov.nist.toolkit.callbackService;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class TransactionLogBean implements TransactionLog {
    private String requestMessageHeader;
    private String requestMessageBody;
    private String responseMessageHeader;
    private String responseMessageBody;
    // implements interface gov.nist.toolkit.callbackService.Callback
    private String callbackClassName;

    @Override
    public String getRequestMessageHeader() {
        return requestMessageHeader;
    }

    public void setRequestMessageHeader(String requestMessageHeader) {
        this.requestMessageHeader = requestMessageHeader;
    }

    @Override
    public String getRequestMessageBody() {
        return requestMessageBody;
    }

    public void setRequestMessageBody(String requestMessageBody) {
        this.requestMessageBody = requestMessageBody;
    }

    @Override
    public String getResponseMessageHeader() {
        return responseMessageHeader;
    }

    public void setResponseMessageHeader(String responseMessageHeader) {
        this.responseMessageHeader = responseMessageHeader;
    }

    @Override
    public String getResponseMessageBody() {
        return responseMessageBody;
    }

    public void setResponseMessageBody(String responseMessageBody) {
        this.responseMessageBody = responseMessageBody;
    }

    @Override
    public String getCallbackClassName() {
        return callbackClassName;
    }

    public void setCallbackClassName(String callbackClassName) {
        this.callbackClassName = callbackClassName;
    }


}
