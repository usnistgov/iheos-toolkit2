package gov.nist.direct.logger.writer;

/**
 * Current structure is:
 * username > directReceive or Send > messageId > direct or mdn > data as text files.
 * 
 * An example is available in the test package.
 * 
 * @author dazais
 *
 */
public class LogStructure {
	
	private String LOG_ROOT;

	
	private final String DIRECT_RECEIVE_FOLDER = "/directReceive";
	private final String DIRECT_SEND_FOLDER = "/directSend";
	
	private final String DIRECT_MESSAGE_FOLDER = "/direct";
	private final String MDN_MESSAGE_FOLDER = "/mdn";
	 
	private final String MESSAGE_CONTENTS = "/message-contents.txt";
	private final String DECRYPTED_MESSAGE = "/decrypted-message"; // needs part number + ".txt" ext.
	private final String MESSAGE_STATUS = "/status.txt";
	private final String TIMESTAMP = "/timestamp.txt";
	

	

	public String getLOG_ROOT() {
		return LOG_ROOT;
	}
	
	
	public String getMessageContentsLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MESSAGE_CONTENTS;
		return path;
	}
	
	
	public String getDecryptedMessageLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DECRYPTED_MESSAGE;
		return path;
	}
	
	public String getMessageStatusLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MESSAGE_STATUS;
		return path;
	}
	
	public String getTimestampLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + TIMESTAMP;
		return path;
	}
	
	public String getFullPath(String transactionType, String messageType, String username, String messageId){
		String defaultPath = LOG_ROOT + "/" + username + DIRECT_RECEIVE_FOLDER + "/" + messageId + DIRECT_MESSAGE_FOLDER;

		if ((transactionType == "DIRECT_SEND") &&  (messageType == "DIRECT")){	
			return LOG_ROOT + "/" + username + DIRECT_SEND_FOLDER + "/" + messageId + DIRECT_MESSAGE_FOLDER;
	}
		if ((transactionType == "DIRECT_SEND") &&  (messageType == "MDN")){	
			return LOG_ROOT + "/" + username + DIRECT_SEND_FOLDER + "/" + messageId + MDN_MESSAGE_FOLDER;
	}
		if ((transactionType == "DIRECT_RECEIVE") &&  (messageType == "DIRECT")){	
			return defaultPath;	}
		
		if ((transactionType == "DIRECT_RECEIVE") &&  (messageType == "MDN")){	
			return LOG_ROOT + "/" + username + DIRECT_RECEIVE_FOLDER + "/" + messageId + MDN_MESSAGE_FOLDER;
	}
		return defaultPath;
	}


	// TODO needs to be changed to a location in tk_props
	public void setLOG_ROOT() {
		LOG_ROOT = "/direct-logs";
		
	}

	
	
	
}
