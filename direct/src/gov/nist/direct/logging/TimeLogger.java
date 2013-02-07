package gov.nist.direct.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import gov.nist.direct.utils.Utils;
import gov.nist.timer.MessageTimestamp;
import gov.nist.timer.SendHistorySingleton;
import gov.nist.timer.impl.DirectMessageTimestamp;

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
