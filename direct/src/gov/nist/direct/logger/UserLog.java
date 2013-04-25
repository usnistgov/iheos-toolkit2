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

import gov.nist.direct.client.MessageLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserLog {
	private LogPathsSingleton ls;

	public UserLog(){
		ls = LogPathsSingleton.getLogStructureSingleton();
	}

	/**
	 * Reads all logs for a given user.
	 * @param username
	 * @return
	 */
	public List<MessageLog> readUserLogs(String username){
		System.out.println("User is " + username);
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		
		String transactionType;
		List<MessageLog> userLogs_Receive = new ArrayList<MessageLog>();
		List<MessageLog> userLogs_Send = new ArrayList<MessageLog>();
		List<MessageLog> allUserLogs = new ArrayList<MessageLog>();


		// parse folder Direct Send
		System.out.println("parse folder Direct Send");
		transactionType =	ls.getDIRECT_SEND_FOLDER();
		userLogs_Send = parseTransactionFoldersAndReadLogs(username, transactionType);

		// parse folder Direct Receive
		System.out.println("parse folder Direct Receive");
		transactionType =	ls.getDIRECT_RECEIVE_FOLDER();
		userLogs_Receive = parseTransactionFoldersAndReadLogs(username, transactionType);

		allUserLogs.addAll(userLogs_Send);
		allUserLogs.addAll(userLogs_Receive);

		return allUserLogs;

	}

	/**
	 * Parses all folders for one Transaction Type (Direct send or receive)
	 * in order to read all existing message IDs.
	 */
	private  List<MessageLog> parseTransactionFoldersAndReadLogs(String username, String transactionType) {
		List<MessageLog> userLog = new ArrayList<MessageLog>();
		List<String> messageIds = LoggerUtils.listFilesForFolder(File.separator + username +  transactionType);
		String id;
		MessageLog singleMsgLog;

		for (int i=0; i< messageIds.size();i++){
			id = messageIds.get(i);
			singleMsgLog = MessageLogManager.readLog(username, transactionType, id);
			userLog.add(singleMsgLog);
		}
		return userLog;

	}

}
