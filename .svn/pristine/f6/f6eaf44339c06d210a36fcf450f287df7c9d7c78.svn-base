package gov.nist.toolkit.common.datatypes;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

public class UuidValidator {
	ErrorRecorder er;
	String rawMsgPrefix = "Validating UUID format of "; 
	String msgPrefix;
	
	public UuidValidator(ErrorRecorder er, String errorMsgPrefix) {
		this.er = er;
		if (errorMsgPrefix != null) 
			rawMsgPrefix = errorMsgPrefix;
	}
	
	
	boolean allHexDigits(String hex, String errorPrefix) {
		String d = "0123456789abcdef";
		
		for (int i=0; i<hex.length(); i++) {
			char digit = hex.charAt(i);
			if (d.indexOf(digit) > -1)
				continue;
			if (d.indexOf(String.valueOf(digit).toLowerCase()) > -1) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, errorPrefix + " - hex digits must be lower case, found - " + digit, this, null);
				return false;
			} else {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, errorPrefix + " - non-hex digit found - " + digit, this, null);
				return false;
			}
		}
		return true;
	}
	
	// validate UUID format
	public void validateUUID(String uuid) {
		msgPrefix = rawMsgPrefix + uuid;
		
		if (!uuid.startsWith("urn:uuid:")) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - does not have urn:uuid: prefix", this, null);
			return;
		}
		
		String content = uuid.substring(9);
		String[] parts = content.split("-");
		
		if (parts.length != 5) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - does not have 5 hex-digit groups separated by the - character", this, null);
			return;
		}

		String part;
		
		part = parts[0];
		if (part.length() != 8)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - first hex character group must have 8 digits", this, null);
		allHexDigits(part, msgPrefix);
		
		part = parts[1];
		if (part.length() != 4)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - second hex character group must have 4 digits", this, null);
		allHexDigits(part, msgPrefix);
		
		part = parts[2];
		if (part.length() != 4)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - third hex character group must have 4 digits", this, null);
		allHexDigits(part, msgPrefix);
		
		part = parts[3];
		if (part.length() != 4)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - fourth hex character group must have 4 digits", this, null);
		allHexDigits(part, msgPrefix);
		
		part = parts[4];
		if (part.length() != 12)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msgPrefix + " - fifth hex character group must have 12 digits", this, null);
		allHexDigits(part, msgPrefix);
		
	}
	

}
