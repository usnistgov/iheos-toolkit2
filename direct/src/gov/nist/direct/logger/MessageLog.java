package gov.nist.direct.logger;

import gov.nist.direct.logger.reader.DirectLogReader;
import gov.nist.direct.logger.writer.DirectContentLogger;
import gov.nist.direct.logger.writer.MessageStatusLogger;
import gov.nist.direct.logger.writer.TimeLogger;
import gov.nist.direct.logger.writer.messageLoggerImpl.MDNLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MessageLog {
	
	// attributes relevant to the Direct message sent
	private String messageId;
	private Date expirationDate; // delay after which MDN is considered as arriving too late

	
	// MDN message, received
	private Date receivedDate;
	private String status;
	private LogPathsSingleton ls;
	
	
	public MessageLog(String _messageId, LogPathsSingleton _ls){
		messageId = _messageId;
		ls = _ls;
		
	}
	
	/**
	 * Completes a Direct message log with matching MDN logs
	 * @param messageId
	 */
		public void logMDN(MimeMessage m, String status, String username, String transactionType, String messageType, String messageId, String receivedDate){
		// Log MDN status
		MessageStatusLogger dl = new MessageStatusLogger();
		try {
			dl.logMessageStatus(status, ls, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Log received date
				TimeLogger tl = new TimeLogger();
				try {
					tl.logDate(receivedDate, ls, transactionType, messageType, username, messageId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				// Log full MDN message
						MDNLogger mdnlog = new MDNLogger();
						try {
							mdnlog.log(m, ls, transactionType, messageType, username, messageId);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 		
		
	
	}
	
	public void logDirectMessage(String username, String directMsgDateSent, String transactionType, String messageType, MimeMessage directMessage){
		// Log Direct message sent date
		TimeLogger tl = new TimeLogger();
		try {
			tl.logDate(directMsgDateSent, ls, transactionType, messageType, username, messageId);
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
			tl.logDate(expirationDate.toString(), ls, transactionType, messageType, username, messageId);
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
	}
	
	
	// uses username and messageid
	public void readLog(String username){
		
	}
	


}
