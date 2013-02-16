package gov.nist.direct.logger;

import gov.nist.direct.logger.writer.DirectContentLogger;
import gov.nist.direct.logger.writer.DirectStatusLogger;
import gov.nist.direct.logger.writer.TimeLogger;
import gov.nist.direct.logger.writer.messageLoggerImpl.MDNLogger;

import java.io.IOException;
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
	public void logMDN(String status, String username, String transactionType, String messageType, String messageId, String receivedDate){
		// Log MDN status
		DirectStatusLogger dl = new DirectStatusLogger();
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
		//MDNLogger mdnlog = new MDNLogger();
		//.log(msg, ls, transactionType, messageType, username, messageId);
		
		
		
	
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
		
		// Log full Direct Message
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		DirectContentLogger dcl = new DirectContentLogger();
		try {
			dcl.logMessageContents(directMessage.getContent().toString(), ls, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// uses username and messageid
	public void readLog(String username){
		
	}
	


}
