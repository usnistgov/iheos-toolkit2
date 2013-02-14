package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.LogPathsSingleton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class DirectMessageLogger {

		public void log(MimeMessage msg, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws FileNotFoundException, IOException {
			String directLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);

			try {
				msg.writeTo(new FileOutputStream(directLogPath));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

	
	
}
