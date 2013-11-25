package gov.nist.direct.mdn.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nist.direct.mdn.MDNUtils;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

public class MDNValidatorImpl implements MDNValidator{


	
	/**
	 *  DTS 450, MDN must be signed and encrypted, Required
	 * @param er
	 * @param dts450
	 */
	public void validateMDNSignatureAndEncryption(ErrorRecorder er, boolean signed, boolean encrypted) {
		String rfc = "-";
		if(signed && encrypted) {
			er.success("450", "Signature", "Signed and Encrypted" , "Must be signed and encrypted", rfc);
		} else if(signed && !encrypted) {
			er.error("450", "Signature", "Not Encrypted" , "Must be signed and encrypted", rfc);
		} else if(!signed && encrypted) {
			er.error("450", "Signature", "Not Signed" , "Must be signed and encrypted", rfc);
		} else {
			er.error("450", "Signature", "Not Signed and Not Encrypted" , "Must be signed and encrypted", rfc);
		}
	}

	
	
	
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	/**
	 *  DTS 451, Message Headers - Contains DTS 452, 453, 454
	 */
	public void validateMessageHeaders(ErrorRecorder er, String sthg) {
	}
	
	
	/**
	 *  DTS 452, Disposition-Notification-To, Required
	 */
	public void validateMDNRequestHeader(ErrorRecorder er, String dispositionNotificationTo) {
		String rfc = "RFC 3798: Section 2.1;http://tools.ietf.org/html/rfc3798#section-2.1";
		if(dispositionNotificationTo.equals("")) {
			er.success("452", "Disposition-Notification-To", "Disposition-Notification-To is not present", "Must NOT be present", rfc);
		} else {
			er.error("452", "Disposition-Notification-To", "Disposition-Notification-To is present", "Must NOT be present", rfc);
		}
	}
		
	
	/**
	 *  DTS 453, Disposition-Notification-Options, warning
	 */
	public void validateDispositionNotificationOptions(ErrorRecorder er, String sthg) {
	}
	

	/**
	 *  DTS 454, Original-Recipient-Header, warning
	 */
	public void validateOriginalRecipientHeader(ErrorRecorder er, String originalRecipient) {
		String rfc = "RFC 3798: Section 2.3;http://tools.ietf.org/html/rfc3798#section-2.3";
		if(originalRecipient.equals("")) {
			er.info("454", "Original-Recipient", "Not present", "Might not be present", rfc);
		} else {
			if(!originalRecipient.contains("rfc822;")) {
				er.warning("454", "Original-Recipient", originalRecipient, "Should normaly contain \"rfc822\"", rfc);
			}
			String[] splitHeader = null;
			splitHeader = originalRecipient.split(";");
			String email = splitHeader[1];
			if(ValidationUtils.validateEmail(email)) {
				er.success("454", "Original-Recipient", originalRecipient, "Should be email address", rfc);
			} else {
				er.error("454", "Original-Recipient", originalRecipient, "Should be email address", rfc);
			}
		}
	}
	
	
	
	
	// ************************************************
	// *************** Report content *****************
	// ************************************************

	/**
	 *  DTS 455, Report content, warning - Contains DTS 456 to 466
	 */
	public void validateReportContent(ErrorRecorder er, String sthg) {
	}
	
	/**
	 *  DTS 456, Disposition-Notification-Content, warning
	 */
	public void validateDispositionNotificationContent(ErrorRecorder er, String reportingUA, String mdnGateway, String originalRecipient, 
			String finalRecipient, String originalMessageID, String disposition, 
			String failure, String error, String warning, String extension) {
		
		String rfc = "RFC 3798: Section 3.1;http://tools.ietf.org/html/rfc3798#section-3.1";
		ErrorRecorder separate = new GwtErrorRecorder();
		validateReportingUAField(separate, reportingUA);
		validateMDNGatewayField(separate, mdnGateway);
		validateOriginalRecipientField(separate, originalRecipient);
		validateFinalRecipientField(separate, finalRecipient);
		validateOriginalMessageIdField(separate, originalMessageID);
		validateDispositionField(separate, disposition);
		validateFailureField(separate, failure);
		validateErrorField(separate, error);
		validateWarningField(separate, warning);
		validateExtensionField(separate, extension);
		
		if(separate.hasErrors()) {
			er.error("456", "Disposition-Notification-Content", "Disposition-Notification-Content is not valid", "", rfc);
		} else {
			er.success("456", "Disposition-Notification-Content", "Disposition-Notification-Content is valid", "", rfc);
		}
	}
	
