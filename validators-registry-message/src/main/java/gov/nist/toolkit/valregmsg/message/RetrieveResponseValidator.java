package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

import java.util.List;

/**
 * Validate a RetrieveResponse message.
 * @author bill
 *
 */
public class RetrieveResponseValidator extends AbstractMessageValidator {
	OMElement xml;
	IErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public RetrieveResponseValidator(ValidationContext vc, OMElement xml, IErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc) {
		super(vc);
		this.xml = xml;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		//er.registerValidator(this);

		if (xml == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA151");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
            //er.unRegisterValidator(this);
			return;
		}

		OMElement registryResponse = XmlUtil.firstChildWithLocalName(xml, "RegistryResponse");
		if (registryResponse == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA152");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
		}
		else {
			mvc.addMessageValidator("RegistryResponse", new RegistryResponseValidator(vc, registryResponse), erBuilder.buildNewErrorRecorder());
		}

		List<OMElement> documentRequests = XmlUtil.childrenWithLocalName(xml, "DocumentResponse");
		for (OMElement dr : documentRequests) {
			RetrieveResponseOrderValidator rov = new RetrieveResponseOrderValidator(vc);
			rov.setBody(dr);
			mvc.addMessageValidator("DocumentResponse element ordering", rov, erBuilder.buildNewErrorRecorder());
			mvc.addMessageValidator("DocumentResponse Validator", new DocumentResponseValidator(vc, dr), erBuilder.buildNewErrorRecorder());
		}
        //er.unRegisterValidator(this);

	}

}
