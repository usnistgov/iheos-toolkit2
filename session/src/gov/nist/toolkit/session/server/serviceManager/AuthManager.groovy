package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.installation.Installation;

import java.io.File;

public class AuthManager {

	public boolean authenticate(String user, String passwd) {
		def realPasswd
		File passwdFile = passwdFile(user)
		if (!passwdFile.exists())
			return false
		passwdFile.withReader { realPasswd = it.readLine() }
		return realPasswd == passwd
	}
	
	File passwdFile(String user) {
		return new File(Installation.installation().getExternalCache().toString() + 
				File.separator + "users" + File.separator + "passwds" + File.separator + user + ".txt");
	}
}
