package gov.nist.direct.mdn.messageDispatch;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MessageDispatchUtils {
	

	
	/**
	 * Checks if a received message is or not an MDN report. If false, then it is either a Direct message,
	 * either a bad message.
	 * @param msg the received message
	 * @return true if the message is an MDN (Message Disposition Notification report),
	 * false if it is not.
	 * @throws MessagingException 
	 */
	public static boolean isMDN (MimeMessage msg) throws MessagingException{
		if (msg.getContentType().contains("multipart/report") ||
				msg.getContentType().contains("message//disposition-notification")) {
			return true;
		}
			else return false;	
	}
		
	
	

}
