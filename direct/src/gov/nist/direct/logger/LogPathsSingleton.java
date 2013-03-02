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

import java.io.File;
import java.sql.Time;

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
	
	private final String DIRECT_RECEIVE_FOLDER = File.separator + "directReceive";
	private final String DIRECT_SEND_FOLDER = File.separator + "directSend";
	
	private final String DIRECT_MESSAGE_FOLDER = File.separator + "direct";
	private final String MDN_MESSAGE_FOLDER = File.separator + "mdn";
	 
	private final String MDN_MESSAGE_CONTENTS = File.separator + "mdn-contents.txt";
	private final String DIRECT_MESSAGE_CONTENTS = File.separator + "direct-contents.txt"; // needs part number + ".txt" ext.
	private final String DECRYPTED_MESSAGE = File.separator + "encrypted-message.txt"; 
	private final String MESSAGE_STATUS = File.separator + "status.txt";
	private final String DATE_LOG = File.separator + "date.txt";
	private final String EXPIRATION_DATE_LOG = File.separator + "expiration-date.txt";
	private final String LABEL_LOG = File.separator + "label.txt";
	
	

	
	
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
		//return Installation.installation().directLogs().toString();
		return "C:\\direct-logs";
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



	
	
	
}
