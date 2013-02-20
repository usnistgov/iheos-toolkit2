package gov.nist.direct.logger.writer.messageLoggerImpl;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.logger.writer.MessageLoggerInterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;


// 2) Logging message processing status (waiting for MDN, error, etc).
// TODO this is probably a duplicate of Directmsglogger
public class MDNLogger {
	
	public MDNLogger(){
	}

		public void log(MimeMessage mdn, LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) throws FileNotFoundException, IOException {
			String mdnLogPath = ls.getEncryptedMessageLogPath(transactionType, messageType, username, messageId);
	
			try {
				mdn.writeTo(new FileOutputStream(mdnLogPath));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		

}
