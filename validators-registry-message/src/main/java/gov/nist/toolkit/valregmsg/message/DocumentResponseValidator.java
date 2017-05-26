package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

public class DocumentResponseValidator extends AbstractMessageValidator {
	OMElement xml;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public DocumentResponseValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;

		OMElement home = XmlUtil.firstChildWithLocalName(xml, "HomeCommunityId");

		if (vc.isRet && vc.isResponse && vc.isXC)
			if (home == null) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA157");
				er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
			}
	}

}
