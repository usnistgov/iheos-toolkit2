package gov.nist.direct.messageProcessor.utils;

import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class MessageDispatchUtils {
	

	
	
	public static boolean isDIRECT(ErrorRecorder er, MimeMessage msg) throws MessagingException{
		if(!msg.isMimeType("application/pkcs7-mime")) {
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
	public static boolean isMDN (ErrorRecorder er, MimeMessage msg) throws MessagingException{
		if (msg.getContentType().contains("multipart/report") ||
				msg.getContentType().contains("message//disposition-notification")) {
			return true;
		}
				return false;	
	}
		
	
	

}
