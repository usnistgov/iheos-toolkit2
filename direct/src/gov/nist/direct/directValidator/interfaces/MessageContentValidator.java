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
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.directValidator.interfaces;

import gov.nist.toolkit.errorrecording.ErrorRecorder;



public interface MessageContentValidator {
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	// xxxxxxxxxxxxxxx SMTP Commands  xxxxxxxxxxxxxxx
	
	/**
	 *  DTS 198, ?, Required
	 * @param er
	 * @param dts198
	 */
	public void validateDTS198(ErrorRecorder er, String dts198);
	
	/**
	 *  DTS 100, MAIL FROM SMTP, Required
	 * @param er
	 * @param mailFromSmtp
	 */
	public void validateMailFromSMTP(ErrorRecorder er, String mailFromSmtp);
		
	/**
	 *  DTS 101, RCPT TO, Required
	 * @param er
	 * @param RcptTo
	 */
	public void validateRcptTo(ErrorRecorder er, String RcptTo);
	
	
	// xxxxxxxxxxxxxxx Outer Enveloped Message  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
		
	/**
	 *  DTS 199, Non-MIME Message Headers, Required
	 */
	public void validateNonMIMEMessageHeaders(ErrorRecorder er, String nonMIMEHeader);
		
	/**
	 *  DTS 200, MIME Entity, Required
	 */
	public void validateMIMEEntity(ErrorRecorder er, String MIMEEntity);
		
	/**
	 *  DTS 133a, Content-Type, Required
	 */
	public void validateMessageContentTypeA(ErrorRecorder er, String messageContentTypeA);
		
	/**
	 *  DTS 201, Content-Type Name, Optional
	 */
	public void validateContentTypeNameOptional(ErrorRecorder er, String contentTypeName);
	
	/**
	 *  DTS 202, Content-Type S/MIME-Type, Optional
	 * @param contentTypeSMIME
	 */
	public void validateContentTypeSMIMETypeOptional(ErrorRecorder er, String contentTypeSMIME);
	
	/**
	 *  DTS 203, Content Disposition, Optional
	 * @param contentDisposition
	 */
	public void validateContentDispositionOptional(ErrorRecorder er, String contentDisposition);
	
	/**
	 *  DTS 129, Message Body, Required
	 */
	public void validateMessageBody(ErrorRecorder er, boolean decrypted);
	
	
	// xxxxxxxxxxxxxxx Inner Decrypted Message  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	
	
	/**
	 *  DTS 204, MIME Entity, Required
	 * @param er
	 * @param mimeEntity
	 */
	public void validateMIMEEntity2(ErrorRecorder er, boolean mimeEntity);
		
	/**
	 *  DTS 133b, Content-Type, Required
	 */
	public void validateMessageContentTypeB(ErrorRecorder er, String messageContentTypeB);
	
	/**
	 *  DTS 160, Content-Type micalg, Required
	 */
	public void validateContentTypeMicalg(ErrorRecorder er, String contentTypeMicalg);
	
	/**
	 *  DTS 205, Content-Type protocol, Required
	 */
	public void validateContentTypeProtocol(ErrorRecorder er, String contentTypeProtocol);
	
	/**
	 *  DTS 206, Content-Transfer-Encoding, Required
	 */
	public void validateContentTransferEncoding(ErrorRecorder er, String contentTransfertEncoding);
	
	/**
	 *  DTS ?, MIME Entity Body, Required
	 */
	public void validateMIMEEntityBody(ErrorRecorder er, int nbBody);
	
	
	// xxxxxxxxxxxxxxx Health Content Container  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	

	/**
	 *  DTS 139, First MIME Part, Required
	 * @param er
	 * @param firstMIMEPart
	 */
	public void validateFirstMIMEPart(ErrorRecorder er, boolean firstMIMEPart);
		
	/**
	 *  DTS 151, First MIME Part Body, Required
	 * @param er
	 * @param firstMIMEPartBody
	 */
	public void validateFirstMIMEPartBody(ErrorRecorder er, boolean firstMIMEPartBody);
		
	
	

}
