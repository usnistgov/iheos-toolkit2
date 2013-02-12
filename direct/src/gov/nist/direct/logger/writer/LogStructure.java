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
	 
	private final String MESSAGE_CONTENTS = "/message-contents.txt"; // needs part number + ".txt" ext.
	private final String DECRYPTED_MESSAGE = "/encrypted-message"; 
	private final String MESSAGE_STATUS = "/status.txt";
	private final String TIMESTAMP = "/timestamp.txt";
	

	public LogStructure(String logRoot){
		LOG_ROOT = logRoot;
	}
	
	
	

	public String getLOG_ROOT() {
		return LOG_ROOT;
	}
	
	
	public String getMessageContentsLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MESSAGE_CONTENTS;
		return path;
	}
	
	
	public String getEncryptedMessageLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DECRYPTED_MESSAGE;
		return path;
	}
	
	public String getMessageStatusLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MESSAGE_STATUS;
		return path;
	}
	
	public String getDateLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + TIMESTAMP;
		return path;
	}
	
	/**
	 * Utility function that returns part of the log path
	 * @param transactionType
	 * @param messageType
	 * @param username
	 * @param messageId
	 * @return
	 */
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
