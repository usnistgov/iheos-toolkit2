package gov.nist.direct.logger;

import gov.nist.direct.logger.writer.DirectStatusLogger;
import gov.nist.direct.logger.writer.TimeLogger;
import gov.nist.direct.logger.writer.messageLoggerImpl.MDNLogger;

import java.io.IOException;
import java.util.Date;

public class MessageLog {
	
	// attributes relevant to the Direct message sent
	private String messageId;
	private Date directMsgDateSent;
	private Date expirationDate; // delay after which MDN is considered as arriving too late

	
	// MDN message, received
	private Date receivedDate;
	private String status;
	private LogPathsSingleton ls;
	
	
	public MessageLog(String _messageId, Date _directMsgDateSent, LogPathsSingleton _ls, String transactionType, String messageType, String username){
		messageId = _messageId;
		directMsgDateSent = _directMsgDateSent;
		ls = _ls;
		
		logDirectMessage(username, transactionType, messageType);
	}
	
	/**
	 * Completes a Direct message log with matching MDN logs
	 * @param messageId
	 */
	public void logMDN(String status, String username, String transactionType, String messageType, String messageId, Date receivedDate){
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
					tl.logDate(receivedDate.toString(), ls, transactionType, messageType, username, messageId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		// Log full MDN message
		//MDNLogger mdnlog = new MDNLogger();
		//.log(msg, ls, transactionType, messageType, username, messageId);
		
		
		
	
	}
	
	public void logDirectMessage(String username, String transactionType, String messageType){
		// Log Direct message sent date
		TimeLogger tl = new TimeLogger();
		try {
			tl.logDate(directMsgDateSent.toString(), ls, transactionType, messageType, username, messageId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// don't log expiration date
		
		// Log full Direct Message
	}
	
	


}
