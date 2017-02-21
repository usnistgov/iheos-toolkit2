package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.http.MultipartParserBa;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

import java.util.List;

public class DocumentElementValidator extends AbstractMessageValidator {
	MessageValidatorEngine mvc;
	
	public DocumentElementValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc) {
		super(vc);
		this.mvc = mvc;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		
		AbstractMessageValidator mcmv = mvc.findMessageValidator("MultipartContainer");
		if (mcmv == null) {
			er.detail("DocumentElementValidator: Document contents not available");
            er.unRegisterValidator(this);
			return;
		}
		MultipartContainer mpc = (MultipartContainer) mcmv;
		
		AbstractMessageValidator mmv = mvc.findMessageValidator("MetadataContainer");
		if (mmv == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "DocumentElementValidator: cannot retrieve MetadataContainer class from validator stack", this, "Data not available");
            er.unRegisterValidator(this);
			return;
		}
		MetadataContainer mc = (MetadataContainer) mmv;
		
		MultipartParserBa mp = mpc.mp;
		Metadata m = mc.m;
		
		List<String> eoIds = m.getExtrinsicObjectIds();

        er.unRegisterValidator(this);
	}

}
