package gov.nist.toolkit.valsupport.message;

import org.apache.axiom.om.OMElement;

/**
 * All uses of this should move to MessageBodyContainer
 * Created by bill on 6/15/15.
 */
//TODO - All uses of this should move to MessageBodyContainer
@Deprecated
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
