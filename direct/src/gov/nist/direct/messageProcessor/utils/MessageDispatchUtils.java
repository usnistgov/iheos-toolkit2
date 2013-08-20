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


package gov.nist.direct.messageProcessor.utils;

import gov.nist.direct.messageProcessor.direct.directImpl.WrappedMessageProcessor;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MessageDispatchUtils {
	
	WrappedMessageProcessor processor = new WrappedMessageProcessor();
	
	public MessageDispatchUtils(ErrorRecorder er, byte[] msg, byte[] directCertificate, String password) {
		processor.messageParser(er, msg, directCertificate, password);
	}
	

	
	
	public boolean isDIRECT() throws MessagingException{
		return this.processor.getIsDirect();
	}
	
	public static boolean isEncrypted(ErrorRecorder er, MimeMessage msg) throws MessagingException{
		//if(!msg.isMimeType("application/pkcs7-mime")) {
		if (msg.getContentType().contains("application/pkcs7-mime") || msg.getContentType().contains("application/x-pkcs7-mime")){
			return true;
		}
		return false;
	}

	/**
	 * Checks if a received message is or not an MDN report. If false, then it is either a Direct message,
	 * either a bad message.
	 * @param msg the received message
	 * @return true if the message is an MDN (Message Disposition Notification report),
	 * false if it is not.
	 * @throws MessagingException 
	 */
	public boolean isMDN() throws MessagingException{
		return this.processor.getIsMDN();
	}
		
	public boolean isSigned() {
		return this.processor.getIsSigned();
	}
	
	public boolean isEncrypted() {
		return this.processor.getIsEncrypted();
	}

}
