package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import org.apache.axiom.om.OMElement;

public class DocumentResponseValidator extends MessageValidator {
	OMElement xml;
	
	public DocumentResponseValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		OMElement home = MetadataSupport.firstChildWithLocalName(xml, "HomeCommunityId");
		
		if (vc.isRet && vc.isResponse && vc.isXC)
			if (home == null)
				er.err(XdsErrorCode.Code.XDSRegistryError, "HomeCommunityId is required inside the DocumentResponse element in this context", this, "???");
	}

}
