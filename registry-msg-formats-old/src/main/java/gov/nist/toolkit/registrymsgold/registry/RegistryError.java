package gov.nist.toolkit.registrymsgold.registry;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import org.apache.axiom.om.OMElement;

public class RegistryError {
	public String codeContext = null;
	public String errorCode = null;
	public String severity = null;
	public String location = null;
	public boolean isWarning;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


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

	public void validate(IErrorRecorder er, ValidationContext vc) {
		if (codeContext == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA092");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, "", "");
		}
		if (errorCode == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA093");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, "", "");
		}
		if (!severity.startsWith("urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:")) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA094");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, "", "");
		}
		if (vc.isXC && (vc.isRet || vc.isSQ) && vc.isResponse) {
			if (location == null) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA095");
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, "", "");
			}
			else if (!location.startsWith("urn:oid:")) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA096");
				String detail = "Value found: '" + location + "'";
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, "", detail);
			}
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
