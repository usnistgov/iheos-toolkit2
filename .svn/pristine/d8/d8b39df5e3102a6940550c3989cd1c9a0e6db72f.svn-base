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


package gov.nist.direct.validation;

import javax.mail.Address;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

public interface MessageHeadersValidator{
	
	// ************************************************
	// *********** Message headers checks *************
	// ************************************************
	
	
	
	/**
	 *  DTS 196, All Headers, Required
	 */
	public void validateAllHeaders(ErrorRecorder er, String[] header, String[] headerContent);
	
	/**
	 *  DTS 103-105, Return Path, Conditional
	 * @param er
	 * @param returnPath
	 */
	public void validateReturnPath(ErrorRecorder er, String returnPath);
		
	/**
	 *  DTS 104-106, Received, Conditional
	 */
	public void validateReceived(ErrorRecorder er, String received);
	
	/**
	 *  DTS 197, Resent Fields, Required
	 */
	public void validateResentFields(ErrorRecorder er, String[] resentField);
	
	/**
	 *  DTS 107, Resent-Date, Conditional
	 */
	public void validateResentDate(ErrorRecorder er, String resentDate);
	
	/**
	 *  DTS 108, Resent-From, Conditional
	 */
	public void validateResentFrom(ErrorRecorder er, String resentFrom);
	
	/**
	 *  DTS 109, Resent-Sender, Conditional
	 */
	public void validateResentSender(ErrorRecorder er, String resentSender, String resentFrom);
	
	/**
	 *  DTS 110, Resent-to, Optional
	 * @param resentTo
	 */
	public void validateResentTo(ErrorRecorder er, String resentTo);
	
	/**
	 *  DTS 111, Resent-cc, Optional
	 * @param resentCc
	 */
	public void validateResentCc(ErrorRecorder er, String resentCc);
	
	/**
	 *  DTS 112, Resent-bcc, Optional
	 * @param resentBcc
	 */
	public void validateResentBcc(ErrorRecorder er, String resentBcc);
		
	/**
	 *  DTS 113, Resent-Msg-Id, Conditional
	 */
	public void validateResentMsgId(ErrorRecorder er, String resentMsgId);
	
	/**
	 *  DTS 114, Orig-Date, Required
	 */
	public void validateOrigDate(ErrorRecorder er, String origDate);
		
	/**
	 *  DTS 115, From, Required
	 */
	public void validateFrom(ErrorRecorder er, String from);
	
	/**
	 *  DTS 116, Sender, Conditional
	 * @param er
	 * @param sender
	 */
	public void validateSender(ErrorRecorder er, String sender, Address[] from);
		
	/**
	 *  DTS 117, Reply-To, Optional
	 * @param replyTo
	 */
	public void validateReplyTo(ErrorRecorder er, String replyTo);
		
	/**
	 *  DTS 118, To, Required
	 */
	public void validateTo(ErrorRecorder er, String to);
	
	/**
	 *  DTS 119, cc, Optional
	 * @param cc
	 */
	public void validateCc(ErrorRecorder er, String cc);
	
	/**
	 *  DTS 120, Bcc, Optional
	 * @param bcc
	 */
	public void validateBcc(ErrorRecorder er, String bcc);
		
	/**
	 *  DTS 121, Message-Id, Required
	 */
	public void validateMessageId(ErrorRecorder er, String messageId);
	
	/**
	 *  DTS 122, In-reply-to, Optional
	 * @param inReplyTo
	 */
	public void validateInReplyTo(ErrorRecorder er, String inReplyTo, String date);
	
	/**
	 *  DTS 123, References, Optional
	 * @param references
	 */
	public void validateReferences(ErrorRecorder er, String references);
	
	/**
	 *  DTS 124, Subject, Optional
	 * @param subject
	 * @param filename
	 */
	public void validateSubject(ErrorRecorder er, String subject, String filename);
	
	/**
	 *  DTS 125, Comments, Optional
	 * @param comments
	 */
	public void validateComments(ErrorRecorder er, String comments);
	
	/**
	 *  DTS 126, Keywords, Optional
	 * @param keyword
	 */
	public void validateKeywords(ErrorRecorder er, String keyword);
	
	/**
	 *  DTS 127, Optional-field, Optional
	 * @param optionalField
	 */
	public void validateOptionalField(ErrorRecorder er, String optionalField);
	
	/**
	 *  DTS 128, Disposition-Notification-To, Optional
	 * @param dispositionNotificationTo
	 */
	public void validateDispositionNotificationTo(ErrorRecorder er, String dispositionNotificationTo);	
		
	/**
	 *  DTS 102b, MIME-Version, Required
	 */
	public void validateMIMEVersion(ErrorRecorder er, String MIMEVersion);
}
