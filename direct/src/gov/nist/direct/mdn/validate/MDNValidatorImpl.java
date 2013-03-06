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
		if(signed && encrypted) {
			er.detail("Success:  DTS 450 - MDN is signed and encrypted");
		} else if(signed && !encrypted) {
			er.err("450", "MDN is not encrypted", "", "", "DTS 450");
		} else if(!signed && encrypted) {
			er.err("450", "MDN is not signed", "", "", "DTS 450");
		} else {
			er.err("450", "MDN is not encrypted and not signed", "", "", "DTS 450");
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
		if(dispositionNotificationTo.equals("")) {
			er.detail("Success:  DTS 452 - Disposition-Notification-To is valid");
		} else {
			er.err("452", "Disposition-Notification-To is invalid must not be present", "", "", "DTS 452");
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
		if(originalRecipient.equals("")) {
			er.warning("454", "Original-Recipient Field is not present", "", "DTS 454");
		} else {
			if(!originalRecipient.contains("rfc822;")) {
				er.warning("454", "Original-Recipient header should normaly contain \"rfc822\"", "", "DTS 454");
			}
			String[] splitHeader = null;
			splitHeader = originalRecipient.split(";");
			String email = splitHeader[1];
			if(ValidationUtils.validateEmail(email)) {
				er.detail("Success:  DTS 454 - Original-Recipient header is valid");
			} else {
				er.err("454", "Original-Recipient header is not valid", "", "", "DTS 454");
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
			er.err("456", "Disposition-Notification-Content is not valid", "", "", "DTS 456");
		} else {
			er.detail("Success:  DTS 456 - Disposition-Notification-Content field is valid");
		}
	}
	
	/**
	 *  DTS 457, Reporting-UA-Field, warning
	 */
	public void validateReportingUAField(ErrorRecorder er, String reportingUA) {
		if(reportingUA.equals("")) {
			er.warning("457", "Reporting-UA Field is not present", "", "DTS 457");
		} else {
			final String uaName = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaProduct = "[0-9,a-z,A-Z,_,.,\\-,\\s]*";
			final String uaReportingPattern =  uaName + "(;" + uaProduct + ")?";
			Pattern pattern = Pattern.compile(uaReportingPattern, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(reportingUA);
			if(matcher.matches()) {
				er.detail("Success:  DTS 457 - Reporting-UA field is valid");
			} else {
				er.err("457", "Reporting-UA field is not valid", "", "", "DTS 457");
			}
		}
	}
	
	/**
	 *  DTS 458, mdn-gateway-field, Required
	 */
	public void validateMDNGatewayField(ErrorRecorder er, String mdnGateway) {
		if(mdnGateway.equals("")) {
			er.warning("458", "MDN-Gateway Field is not present", "", "DTS 458");
		} else {
			if(MDNUtils.validateAtomTextField(mdnGateway)) {
				er.detail("Success:  DTS 458 - MDN-Gateway field is valid");
			} else {
				er.err("458", "MDN-Gateway is not valid", "", "", "DTS 458");
			}
		}
	}
	
	/**
	 *  DTS 459, original-recipient-field, Required
	 */
	public void validateOriginalRecipientField(ErrorRecorder er, String originalRecipient) {
		if(originalRecipient.equals("")) {
			er.warning("459", "Original-Recipient Field is not present", "", "DTS 459");
		} else {
			if(MDNUtils.validateAtomTextField(originalRecipient)) {
				er.detail("Success:  DTS 459 - Original-Recipient field is valid");
			} else {
				er.err("459", "Original-Recipient is not valid", "", "", "DTS 459");
			}
		}
	}
	
	/**
	 *  DTS 460, final-recipient-field, Required
	 */
	public void validateFinalRecipientField(ErrorRecorder er, String finalRecipient) {
		if(finalRecipient.equals("")) {
			er.warning("460", "Final-Recipient Field is not present", "", "DTS 460");
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
					er.err("460", "Final-Recipient address type is not valid", "", "", "DTS 460");
				}
				if(!ValidationUtils.validateEmail(buf[1])) {
					result = false;
					er.err("460", "Final-Recipient generic address is not valid", "", "", "DTS 460");
				}
			} else {
				result = false;
			}
			
			if(result) {
				er.detail("Success:  DTS 460 - Final-Recipient field is valid");
			}
		}
	}

	/**
	 *  DTS 461, original-message-id-field, Required
	 */
	public void validateOriginalMessageIdField(ErrorRecorder er, String originalMessageId) {
		if(originalMessageId.equals("")) {
			er.warning("461", "Origianl-Message-ID Field is not present", "", "DTS 461");
		} else {
			if(ValidationUtils.validateAddrSpec(originalMessageId)) {
				er.detail("Success:  DTS 461 - Original-Message-Id field is valid");
			} else {
				er.err("461", "Original-Message-Id is not valid", "", "", "DTS 461");
			}
		}
	}
	
	/**
	 *  DTS 462, disposition-field, Required
	 */
	public void validateDispositionField(ErrorRecorder er, String disposition) {
		if(disposition.equals("")) {
			er.warning("462", "Disposition Field is not present", "", "DTS 462");
		} else {
			if(MDNUtils.validateDisposition(disposition)) {
				er.detail("Success:  DTS 462 - Disposition field is valid");
			} else {
				er.err("462", "Disposition field is not valid", "", "", "DTS 462");
			}
		}
	}
	
	/**
	 *  DTS 463, failure-field, Required
	 */
	public void validateFailureField(ErrorRecorder er, String failure) {
		if(failure.equals("")) {
			er.warning("463", "Failure Field is not present", "", "DTS 463");
		} else {
			if(MDNUtils.validateTextField(failure)) {
				er.detail("Success:  DTS 463 - Failure field is valid");
			} else {
				er.err("463", "Failure field is not valid", "", "", "DTS 463");
			}
		}
	}
	
	/**
	 *  DTS 464, error-field, Required
	 */
	public void validateErrorField(ErrorRecorder er, String error) {
		if(error.equals("")) {
			er.warning("464", "Failure Field is not present", "", "DTS 464");
		} else {
			if(MDNUtils.validateTextField(error)) {
				er.detail("Success:  DTS 464 - Error field is valid");
			} else {
				er.err("464", "Error field is not valid", "", "", "DTS 464");
			}
		}
	}
	
	/**
	 *  DTS 465, warning-field, Required
	 */
	public void validateWarningField(ErrorRecorder er, String warning) {
		if(warning.equals("")) {
			er.warning("465", "Warning Field is not present", "", "DTS 465");
		} else {
			if(MDNUtils.validateTextField(warning)) {
				er.detail("Success:  DTS 465 - Warning field is valid");
			} else {
				er.err("465", "Warning field is not valid", "", "", "DTS 465");
			}
		}
	}
	
	/**
	 *  DTS 466, extension-field, Required
	 */
	public void validateExtensionField(ErrorRecorder er, String extension) {
		if(extension.equals("")) {
			er.warning("466", "Extension Field is not present", "", "DTS 466");
		} else {
			if(MDNUtils.validateTextField(extension)) {
				er.detail("Success:  DTS 466 - Extension field is valid");
			} else {
				er.err("466", "Extension field is not valid", "", "", "DTS 466");
			}
		}
	}


}
