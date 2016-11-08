package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;

public class HashFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public HashFormat(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}

	public void validate(String input) {
		if (!UuidFormat.isHexString(input)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA085");
			String location = context + ": " + input;
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, location, "");
		}
	}

}
