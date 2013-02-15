package gov.nist.direct.logger;

import gov.nist.toolkit.installation.Installation;

import java.io.File;

/**
 * Current structure is:
 * username > directReceive or Send > messageId > direct or mdn > data as text files.
 * 
 * An example is available in the test package.
 * 
 * @author dazais
 *
 */
public class LogPathsSingleton {
	
	private String LOG_ROOT;
	
	private final String DIRECT_RECEIVE_FOLDER = "\\directReceive";
	private final String DIRECT_SEND_FOLDER = "\\directSend";
	
	private final String DIRECT_MESSAGE_FOLDER = "\\direct";
	private final String MDN_MESSAGE_FOLDER = "\\mdn";
	 
	private final String MDN_MESSAGE_CONTENTS = "\\mdn-contents.txt";
	private final String DIRECT_MESSAGE_CONTENTS = "\\direct-contents.txt"; // needs part number + ".txt" ext.
	private final String DECRYPTED_MESSAGE = "\\encrypted-message.txt"; 
	private final String MESSAGE_STATUS = "\\status.txt";
	private final String DATE_LOG = "\\date.txt";
	
	
	private static LogPathsSingleton LogStructureSingleton;
	
	
	/**
	 * Private constructor
	 * @param logRoot
	 */
	private LogPathsSingleton(String logRoot){
		LOG_ROOT = logRoot;
	}
	
	
	public static synchronized LogPathsSingleton getLogStructureSingleton() {
		if (LogStructureSingleton == null) {
			String logRoot = getLOG_ROOT();
			
			// check if directory exists
			File dir = new File(logRoot);
			if(!dir.exists()) {
				dir.mkdir();
				dir.setWritable(true);
				dir.setReadable(true);
				System.out.println("Created directory "+ logRoot);
			}
			LogStructureSingleton = new LogPathsSingleton(logRoot);
		}
		return LogStructureSingleton;
	}

	
	
	
 /**
  * Todo needs to be changed to tk_props
  * @return
  */
	public static String getLOG_ROOT() {
//		if (Installation.installation().externalCache().getPath() != null){
//			return File.separator + "direct-logs";
//		} 
		//return Installation.installation().externalCache().getPath() + File.separator + "direct + " + File.separator + "logs";
		return File.separator + "direct" + File.separator + "logs";
	}
	
	
	public String getDirectMessageLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DIRECT_MESSAGE_CONTENTS;
		return path;
	}
	
	public String getMDNLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MDN_MESSAGE_CONTENTS;
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
	
	public String getDIRECT_RECEIVE_FOLDER() {
		return DIRECT_RECEIVE_FOLDER;
	}


	public String getDIRECT_SEND_FOLDER() {
		return DIRECT_SEND_FOLDER;
	}


	public String getDIRECT_MESSAGE_FOLDER() {
		return DIRECT_MESSAGE_FOLDER;
	}


	public String getMDN_MESSAGE_FOLDER() {
		return MDN_MESSAGE_FOLDER;
	}


	public String getMDN_MESSAGE_CONTENTS() {
		return MDN_MESSAGE_CONTENTS;
	}


	public String getDIRECT_MESSAGE_CONTENTS() {
		return DIRECT_MESSAGE_CONTENTS;
	}


	public String getDECRYPTED_MESSAGE() {
		return DECRYPTED_MESSAGE;
	}


	public String getMESSAGE_STATUS() {
		return MESSAGE_STATUS;
	}


	public String getDATE_LOG() {
		return DATE_LOG;
	}


	public String getDateLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DATE_LOG;
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
	private String getFullPath(String transactionType, String messageType, String username, String messageId){
		String defaultPath = LOG_ROOT + File.separator + username + DIRECT_RECEIVE_FOLDER + File.separator + messageId + DIRECT_MESSAGE_FOLDER;

		if ((transactionType == "DIRECT_SEND") &&  (messageType == "DIRECT")){	
			return LOG_ROOT + File.separator + username + DIRECT_SEND_FOLDER + File.separator + messageId + DIRECT_MESSAGE_FOLDER;
	}
		if ((transactionType == "DIRECT_SEND") &&  (messageType == "MDN")){	
			return LOG_ROOT + File.separator + username + DIRECT_SEND_FOLDER + File.separator + messageId + MDN_MESSAGE_FOLDER;
	}
		if ((transactionType == "DIRECT_RECEIVE") &&  (messageType == "DIRECT")){	
			return defaultPath;	}
		
		if ((transactionType == "DIRECT_RECEIVE") &&  (messageType == "MDN")){	
			return LOG_ROOT + File.separator + username + DIRECT_RECEIVE_FOLDER + File.separator + messageId + MDN_MESSAGE_FOLDER;
	}
		return defaultPath;
	}


	
	
	
}
