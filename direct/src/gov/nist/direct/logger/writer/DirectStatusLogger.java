package gov.nist.direct.logger.writer;

import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Logs status of message validation to MDN and Direct log file structure
 * @author dazais
 *
 */
public class DirectStatusLogger {

	// Logging a message status
	public void logStatus(String s, File f) throws IOException {
		Utils.writeToFile(s, f);
	}


}
