package gov.nist.direct.logger.writer;

import gov.nist.direct.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Logs dates to MDN and Direct log file structure
 * @author dazais
 *
 */
public class TimeLogger {

	public void logDate(String s, File f) throws IOException {
			Utils.writeToFile(s, f);
		}




}
