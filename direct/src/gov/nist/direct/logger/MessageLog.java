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

package gov.nist.direct.logger;

import gov.nist.direct.logger.reader.DirectLogReader;
import gov.nist.direct.logger.writer.DirectContentLogger;
import gov.nist.direct.logger.writer.LabelLogger;
import gov.nist.direct.logger.writer.MessageStatusLogger;
import gov.nist.direct.logger.writer.TimeLogger;
import gov.nist.direct.logger.writer.messageLoggerImpl.MDNLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.mail.internet.MimeMessage;

public class MessageLog {
	// general attributes
	private String testReference;
	private String transactionType; // DirectSend or DirectReceive
	private String messageType; // Direct or MDN

	// attributes relevant to the Direct message sent
	private String messageId;
	private Date expirationDate; // delay after which MDN is considered as arriving too late


	// MDN message, received
	private Date mdnReceivedDate;
	private String status;
	private LogPathsSingleton ls;
	private String label;

	/**
	 * Stores a single message log
	 * @param _testReference
	 * @param _transactionType
	 * @param _messageType
	 * @param _messageId
	 * @param _expirationDate
	 * @param _mdnReceivedDate
	 * @param _status
	 * @param _label 
	 * @param _ls
	 */
	public MessageLog(String _testReference, String _transactionType, String _messageType, String _messageId, Date _expirationDate, Date _mdnReceivedDate, String _status, String _label){
		testReference = _testReference;
		transactionType = _transactionType;
		messageType = _messageType;
		messageId = _messageId;
		expirationDate = _expirationDate;
		mdnReceivedDate = _mdnReceivedDate;
		status = _status;
		label = _label;

	}

	/**
	 * Completes a Direct message log with matching MDN logs
	 * @param messageId
	 */
	public static void logMDN(MimeMessage m, String status, String username, String transactionType, String messageType, String messageId, String receivedDate){
		// Log MDN status
		MessageStatusLogger dl = new MessageStatusLogger();
		try {
			dl.logMessageStatus(status, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Log received date
		TimeLogger tl = new TimeLogger();
		try {
			tl.logDate(receivedDate, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Log full MDN message
		MDNLogger mdnlog = new MDNLogger();
		try {
			mdnlog.log(m, transactionType, messageType, username, messageId);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	public static void logDirectMessage(String username, String directMsgDateSent, String transactionType, String messageType, String messageId, MimeMessage directMessage, String label){
		// Log Direct message sent date
		TimeLogger tl = new TimeLogger();
		try {
			tl.logDate(directMsgDateSent, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// log expiration date
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		ls.getDateExpirationLogPath(transactionType, messageType, username, messageId);
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
		Date expirationDate =  cal.getTime(); // returns new date object, one hour in the future
		try {
			tl.logDate(expirationDate.toString(), transactionType, messageType, username, messageId);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Log full Direct Message
		DirectContentLogger dcl = new DirectContentLogger();
		try {
			dcl.logMessageContents(directMessage, ls, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Log Label
		LabelLogger ll = new LabelLogger();
		try {
			ll.logLabel(label, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Reads a single message log (Direct message + possible MDN acknowledgement + statuses and expiration dates)
	 * 
	 * No message type (direct / mdn) because a log includes both the Direct message sent and its MDN counterpart
	 * @param username
	 * @param transactionType
	 * @param messageId
	 * @return
	 */
	public static MessageLog readLog(String username, String transactionType, String messageId){
		MessageLog log;
		String messageType;
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();

		DirectLogReader reader = new DirectLogReader();

		// **** parse folder Direct ****
		messageType =	ls.getDIRECT_MESSAGE_FOLDER();

		// read whole message content - should get path only
		//MimeMessage directContents = reader.readDirectMessage(ls, transactionType,  messageType, username, messageId);

		// read message status
		String status = reader.readMessageStatus(ls, transactionType,  messageType, username, messageId);

		// read label
		String label = reader.readLabel(ls, transactionType,  messageType, username, messageId);
		
		// read expiration date
		Date expirationDate = reader.readMDNExpirationDate(ls, transactionType, messageType, username, messageId);


		// **** parse folder MDN ****
		messageType =	ls.getMDN_MESSAGE_FOLDER();

		// read MDN actual receive date
		Date mdnReceivedDate = reader.readMDNReceivedDate(ls, transactionType, messageType, username, messageId);

		
		return new MessageLog("", transactionType, messageType, messageId, expirationDate, mdnReceivedDate, status, label);	
	}



}
