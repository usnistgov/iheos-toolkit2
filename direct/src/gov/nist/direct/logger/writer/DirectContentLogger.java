package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author dazais
 *
 */
public class DirectContentLogger {


	public void logMessageContents(MimeMessage msg, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws IOException {
		String contentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		try {
			msg.writeTo(new FileOutputStream(contentsLogPath));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}



}
