package gov.nist.direct.logger;

import java.util.ArrayList;

public class UserLog {
	private String username;
	private ArrayList<MessageLog> messageLogs;

	public UserLog(String _username){
		username = _username;
	}
	
	public ArrayList<MessageLog> getLog(){
		return messageLogs;
		
	}
	
	public UserLog readUserLogs(String username){
		LogPathsSingleton ls = LogPathsSingleton.getLogStructureSingleton();
		// parse folder directsend
		ls.getDIRECT_MESSAGE_FOLDER();
		// parse all folders (messageIds)
		// parse folder direct
	
		// get whole message contents - not done yet
		// get message status
	//	String s = getMessageStatusLogPath(String transactionType, String messageType, String username, String messageId);
	//	String s = getDateLogPath(String transactionType, String messageType, String username, String messageId);
		// parse folder mdn
		
		// try directreceive
		
		return null;
		
	}
	
public ArrayList<UserLog> readAllLogs(){
	return null;
		
	}
}
