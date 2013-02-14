package gov.nist.direct.logger.reader;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import gov.nist.direct.logger.writer.LogStructureSingleton;
import gov.nist.direct.utils.Utils;
import gov.nist.direct.utils.ValidationUtils;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class DirectLogReader {
	
	/**
	 * UserID should probably be the user email address.
	 * @param userID
	 * @return
	 */
	public ArrayList getUserLogs(String userID, LogStructureSingleton ls){
		//use UserLog
		
		//readMessageStatus for DirectSend / direct + its message id + date
		//readMessageStatus for DirectSend / mdn +date
		
		//same thing for DirectReceive
		return null;
		
	}


	public MimeMessage readDirectMessage (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String messageContentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		return Utils.getMimeMessage(messageContentsLogPath);
	}
	
	public byte[] readDirectMessageToByteArray (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String messageContentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		return Utils.getMessage(messageContentsLogPath);
	}
	
public MimeMultipartReport readMDNMessage (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String mdnLogPath = ls.getMDNLogPath(transactionType, messageType, username, messageId);
	return Utils.getMDN(mdnLogPath);
	}

public byte[] readMDNMessageToByteArray (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String mdnLogPath = ls.getMDNLogPath(transactionType, messageType, username, messageId);
	return Utils.getMessage(mdnLogPath);
	}

public MimeMessage readEncryptedDirectMessage (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String encryptedLogPath = ls.getEncryptedMessageLogPath(transactionType, messageType, username, messageId);
	return Utils.getMimeMessage(encryptedLogPath);
	
}

public String readMessageStatus (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String statusLogPath = ls.getMessageStatusLogPath(transactionType, messageType, username, messageId);
	ArrayList<String> read = Utils.readFile(new File(statusLogPath));
	
	// ignore 2nd and later lines of the file, only the first one contains status
	return read.get(0).trim();
	
}


public Date readLogDate (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String mdnLogPath = ls.getDateLogPath(transactionType, messageType, username, messageId);
	String str = Utils.readFile(mdnLogPath);
	
	try {
		return ValidationUtils.parseDate(str);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
		
}

//
//private ArrayList<Date> readMessageLogDates (LogStructureSingleton ls, String transactionType, String messageType, String username, String messageId) {
//	String dateLogPath = ls.getDateLogPath(transactionType, messageType, username, messageId);
//	ArrayList<String> array = Utils.readFile(new File(dateLogPath));
//	ArrayList<Date> arrayDate = new ArrayList<Date>();
//	int index = 0;
//while (array.iterator().hasNext()) {
//	String next = array.iterator().next();
//	try {
//		arrayDate.add(index,ValidationUtils.parseDate(next));
//	} catch (ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	index++;
//	
//}
//return arrayDate;
//}


	
}
