package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 6/19/15.
 */
public class SoapMessageParser extends MessageValidator {
    OMElement envelope;
    OMElement header;
    OMElement body;

    public SoapMessageParser(ValidationContext vc, OMElement envelope) {
        super(vc);
        this.envelope = envelope;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        this.er = er;

        parse();
    }

    void parse() {
        header = MetadataSupport.firstChildWithLocalName(envelope, "Header");
        body = MetadataSupport.firstChildWithLocalName(envelope, "Body");
    }

    public OMElement getEnvelope() { return envelope; }
    public OMElement getHeader() { return header; }
    public OMElement getBody() { return body; }
}
