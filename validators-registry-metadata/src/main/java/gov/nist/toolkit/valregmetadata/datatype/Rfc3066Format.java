package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;

/**
 * RFC 3066
 * @author bill
 *
 * The syntax of this tag in ABNF [RFC 2234] is:
 *
 *   Language-Tag = Primary-subtag *( "-" Subtag )
 *
 *   Primary-subtag = 1*8ALPHA
 *
 *   Subtag = 1*8(ALPHA / DIGIT)
 *
 */
public class Rfc3066Format extends FormatValidator {

	public Rfc3066Format(IErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	String errMsg = " - does not conform to RFC 3066 format";
	String xresource = "RFC 3066";

	static String digits = "1234567890";
	static String alphas = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public void validate(String input) {
		if (input == null || input.equals("")) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA086");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, "");
			return;
		}
		String[] parts = input.split("-");
		if (parts[0].length() > 8) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA087");
			String detail = "Input: '" + input + "'";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, "");
		}
		if (!allAlphas(parts[0])) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA088");
			String detail = "Input: '" + input + "'";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, "");
		}
		if (parts.length == 1)
			return;

		if (parts[1].length() > 8) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA089");
			String detail = "Input: '" + input + "'";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, "");
		}
		if (!allAlphasAndDigits(parts[1])) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA090");
			String detail = "Input: '" + input + "'";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, context, "");
		}

	}

	boolean allAlphas(String in) {
		for (int i=0; i<in.length(); i++) {
			char c = in.charAt(i);
			if (alphas.indexOf(c) == -1)
				return false;
		}
		return true;
	}
	boolean allAlphasAndDigits(String in) {
		for (int i=0; i<in.length(); i++) {
			char c = in.charAt(i);
			if (digits.indexOf(c) == -1 && alphas.indexOf(c) == -1)
				return false;
		}
		return true;
	}
}
