package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.IOException;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author dazais
 *
 */
public class MessageStatusLogger {
	 LogPathsSingleton ls;
	
	public MessageStatusLogger(){
		 ls = LogPathsSingleton.getLogStructureSingleton();
	}

	// Logging a message status
	public void logMessageStatus(String status, String transactionType, String messageType, String username, String messageId) throws IOException {
		String statusLogPath = ls.getMessageStatusLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(status, statusLogPath);
		}
	


}
