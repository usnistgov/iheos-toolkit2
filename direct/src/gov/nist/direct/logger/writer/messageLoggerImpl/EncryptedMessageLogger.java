package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.writer.MessageLoggerInterface;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.hamcrest.core.IsInstanceOf;

public class EncryptedMessageLogger {

	public void log(MimeMessage msg, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws FileNotFoundException, IOException {
		String encryptedLogPath = ls.getEncryptedMessageLogPath(transactionType, messageType, username, messageId);

		try {
			msg.writeTo(new FileOutputStream(encryptedLogPath));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	
}
