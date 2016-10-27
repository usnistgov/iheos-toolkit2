package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;

public class DtmFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public DtmFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {
		Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA018");

		int size = input.length();
		if (!(size == 4 || size == 6 || size == 8 || size == 10 || size == 12 || size == 14)) {
			String detail = "Input " + input + " has an invalid number of characters.";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, detail);
		}
		if (!ValidatorCommon.isInt(input)) {
			String detail = "Input: " + input + ". All characters must be digits.";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, detail);
		}
	}

}
