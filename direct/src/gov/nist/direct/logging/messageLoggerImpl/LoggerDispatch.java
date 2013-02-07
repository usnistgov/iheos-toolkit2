package gov.nist.direct.logging.messageLoggerImpl;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

import com.sun.mail.iap.ByteArray;

import gov.nist.direct.logging.MessageLogger;

public class LoggerDispatch implements MessageLogger {

	@Override
	public boolean log(Object o) throws IOException {
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
		
			
		} // end if object not null
		return false;
	}

}
