package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorList;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

import java.util.Arrays;
import java.util.List;

/**
 * Validate a RegistryResponse message.
 * @author bill
 *
 */
public class RegistryResponseValidator extends AbstractMessageValidator {
	OMElement xml;
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	static List<String> statusValues =
			Arrays.asList(
					MetadataSupport.response_status_type_namespace + "Success",
					MetadataSupport.ihe_response_status_type_namespace + "PartialSuccess",
					MetadataSupport.response_status_type_namespace + "Failure"
			);

	public RegistryResponseValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);

		if (xml == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA139");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
			er.unRegisterValidator(this);
			return;
		}

		String longStatus = xml.getAttributeValue(MetadataSupport.status_qname);
		if (longStatus == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA140");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
			er.unRegisterValidator(this);
			return;
		}

		if (!statusValues.contains(longStatus)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA141");
			String detail = "Status attribute must be one of these values: '" + statusValues + "'; found instead: '" + longStatus + "'";
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", detail);
		}
		boolean isPartialSuccess = longStatus.endsWith(":PartialSuccess");
		boolean isSuccess = longStatus.endsWith(":Success");

		RegistryErrorList rel = new RegistryErrorList(XmlUtil.firstChildWithLocalName(xml, "RegistryErrorList"));
		rel.validate(er, vc);

		boolean hasErrors = rel.hasError();

		if (isPartialSuccess && !isPartialSuccessPermitted()) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA142");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
		}

		if (hasErrors && isSuccess) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA143");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
		}
		if (!isSuccess && !hasErrors) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA144");
			String detail = "Status found: '" + longStatus + "'";
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", detail);
		}
		er.unRegisterValidator(this);
	}

	boolean isPartialSuccessPermitted() {
		return (vc.isSQ && vc.isResponse) ||
				(vc.isRet && vc.isResponse);
	}

}
