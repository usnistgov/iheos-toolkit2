package gov.nist.direct.logger.writer;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Logs dates to MDN and Direct log file structure.
 * @author dazais
 *
 */
public class TimeLogger {

	public void logDate(String s, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws IOException {
		String dateLogPath = ls.getDateLogPath(transactionType, messageType, username, messageId);
		Utils.writeToFile(s, new File(dateLogPath)); // ask to overwrite? which failsafes?
		}

	
	

}
