package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;

public class HashFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public HashFormat(IErrorRecorder er, String context, String resource) {
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
