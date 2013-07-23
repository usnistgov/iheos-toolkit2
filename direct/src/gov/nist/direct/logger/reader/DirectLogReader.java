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
import gov.nist.direct.logger.LoggerUtils;
import gov.nist.direct.utils.Utils;

import java.io.File;
import java.util.ArrayList;

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
		String statusLogPath = ls.getMDNValidationStatusLogPath(transactionType, messageType, username, messageId);
		ArrayList<String> read = Utils.readFile(new File(statusLogPath));

		// ignore 2nd and later lines of the file, only the first one contains status
		if (read.size() > 0)
			return read.get(0).trim();
		else
			return "";
	}


	public String readOrigDirectMessageStatus (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String statusLogPath = ls.getDirectOriginalValidationStatusLogPath(transactionType, messageType, username, messageId);
		ArrayList<String> read = Utils.readFile(new File(statusLogPath));

		// ignore 2nd and later lines of the file, only the first one contains status
		if (read.size() > 0)
			return read.get(0).trim();
		else
			return "";
	}


	public String readDirectSendDate (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String directLogPath = ls.getDateLogPath(transactionType, messageType, username, messageId);
		if (!new File(directLogPath).canRead())
			return "";
		return LoggerUtils.readTextFileFirstLine(directLogPath);
	}


	public String readMDNReceivedDate (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String mdnLogPath = ls.getMDNReceivedDateLogPath(transactionType, messageType, username, messageId);
		if (!new File(mdnLogPath).canRead())
			return "";
		return LoggerUtils.readTextFileFirstLine(mdnLogPath);
	}


	public String readMDNExpirationDate (LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String expDatePath = ls.getDateExpirationLogPath(transactionType, messageType, username, messageId); 
		if (!new File(expDatePath).canRead())
			return "";
		return LoggerUtils.readTextFileFirstLine(expDatePath);
	}


	public String readLabel(LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String labelLogPath = ls.getLabelLogPath(transactionType, messageType, username, messageId);
		ArrayList<String> read = Utils.readFile(new File(labelLogPath));

		// ignore 2nd and later lines of the file, only the first one contains status
		if (read.size() > 0)
			return read.get(0).trim();
		else
			return "";
	}



	public String readMDNMessageID(LogPathsSingleton ls, String transactionType, String messageType, String username, String messageId) {
		String mdnMsgID = ls.getMessageIdLogPath(transactionType, messageType, username, messageId);
		if (!new File(mdnMsgID).canRead())
			return "";
		return LoggerUtils.readTextFileFirstLine(mdnMsgID);
	}




}
