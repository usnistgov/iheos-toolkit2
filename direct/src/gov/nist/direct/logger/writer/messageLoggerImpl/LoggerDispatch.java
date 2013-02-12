package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.writer.MessageLoggerInterface;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class LoggerDispatch implements MessageLoggerInterface {

	@Override
	public void log(Object o) throws IOException {
		if(!o.equals(null)){ 
		
			// Logging a Direct Message
			if (o instanceof MimeMessage){
				DirectMessageLogger directLogger = new DirectMessageLogger();
				try {
					directLogger.log((MimeMessage)o);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
			
			// Logging an MDN Message
			if (o instanceof MimeMultipartReport){
				MDNLogger mdnLogger = new MDNLogger();
				try {
					mdnLogger.log((MimeMultipartReport)o);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		
		}
	}
	
	
	

}
