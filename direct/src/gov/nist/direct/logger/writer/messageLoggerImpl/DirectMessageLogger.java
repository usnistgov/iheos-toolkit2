package gov.nist.direct.logger.writer.messageLoggerImpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class DirectMessageLogger{

	public boolean log(MimeMessage msg) throws FileNotFoundException, IOException, MessagingException {
		msg.writeTo(new FileOutputStream("UnwrappedDirectMessage.txt"));
		
		return false;
	}

	
	
	
}
