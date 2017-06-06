package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.ErrorRecorderFactory;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageBodyContainer;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

import java.util.List;

/**
 * Validate a RetreiveDocumentSetRequest message.
 * @author bill
 *
 */
public class RetrieveRequestValidator  extends AbstractMessageValidator {
	OMElement xml;
	IErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public RetrieveRequestValidator(ValidationContext vc, IErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc) {
		super(vc);
		this.erBuilder = erBuilder;
		this.mvc = mvc;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;

		MessageBodyContainer cont = (MessageBodyContainer) mvc.findMessageValidator("MessageBodyContainer");
		xml = cont.getBody();

		if (xml == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA148");
			er.err(XdsErrorCode.Code.XDSRepositoryError, assertion, this, "", "");
			return;
		}


		List<OMElement> documentRequests = XmlUtil.childrenWithLocalName(xml, "DocumentRequest");
		for (OMElement dr : documentRequests) {
			RetrieveOrderValidator rov = new RetrieveOrderValidator(vc);
			rov.setBody(dr);
			mvc.addMessageValidator("DocumentRequest element ordering", rov, ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder());
			if (vc.isXC) {
				OMElement homeElement = XmlUtil.firstChildWithLocalName(dr, "HomeCommunityId");
				if (homeElement == null) {
					Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA149");
					er.err(XdsErrorCode.Code.XDSMissingHomeCommunityId, assertion, this, "", "");
				}
				else  {
					String homeValue = homeElement.getText();
					if (!homeValue.startsWith("urn:oid:")) {
						Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA150");
						er.err(XdsErrorCode.Code.XDSRepositoryError, assertion, this, "", "");
					}
				}
			}
		}
	}

}
