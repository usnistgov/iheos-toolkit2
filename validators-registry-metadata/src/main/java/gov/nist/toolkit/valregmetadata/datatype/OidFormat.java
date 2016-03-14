package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class OidFormat extends FormatValidator {

	public OidFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {
		if (!ValidatorCommon.is_oid(input, true))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, context + ": " + input + " is not in OID format", this, getResource(null));
	}

}
