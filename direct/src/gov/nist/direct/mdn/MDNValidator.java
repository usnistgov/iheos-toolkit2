/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */

package gov.nist.direct.mdn;

import gov.nist.toolkit.errorrecording.ErrorRecorder;



public interface MDNValidator {
	
	
	
	
	/**
	 *  DTS 450, MDN must be signed and encrypted, Required
	 * @param er
	 * @param dts450
	 */
	public void validateMDNSignatureAndEncryption(ErrorRecorder er, String dts450);

	
	
	
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	/**
	 *  DTS 451, Message Headers - Contains DTS 452, 453, 454
	 */
	public void validateMessageHeaders(ErrorRecorder er, String sthg);
	
	
	/**
	 *  DTS 452, mdn-request-header, Required
	 */
	public void validateMDNRequestHeader(ErrorRecorder er, String sthg);
		
	
	/**
	 *  DTS 453, Disposition-Notification-Options, warning
	 */
	public void validateDispositionNotificationOptions(ErrorRecorder er, String sthg);
	

	/**
	 *  DTS 454, Original-Recipient-Header, warning
	 */
	public void validateOriginalRecipientHeader(ErrorRecorder er, String sthg);
	
	
	
	
	// ************************************************
	// *************** Report content *****************
	// ************************************************

	/**
	 *  DTS 455, Report content, warning - Contains DTS 456 to 466
	 */
	public void validateReportContent(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 456, Disposition-Notification-Content, warning
	 */
	public void validateDispositionNotificationContent(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 457, Reporting-UA-Field, warning
	 */
	public void validateReportingUAField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 458, mdn-gateway-field, Required
	 */
	public void validateMDNGatewayField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 459, original-recipient-field, Required
	 */
	public void validateOriginalRecipientField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 460, final-recipient-field, Required
	 */
	public void validateFinalRecipientField(ErrorRecorder er, String sthg);

	/**
	 *  DTS 461, original-message-id-field, Required
	 */
	public void validateOriginalMessageIdField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 462, disposition-field, Required
	 */
	public void validateDispositionField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 463, failure-field, Required
	 */
	public void validateFailureField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 464, error-field, Required
	 */
	public void validateErrorField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 465, warning-field, Required
	 */
	public void validateWarningField(ErrorRecorder er, String sthg);
	
	/**
	 *  DTS 466, extension-field, Required
	 */
	public void validateExtensionField(ErrorRecorder er, String sthg);
}
