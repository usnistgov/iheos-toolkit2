package gov.nist.toolkit.valsupport.message;

import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 6/15/15.
 */
public class MessageBody {
    OMElement body;

    public MessageBody(OMElement body) {
        this.body = body;
    }

    public OMElement getBody() {
        return body;
    }

    public void setBody(OMElement body) {
        this.body = body;
    }

}
