package gov.nist.direct.logger;

import java.util.ArrayList;

public class UserLog {
	private ArrayList<MessageLog> messageLogs;

	public UserLog(String _username, ArrayList<MessageLog> _messageLogs){
	}
	
	public static ArrayList<MessageLog> readUserLogs(String username){
		ArrayList<MessageLog> allUserLogs = new ArrayList<MessageLog>();
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		
		// parse folder directsend
	String directSend =	ls.getDIRECT_SEND_FOLDER();
	//String directSendPath = ls.get
		
		// parse all folders (all messageIds)
	ArrayList<String> messageIds = LoggerUtils.listFilesForFolder(directSend);
	String id;
	while (messageIds.iterator().hasNext()){
		id = messageIds.iterator().next();
		MessageLog singleMsgLog = new MessageLog(id, ls);
		singleMsgLog.readLog(id);
	}
	
		
		// parse folder direct
	String direct =	ls.getDIRECT_MESSAGE_FOLDER();
	
		// get whole message contents - not done yet
	String completeDirectMsg = ls.getDIRECT_MESSAGE_CONTENTS();
	
		// get message status
	//	String s = getMessageStatusLogPath(String transactionType, String messageType, String username, String messageId);
	//	String s = getDateLogPath(String transactionType, String messageType, String username, String messageId);
		// parse folder mdn
	String mdn =	ls.getMDN_MESSAGE_FOLDER();
		
	
	
		// do same thing for direct receive
		// not implemented yet
	
	
		
		return null;
		
	}
	
}
