package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;
import org.junit.Assert;

public class CxFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public CxFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {

		String output = ValidatorCommon.validate_CX_datatype(input);
		String assertionID = "TA001";
		if (output != null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
			String location = context + ": " + input;
			String detail = output;
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, detail);
		}
	}

}
