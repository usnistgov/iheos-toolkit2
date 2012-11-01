package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.util.List;

import org.apache.axiom.om.OMElement;

/**
 * Validate a RetreiveDocumentSetRequest message.
 * @author bill
 *
 */
public class RetrieveRequestValidator  extends MessageValidator {
	OMElement xml;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;

	public RetrieveRequestValidator(ValidationContext vc, OMElement xml, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc) {
		super(vc);
		this.xml = xml;
		this.erBuilder = erBuilder;
		this.mvc = mvc;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		if (xml == null) {
			er.err(XdsErrorCode.Code.XDSRepositoryError, "RetrieveDocumentSetRequest: top element null", this, "");
			return;
		}
		

		List<OMElement> documentRequests = MetadataSupport.childrenWithLocalName(xml, "DocumentRequest");
		for (OMElement dr : documentRequests) {
			mvc.addMessageValidator("DocumentRequest element ordering", new RetrieveOrderValidator(vc, dr), erBuilder.buildNewErrorRecorder());
			if (vc.isXC) {
				OMElement homeElement = MetadataSupport.firstChildWithLocalName(dr, "HomeCommunityId");
				if (homeElement == null) {
					er.err(XdsErrorCode.Code.XDSMissingHomeCommunityId, "Cross-Community Retrieve request must include homeCommunityId", this, "ITI TF-2b: 3.39.1");
				}
				else  {
					String homeValue = homeElement.getText();
					if (!homeValue.startsWith("urn:oid:"))
						er.err(XdsErrorCode.Code.XDSRepositoryError, "HomeCommunityId must have urn:oid: prefix", this, "ITI TF-2b: 3.38.4.1.2.1");
				} 
			}
		}

	}

}
