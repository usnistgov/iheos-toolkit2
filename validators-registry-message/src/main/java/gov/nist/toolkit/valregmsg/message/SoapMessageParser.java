package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 6/19/15.
 */
public class SoapMessageParser extends MessageValidator {
    OMElement envelope;
    List<OMElement> headers = new ArrayList<>();
    List<OMElement> bodies = new ArrayList<>();

    public SoapMessageParser(ValidationContext vc, OMElement envelope) {
        super(vc);
        this.envelope = envelope;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        this.er = er;
        er.registerValidator(this);
        parse();
        er.unRegisterValidator(this);
    }

    void parse() {
        headers = XmlUtil.childrenWithLocalName(envelope, "Header");
        bodies = XmlUtil.childrenWithLocalName(envelope, "Body");
    }

    public OMElement getEnvelope() { return envelope; }
    public OMElement getHeader() {
        if (headers.size() > 0)
            return headers.get(0);
        return null;
    }
    public List<OMElement> getHeaders() { return headers; }
    public OMElement getBody() {
        if (bodies.size() > 0)
            return bodies.get(0);
        return null;
    }
    public List<OMElement> getBodies() { return bodies; }
}
