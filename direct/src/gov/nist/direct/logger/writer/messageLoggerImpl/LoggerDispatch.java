package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.writer.LogStructureSingleton;
import gov.nist.direct.logger.writer.MessageLoggerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class LoggerDispatch implements MessageLoggerInterface {

	@Override
	public void log(Object o, LogStructureSingleton ls, String transactionType, String messageType, String partType, String username, String messageId) {

		if(!o.equals(null)){ 
		
			// Logging a Direct Message
			if (o instanceof MimeMessage){
				if( partType == "DECRYPTED_MSG_PART") {
					
					DirectMessageLogger directLogger = new DirectMessageLogger();
					try {
						directLogger.log((MimeMessage)o, ls, transactionType, messageType, username, messageId);
				
					
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
				}
				if( partType == "ENCRYPTED_MSG") {
					EncryptedMessageLogger encryptLogger = new EncryptedMessageLogger();
						try {
							encryptLogger.log((MimeMessage)o, ls, transactionType, messageType, username, messageId);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				} // if
				
				
				
			} // instance of
			
		
			
			// Logging an MDN Message
			if (o instanceof MimeMultipartReport){
				MDNLogger mdnLogger = new MDNLogger();
				try {
					mdnLogger.log((MimeMultipartReport)o, ls, transactionType, messageType, username, messageId);
			
				
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} //mimemultipartReport
			

		} //equals null
	}
	
	
	

}
