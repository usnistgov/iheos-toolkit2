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
}
