package gov.nist.timer;

import java.util.Date;

public abstract class MessageTimestamp {

	
	public abstract Date getTimestamp();

	public abstract void setTimestamp(Date timestamp);
	
	public abstract String getMessageID();

	public abstract void setMessageID(String messageID);
	

}
