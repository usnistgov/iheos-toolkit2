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

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;

import java.io.File;
import java.util.regex.Pattern;

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
	

	

	/**
	 * Display-only fields
	 */
	private final String DIRECT_RECEIVE_LABEL_FOR_DISPLAY = "Direct Receive";
	private final String DIRECT_SEND_LABEL_FOR_DISPLAY = "Direct Send";
	private final String DIRECT_MESSAGE_LABEL = "Direct";
	private final String MDN_MESSAGE_LABEL = "MDN";
	

	public String getDIRECT_MESSAGE_LABEL() {
		return DIRECT_MESSAGE_LABEL;
	}


	public String getMDN_MESSAGE_LABEL() {
		return MDN_MESSAGE_LABEL;
	}




	private String LOG_ROOT;
	
	private final String DIRECT_RECEIVE_FOLDER = File.separator + "directReceive";
	private final String DIRECT_SEND_FOLDER = File.separator + "directSend";
	
	private final String DIRECT_MESSAGE_FOLDER = File.separator + "direct";
	private final String MDN_MESSAGE_FOLDER = File.separator + "mdn";
	 
	private final String MDN_MESSAGE_CONTENTS = File.separator + "mdn-contents.txt";
	private final String TEST_SESSION = File.separator + "test-session.txt";
	private final String DIRECT_MESSAGE_CONTENTS = File.separator + "direct-contents.txt"; // needs part number + ".txt" ext.
	private final String DECRYPTED_MESSAGE = File.separator + "encrypted-message.txt"; 
	private final String MDN_VALIDATION_STATUS = File.separator + "mdn-validation-status.txt";
	private final String DIRECT_ORIGINAL_MSG_VALIDATION_STATUS = File.separator + "direct-orig-msg-validation-status.txt";
	private final String DATE_LOG = File.separator + "direct-received-date.txt";
	private final String MDN_RECEIVED_DATE_LOG = File.separator + "mdn-received-date.txt";
	private final String EXPIRATION_DATE_LOG = File.separator + "expiration-date.txt";
	private final String LABEL_LOG = File.separator + "label.txt";
	private static final String MESSAGE_ID_LOG = File.separator + "message-id.txt";
	

	
	
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
  * @return
  */
	public static String getLOG_ROOT() {
		//return "C:\\direct-logs";
		return Installation.installation().directLogs().toString();
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
	
	public String getTestSessionLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + TEST_SESSION;
		return path;
	}
	
	public String getEncryptedMessageLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DECRYPTED_MESSAGE;
		return path;
	}
	
	public String getMDNValidationStatusLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MDN_VALIDATION_STATUS;
		return path;
	}
	
	public String getDirectOriginalValidationStatusLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DIRECT_ORIGINAL_MSG_VALIDATION_STATUS;
		return path;
	}
	
	
	public String getDIRECT_RECEIVE_FOLDER() {
		return DIRECT_RECEIVE_FOLDER;
	}


	public String getDIRECT_SEND_FOLDER() {
		return DIRECT_SEND_FOLDER;
	}


	public String getDIRECT_RECEIVE_LABEL_FOR_DISPLAY() {
		return DIRECT_RECEIVE_LABEL_FOR_DISPLAY;
	}


	public String getDIRECT_SEND_LABEL_FOR_DISPLAY() {
		return DIRECT_SEND_LABEL_FOR_DISPLAY;
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
		return MDN_VALIDATION_STATUS;
	}


	public String getDATE_LOG() {
		return DATE_LOG;
	}


	public String getDateLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + DATE_LOG;
		return path;
	}
	
	public String getMDNReceivedDateLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MDN_RECEIVED_DATE_LOG;
		return path;
	}
	
	
	
	public String getDateExpirationLogPath(String transactionType, String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + EXPIRATION_DATE_LOG;
		return path;
	}
	

	public String getLabelLogPath(String transactionType, String messageType,
			String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + LABEL_LOG;
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

	public String getAttachmentLogPath(String transactionType, String messageType, String username, String messageId, String attachmentName) {
		String usernamePath = "direct-logs" + File.separator + username;
		String fullPath = usernamePath + File.separator + messageId;
		fullPath = Installation.installation().warHome() + File.separator + fullPath;
		
		String path = fullPath + File.separator + attachmentName;
		
		
		// check if directory exists
		File dir2 = new File(fullPath);
		if(!dir2.exists()) {
			dir2.mkdirs();
			dir2.setWritable(true);
			dir2.setReadable(true);
			System.out.println("Created directory "+ fullPath);
		}
		return path;
	}
	
	public String getAttachmentLink(String transactionType, String messageType, String username, String messageId, String attachmentName) {
		String usernamePath = "direct-logs/" + username;
		String fullPath = usernamePath + "/" + messageId;
		
		String warPath = Installation.installation().warHome().toString();
		String separatorPattern = Pattern.quote(File.separator);
		String[] warSplit = warPath.split(separatorPattern);
		warPath = warSplit[warSplit.length-1];
		
		PropertyServiceManager manager = Installation.installation().propertyServiceManager();
		
		String path = warPath + "/" + fullPath + "/" + attachmentName;
		path = "http://" + manager.getToolkitHost() + ":" + manager.getToolkitPort() + "/" + path;
		
		
		
		return path;
	}


	public String getMessageIdLogPath(String transactionType,
			String messageType, String username, String messageId) {
		String fullPath = getFullPath(transactionType, messageType, username, messageId);
		String path = fullPath + MESSAGE_ID_LOG;
		return path;
	}	
	
}