	/**
	 *  DTS 457, Reporting-UA-Field, warning
	 */
	public void validateReportingUAField(ErrorRecorder er, String reportingUA) {
		String rfc = "RFC 3798: Section 3.2.1;http://tools.ietf.org/html/rfc3798#section-3.2.1";
		if(reportingUA.equals("")) {
			er.warning("457", "Reporting-UA Field", "Not present", "Should be present", rfc);
		} else {
			final String uaName = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaProduct = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaReportingPattern =  uaName + "(;" + uaProduct + ")?";
			Pattern pattern = Pattern.compile(uaReportingPattern, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(reportingUA);
			if(matcher.matches()) {
				er.success("457", "Reporting-UA Field", reportingUA, "ua-name [ \";\" ua-product ]", rfc);
			} else {
				er.error("457", "Reporting-UA Field", reportingUA, "ua-name [ \";\" ua-product ]", rfc);
			}
		}
	}
	
	/**
	 *  DTS 458, mdn-gateway-field, Required
	 */
	public void validateMDNGatewayField(ErrorRecorder er, String mdnGateway) {
		String rfc = "RFC 3798: Section 3.2.2;http://tools.ietf.org/html/rfc3798#section-3.2.2";
		if(mdnGateway.equals("")) {
			er.info("458", "MDN-Gateway", "Not present", "Might not be present", rfc);
		} else {
			if(MDNUtils.validateAtomTextField(mdnGateway)) {
				er.success("458", "MDN-Gateway", mdnGateway, "mta-name-type \";\" mta-name", rfc);
			} else {
				er.error("458", "MDN-Gateway", mdnGateway, "mta-name-type \";\" mta-name", rfc);
			}
		}
	}
	
	/**
	 *  DTS 459, original-recipient-field, Required
	 */
	public void validateOriginalRecipientField(ErrorRecorder er, String originalRecipient) {
		String rfc = "RFC 3798: Section 3.2.3;http://tools.ietf.org/html/rfc3798#section-3.2.3";
		if(originalRecipient.equals("")) {
			er.info("459", "Original-Recipient", "Not present", "Might not be present", rfc);
		} else {
			if(MDNUtils.validateAtomTextField(originalRecipient)) {
				er.success("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc);
			} else {
				er.error("459", "Original-Recipient", originalRecipient, "address-type \";\" generic-address", rfc);
			}
		}
	}
	
	/**
	 *  DTS 460, final-recipient-field, Required
	 */
	public void validateFinalRecipientField(ErrorRecorder er, String finalRecipient) {
		String rfc = "RFC 3798: Section 3.2.4;http://tools.ietf.org/html/rfc3798#section-3.2.4";
		if(finalRecipient.equals("")) {
			er.warning("460", "Final-Recipient", "Not present", "Should be present", rfc);
		} else {
			String[] buf;
			boolean result = true;
			if(finalRecipient.contains(";")) {
				buf = finalRecipient.split(";");
				final String stringPattern =  MDNUtils.getAtom();
				Pattern pattern = Pattern.compile(stringPattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(buf[0]);
				if(!matcher.matches()) {
					result = false;
					er.error("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc);
				}
				if(!ValidationUtils.validateEmail(buf[1])) {
					result = false;
					er.error("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc);
				}
			} else {
				result = false;
			}
			
			if(result) {
				er.success("460", "Final-Recipient", finalRecipient, "address-type \";\" generic-address", rfc);
			}
		}
	}

	/**
	 *  DTS 461, original-message-id-field, Required
	 */
	public void validateOriginalMessageIdField(ErrorRecorder er, String originalMessageId) {
		String rfc = "RFC 3798: Section 3.2.5;http://tools.ietf.org/html/rfc3798#section-3.2.5";
		if(originalMessageId.equals("")) {
			er.warning("461", "Original-Message-ID", "Not present", "Should be present", rfc);
		} else {
			if(ValidationUtils.validateAddrSpec(originalMessageId)) {
				er.success("461", "Original-Message-ID", originalMessageId, "\"<\" id-left \"@\" id-right \">\"", rfc);
			} else {
				er.error("461", "Original-Message-ID", originalMessageId, "\"<\" id-left \"@\" id-right \">\"", rfc);
			}
		}
	}
	
	/**
	 *  DTS 462, disposition-field, Required
	 */
	public void validateDispositionField(ErrorRecorder er, String disposition) {
		String rfc = "RFC 3798: Section 3.2.6;http://tools.ietf.org/html/rfc3798#section-3.2.6";
		if(disposition.equals("")) {
			er.warning("462", "Disposition Field", "Not present", "Should be present", rfc);
		} else {
			if(MDNUtils.validateDisposition(disposition)) {
				er.success("462", "Disposition Field", disposition, "disposition-mode \";\" disposition-type", rfc);
			} else {
				er.error("462", "Disposition Field", disposition, "disposition-mode \";\" disposition-type", rfc);
			}
		}
	}
	
	/**
	 *  DTS 463, failure-field, Required
	 */
	public void validateFailureField(ErrorRecorder er, String failure) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(failure.equals("")) {
			er.info("463", "Failure Field", "Not present", "", rfc);
		} else {
			if(MDNUtils.validateTextField(failure)) {
				er.success("463", "Failure Field", failure, "*text", rfc);
			} else {
				er.error("463", "Failure Field", failure, "*text", rfc);
			}
		}
	}
	
	/**
	 *  DTS 464, error-field, Required
	 */
	public void validateErrorField(ErrorRecorder er, String error) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(error.equals("")) {
			er.info("464", "Error Field", "Not present", "", rfc);
		} else {
			if(MDNUtils.validateTextField(error)) {
				er.success("464", "Error Field", error, "*text", rfc);
			} else {
				er.error("464", "Error Field", error, "*text", rfc);
			}
		}
	}
	
	/**
	 *  DTS 465, warning-field, Required
	 */
	public void validateWarningField(ErrorRecorder er, String warning) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(warning.equals("")) {
			er.info("465", "Warning Field", "Not present", "", rfc);
		} else {
			if(MDNUtils.validateTextField(warning)) {
				er.success("465", "Warning Field", warning, "*text", rfc);
			} else {
				er.error("465", "Warning Field", warning, "*text", rfc);
			}
		}
	}
	
	/**
	 *  DTS 466, extension-field, Required
	 */
	public void validateExtensionField(ErrorRecorder er, String extension) {
		String rfc = "RFC 3798: Section 3.2.7;http://tools.ietf.org/html/rfc3798#section-3.2.7";
		if(extension.equals("")) {
			er.info("466", "Extension Field", "Not present", "", rfc);
		} else {
			if(MDNUtils.validateTextField(extension)) {
				er.success("466", "Extension Field", extension, "*text", rfc);
			} else {
				er.error("466", "Extension Field", extension, "*text", rfc);
			}
		}
	}


}
