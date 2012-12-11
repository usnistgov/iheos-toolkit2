package gov.nist.timer.impl;

import gov.nist.timer.MessageTimestamp;

import java.util.Date;

/**
 * Stores time stamps and message IDs for Direct messages "send" functionality.
 * @author dazais
 *
 */
public class DirectMessageTimestamp extends MessageTimestamp {
	Date timestamp;
	String messageID;
	
	public DirectMessageTimestamp(Date date, String id){
		timestamp = date;
		messageID = id;
	}
	
	

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	
	

}
