/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.logger.reader;

import gov.nist.direct.logger.LogPathsSingleton;
import gov.nist.direct.utils.Utils;
import gov.nist.direct.utils.ValidationUtils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.apache.mailet.base.mail.MimeMultipartReport;

public class DirectLogReader {
	
	/**
	 * UserID should probably be the user email address.
	 * @param userID
	 * @return
	 */
	public DirectLogReader(){
		
	}



	public MimeMessage readDirectMessage (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String messageContentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		return Utils.getMimeMessage(messageContentsLogPath);
	}
	
	public byte[] readDirectMessageToByteArray (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String messageContentsLogPath = ls.getDirectMessageLogPath(transactionType, messageType, username, messageId);
		return Utils.getMessage(messageContentsLogPath);
	}
	
public MimeMultipartReport readMDNMessage (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String mdnLogPath = ls.getMDNLogPath(transactionType, messageType, username, messageId);
	return Utils.getMDN(mdnLogPath);
	}

public byte[] readMDNMessageToByteArray (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String mdnLogPath = ls.getMDNLogPath(transactionType, messageType, username, messageId);
	return Utils.getMessage(mdnLogPath);
	}

public MimeMessage readEncryptedDirectMessage (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String encryptedLogPath = ls.getEncryptedMessageLogPath(transactionType, messageType, username, messageId);
	return Utils.getMimeMessage(encryptedLogPath);
	
}

public String readMessageStatus (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String statusLogPath = ls.getMessageStatusLogPath(transactionType, messageType, username, messageId);
	ArrayList<String> read = Utils.readFile(new File(statusLogPath));
	
	// ignore 2nd and later lines of the file, only the first one contains status
	return read.get(0).trim();
	
}


public Date readMDNReceivedDate (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
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


public Date readMDNExpirationDate (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
	String expDatePath = ls.getDateExpirationLogPath(transactionType, messageType, username, messageId);
	String str = Utils.readFile(expDatePath);
	
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
