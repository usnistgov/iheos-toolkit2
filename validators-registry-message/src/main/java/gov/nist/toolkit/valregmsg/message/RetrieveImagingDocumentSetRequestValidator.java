package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageBodyContainer;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

import java.util.List;

/**
 * Validate a RetrieveImagingDocumentSetRequest message.
 * @author bill
 *
 */
public class RetrieveImagingDocumentSetRequestValidator  extends AbstractMessageValidator {
	OMElement xml;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public RetrieveImagingDocumentSetRequestValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc) {
		super(vc);
		this.erBuilder = erBuilder;
		this.mvc = mvc;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);

		MessageBodyContainer cont = (MessageBodyContainer) mvc.findMessageValidator("MessageBodyContainer");
		xml = cont.getBody();

		if (xml == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA145");
			er.err(XdsErrorCode.Code.XDSRepositoryError, assertion, this, "", "");
			er.unRegisterValidator(this);
			return;
		}


		List<OMElement> documentRequests = XmlUtil.childrenWithLocalName(xml, "DocumentRequest");
		for (OMElement dr : documentRequests) {
			RetrieveOrderValidator rov = new RetrieveOrderValidator(vc);
			rov.setBody(dr);
			mvc.addMessageValidator("DocumentRequest element ordering", rov, erBuilder.buildNewErrorRecorder());
			if (vc.isXC) {
				OMElement homeElement = XmlUtil.firstChildWithLocalName(dr, "HomeCommunityId");
				if (homeElement == null) {
					Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA146");
					er.err(XdsErrorCode.Code.XDSMissingHomeCommunityId, assertion, this, "", "");
				} else {
					String homeValue = homeElement.getText();
					if (!homeValue.startsWith("urn:oid:")) {
						Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA147");
						er.err(XdsErrorCode.Code.XDSRepositoryError, assertion, this, "", "");
					}
				}
			}
			er.unRegisterValidator(this);
		}

	}
}
