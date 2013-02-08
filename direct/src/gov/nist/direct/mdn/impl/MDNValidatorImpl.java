package gov.nist.direct.mdn.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nist.direct.mdn.MDNUtils;
import gov.nist.direct.mdn.MDNValidator;
import gov.nist.direct.utils.ValidationUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

public class MDNValidatorImpl implements MDNValidator{


	
	/**
	 *  DTS 450, MDN must be signed and encrypted, Required
	 * @param er
	 * @param dts450
	 */
	public void validateMDNSignatureAndEncryption(ErrorRecorder er, String dts450) {
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
		if(originalRecipient.contains("rfc822;")) {
			String[] splitHeader = null;
			splitHeader = originalRecipient.split(";");
			String email = splitHeader[1];
			if(ValidationUtils.validateEmail(email)) {
				er.detail("Success:  DTS 454 - Original-Recipient header is valid");
			} else {
				er.err("454", "Original-Recipient header is not valid", "", "", "DTS 454");
			}
		} else {
			er.err("454", "Original-Recipient header is not valid", "", "", "DTS 454");
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
		
		
	}
	
	/**
	 *  DTS 457, Reporting-UA-Field, warning
	 */
	public void validateReportingUAField(ErrorRecorder er, String reportingUA) {
		final String uaName = "[0-9a-zA-Z_.-]*";
		final String uaProduct = "[0-9a-zA-Z_.-]*";
		final String uaReportingPattern =  uaName + "(;" + uaProduct + ")?";
		Pattern pattern = Pattern.compile(uaReportingPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(reportingUA);
		if(matcher.matches()) {
			er.detail("Success:  DTS 457 - Reporting-UA field is valid");
		} else {
			er.err("457", "Reporting-UA field is not valid", "", "", "DTS 457");
		}
	}
	
	/**
	 *  DTS 458, mdn-gateway-field, Required
	 */
	public void validateMDNGatewayField(ErrorRecorder er, String mdnGateway) {
		if(MDNUtils.validateAtomTextField(mdnGateway)) {
			er.detail("Success:  DTS 458 - MDN-Gateway field is valid");
		} else {
			er.err("458", "MDN-Gateway is not valid", "", "", "DTS 458");
		}
	}
	
	/**
	 *  DTS 459, original-recipient-field, Required
	 */
	public void validateOriginalRecipientField(ErrorRecorder er, String originalRecipient) {
		if(MDNUtils.validateAtomTextField(originalRecipient)) {
			er.detail("Success:  DTS 459 - Original-Recipient field is valid");
		} else {
			er.err("459", "Original-Recipient is not valid", "", "", "DTS 459");
		}
	}
	
	/**
	 *  DTS 460, final-recipient-field, Required
	 */
	public void validateFinalRecipientField(ErrorRecorder er, String finalRecipient) {
		if(MDNUtils.validateAtomTextField(finalRecipient)) {
			er.detail("Success:  DTS 460 - Final-Recipient field is valid");
		} else {
			er.err("460", "Final-Recipient is not valid", "", "", "DTS 460");
		}
	}

	/**
	 *  DTS 461, original-message-id-field, Required
	 */
	public void validateOriginalMessageIdField(ErrorRecorder er, String originalMessageId) {
		if(ValidationUtils.validateAddrSpec(originalMessageId)) {
			er.detail("Success:  DTS 461 - Original-Message-Id field is valid");
		} else {
			er.err("461", "Original-Message-Id is not valid", "", "", "DTS 461");
		}
	}
	
	/**
	 *  DTS 462, disposition-field, Required
	 */
	public void validateDispositionField(ErrorRecorder er, String disposition) {
		if(MDNUtils.validateDisposition(disposition)) {
			er.detail("Success:  DTS 462 - Disposition field is valid");
		} else {
			er.err("462", "Disposition field is not valid", "", "", "DTS 462");
		}
	}
	
	/**
	 *  DTS 463, failure-field, Required
	 */
	public void validateFailureField(ErrorRecorder er, String sthg) {
	}
	
	/**
	 *  DTS 464, error-field, Required
	 */
	public void validateErrorField(ErrorRecorder er, String sthg) {
	}
	
	/**
	 *  DTS 465, warning-field, Required
	 */
	public void validateWarningField(ErrorRecorder er, String sthg) {
	}
	
	/**
	 *  DTS 466, extension-field, Required
	 */
	public void validateExtensionField(ErrorRecorder er, String sthg) {
	}


}
