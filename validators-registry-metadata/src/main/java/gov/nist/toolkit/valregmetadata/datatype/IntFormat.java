package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class IntFormat extends FormatValidator {

	public IntFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	//TODO add Assertions model after figuring out value of Resource
	public void validate(String input) {
		if (!ValidatorCommon.isInt(input)) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, context + ": " + input + " is not an integer", this, getResource(null));
		}
		
	}

}
