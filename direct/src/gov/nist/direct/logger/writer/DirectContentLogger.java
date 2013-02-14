package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author dazais
 *
 */
public class DirectContentLogger {


	public void logMessageContents(String s, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws IOException {
		String contentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(s, new File(contentsLogPath));
		}


}
