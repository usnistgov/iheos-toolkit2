package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.valregmetadata.field.ValidatorCommon;
import gov.nist.toolkit.valregmetadata.object.DocumentEntry;
import gov.nist.toolkit.valregmetadata.object.Folder;
import gov.nist.toolkit.valregmetadata.object.SubmissionSet;

public class DtmFormat extends FormatValidator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();
	private Object xdsType;


	public DtmFormat(IErrorRecorder er, Object _xdsType, String context, String resource) {
		super(er, context, resource); // TODO change to include XDSType in super type
		xdsType = _xdsType; // TODO this parameter should ultimately go away
	}

	// TODO this architecture should be replaced with a Visitor or inheritance
	public void validate(String input) {
		 String assertionID = "";
		if (xdsType instanceof DocumentEntry)
			assertionID = "TA018";
		else if (xdsType instanceof SubmissionSet)
			assertionID = "TA021";
		else if (xdsType instanceof Folder)
			assertionID = "TA022";

		Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);
		int size = input.length();
		if (!(size == 4 || size == 6 || size == 8 || size == 10 || size == 12 || size == 14)) {
			String detail = "Input '" + input + "' has an invalid number of characters.";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, detail);
		}
		if (!ValidatorCommon.isInt(input)) {
			String detail = "Input: '" + input + "'. All characters must be digits.";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, detail);
		}
	}

}
