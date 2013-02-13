package gov.nist.direct.logger.reader;

import gov.nist.direct.logger.writer.LogStructure;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class DirectLogReader {
	// use Java File Scanner API
	// http://docs.oracle.com/javase/6/docs/api/java/util/Scanner.html
	// use Utils.readFile()
	
	public MimeMessage readDirectMessage (LogStructure ls, String transactionType, String messageType, String username, String messageId) {
		return null;
		
	}
	
public MimeMultipartReport readMDNMessage (LogStructure ls, String transactionType, String messageType, String username, String messageId) {
	return null;
		
	}

public MimeMessage readEncryptedDirectMessage (LogStructure ls, String transactionType, String messageType, String username, String messageId) {
	return null;
	
}

public String readMessageStatus (LogStructure ls, String transactionType, String messageType, String username, String messageId) {
	return null;
	
}

public String readMessageLogDate (LogStructure ls, String transactionType, String messageType, String username, String messageId) {
	return null;
	
}
	
}
