package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author dazais
 *
 */
public class MessageStatusLogger {
	
	public MessageStatusLogger(){
		
	}

	// Logging a message status
	public void logMessageStatus(String status, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws IOException {
		String statusLogPath = ls.getMessageStatusLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(status, statusLogPath);
		}
	


}
