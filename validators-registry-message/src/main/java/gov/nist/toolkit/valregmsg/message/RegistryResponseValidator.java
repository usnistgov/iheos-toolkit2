package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorList;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMElement;

/**
 * Validate a RegistryResponse message.
 * @author bill
 *
 */
public class RegistryResponseValidator extends MessageValidator {
	OMElement xml;

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

		if (xml == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "RegistryResponseValidator: no RegistryResponse found", this, "");
			return;
		}

		String longStatus = xml.getAttributeValue(MetadataSupport.status_qname);
		if (longStatus == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "RegistryResponseValidator: required attribute status is missing", this, "ebRS 3.0 Schema");
			return;
		}

		if (!statusValues.contains(longStatus))
			er.err(XdsErrorCode.Code.XDSRegistryError, "RegistryResponseValidator: status attribute must be one of these values: "
					+ statusValues + " found instead " + longStatus,
					this, "ITI TF-3: 4.1.13");

		boolean isPartialSuccess = longStatus.endsWith(":PartialSuccess");
		boolean isSuccess = longStatus.endsWith(":Success");

		RegistryErrorList rel = new RegistryErrorList(XmlUtil.firstChildWithLocalName(xml, "RegistryErrorList"));
		rel.validate(er, vc);

		boolean hasErrors = rel.hasError();

		if (isPartialSuccess && !isPartialSuccessPermitted())
			er.err(XdsErrorCode.Code.XDSRegistryError, "Status is PartialSuccess but this status not allowed on this transaction", this, "ITI TF-3: 4.1.13");


		if (hasErrors && isSuccess)
			er.err(XdsErrorCode.Code.XDSRegistryError, "RegistryResponse contains errors but status is Success", this, "ebRS 3.0 Section 2.1.6.2");

		if (!isSuccess && !hasErrors)
			er.err(XdsErrorCode.Code.XDSRegistryError, "Status attribute is " + longStatus + " but no errors are present", this, "ebRS 3.0 Section 2.1.3.2");


	}

	boolean isPartialSuccessPermitted() {
		return (vc.isSQ && vc.isResponse) ||
		(vc.isRet && vc.isResponse);
	}

}
