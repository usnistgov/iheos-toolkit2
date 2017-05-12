package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class CxFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public CxFormat(IErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {

		String output = ValidatorCommon.validate_CX_datatype(input);
		if (output != null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA001");
			String location = context + ": " + input;
			String detail = output;
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
		}
	}

}
