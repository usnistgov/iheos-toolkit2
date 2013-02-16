package gov.nist.direct.messageProcessor.utils;

import java.io.IOException;

import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.messageProcessor.direct.directImpl.WrappedMessageProcessor;
import gov.nist.direct.utils.ParseUtils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class MessageDispatchUtils {
	

	
	
	public static boolean isDIRECT(ErrorRecorder er, MimeMessage msg) throws MessagingException{
		//if(!msg.isMimeType("application/pkcs7-mime")) {
		if (msg.getContentType().contains("application/pkcs7-mime")){
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
	public static boolean isMDN (ErrorRecorder er, byte[] msg, byte[] directCertificate, String password) throws MessagingException{
		WrappedMessageProcessor processor = new WrappedMessageProcessor();
		processor.messageParser(er, msg , directCertificate, password);
		if (processor.getIsMDN()) {
			return true;
		}
				return false;	
	}
		
	
	

}
