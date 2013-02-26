package gov.nist.direct.logger;

import java.util.ArrayList;

public class UserLog {
	private static LogPathsSingleton ls;

	public UserLog(String _username, ArrayList<MessageLog> _messageLogs){
		ls = LogPathsSingleton.getLogStructureSingleton();
	}

	/**
	 * Reads all logs for a given user.
	 * @param username
	 * @return
	 */
	public static ArrayList<MessageLog> readUserLogs(String username){
		String transactionType;
		ArrayList<MessageLog> userLogs_Receive = new ArrayList<MessageLog>();
		ArrayList<MessageLog> userLogs_Send = new ArrayList<MessageLog>();
		ArrayList<MessageLog> allUserLogs = new ArrayList<MessageLog>();


		// parse folder Direct Send
		transactionType =	ls.getDIRECT_SEND_FOLDER();
		userLogs_Send = parseTransactionFolders(username, transactionType);

		// parse folder Direct Receive
		transactionType =	ls.getDIRECT_RECEIVE_FOLDER();
		userLogs_Receive = parseTransactionFolders(username, transactionType);

		allUserLogs.addAll(userLogs_Send);
		allUserLogs.addAll(userLogs_Receive);

		return allUserLogs;

	}

	/**
	 * Parses all folders for one Transaction Type (Direct send or receive)
	 * in order to read all existing message IDs.
	 */
	private static ArrayList<MessageLog> parseTransactionFolders(String username, String transactionType) {
		ArrayList<MessageLog> userLog = new ArrayList<MessageLog>();
		ArrayList<String> messageIds = LoggerUtils.listFilesForFolder(transactionType);
		String id;
		MessageLog singleMsgLog;

		while (messageIds.iterator().hasNext()){
			id = messageIds.iterator().next();
			singleMsgLog = MessageLog.readLog(username, transactionType, id);
			userLog.add(singleMsgLog);
		}
		return userLog;

	}

}
