package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

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

	public Rfc3066Format(ErrorRecorder er, String context, String resource) {
		super(er, context, resource);
	}
	
	String errMsg = " - does not conform to RFC 3066 format";
	String xresource = "RFC 3066";
	
	static String digits = "1234567890";
	static String alphas = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public void validate(String input) {
		if (input == null || input.equals("")) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, errMsg, this, getResource(xresource + " - input is empty"));
			return;
		}
		String[] parts = input.split("-");
		if (parts[0].length() > 8) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, input + errMsg, this, getResource(xresource + " - Primary-subtag limited to 8 characters"));
		}
		if (!allAlphas(parts[0])) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, input + errMsg, this, getResource(xresource + " - Primary-subtag is not all alpha characters"));
		}
		if (parts.length == 1)
			return;
		
		if (parts[1].length() > 8) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, input + errMsg, this, getResource(xresource + " - Subtag limited to 8 characters"));
		}
		if (!allAlphasAndDigits(parts[1])) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, input + errMsg, this, getResource(xresource + " - Subtag is not all alpha or digit characters"));
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
