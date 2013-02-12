package gov.nist.direct.logger.writer.messageLoggerImpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;

import org.apache.mailet.base.mail.MimeMultipartReport;


// 2) Logging message processing status (waiting for MDN, error, etc).
public class MDNLogger {

	public boolean log(MimeMultipartReport mdn) throws FileNotFoundException, IOException, MessagingException {
		mdn.writeTo(new FileOutputStream("UnwrappedDirectMessage.txt"));
		
		return false;
	}

}
