package gov.nist.toolkit.registrymsg.registry;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import org.apache.axiom.om.OMElement;

public class RegistryError {
	public String codeContext = null;
	public String errorCode = null;
	public String severity = null;
	public String location = null;
	public boolean isWarning;

	@Override
	public String toString() {
		return String.format("%s %s: %s at %s", severity, errorCode, codeContext, location);
	}

	public RegistryError() {

	}

	public RegistryError(OMElement xml) {
		if (xml  == null)
			return;

		codeContext = xml.getAttributeValue(MetadataSupport.code_context_qname);
		errorCode = xml.getAttributeValue(MetadataSupport.error_code_qname);
		severity = xml.getAttributeValue(MetadataSupport.severity_qname);
		location = xml.getAttributeValue(MetadataSupport.location_qname);

		if (severity == null)
			severity = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error";
	}

	public void validate(ErrorRecorder er, ValidationContext vc) {
		if (codeContext == null)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryError: codeContext attribute is required", this, "ebRS 3.0 Section 2.1.6");
		if (errorCode == null)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryError: errorCode attribute is required", this, "ebRS 3.0 Section 2.1.6");
		if (!severity.startsWith("urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:"))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryError: severity attribute value must have prefix urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:", this, "ebRS 3.0 Section 2.1.6");

		if (vc.isXC && (vc.isRet || vc.isSQ) && vc.isResponse) {
			if (location == null)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryError: The location attribute must be set to the homeCommunityId of Responding Gateway", this, "ITI TF-2b: 3.39.4.1.3");
			else if (!location.startsWith("urn:oid:"))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryError: The location attribute contains an invalid homeCommunityId, it must have the prefix urn:oid:, value found was " + location, this, "ITI TF-2b: 3.38.4.1.2.1");
		}
	}

	public boolean isError() {
		return severity.endsWith("Error");
	}

	public String getErrorCode() { return errorCode; }
	public String getCodeContext() { return codeContext; }
	public boolean isWarning() { return isWarning; }
	public String getLocation() { return location; }

}
