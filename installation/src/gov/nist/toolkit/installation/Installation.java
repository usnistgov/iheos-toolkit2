package gov.nist.toolkit.installation;


import java.io.File;

import javax.servlet.ServletContext;

public class Installation {
	File warHome = null;
	File externalCache = null;
	String sep = File.separator;
	
	PropertyServiceManager propertyServiceMgr = null;

	static Installation me = null;

	static public Installation installation() {
		if (me == null)
			me = new Installation();
		return me;
	}
	
	static public Installation installation(ServletContext servletContext) {
		if (me == null)
			me = new Installation();
		if (me.warHome == null)
			me.warHome = new File(servletContext.getRealPath("/"));
		return me;
	}

	private Installation() {   }
	
	public File warHome() { 
		return warHome; 
		}
	public void warHome(File warHome) { 
		this.warHome = warHome; 
		}
	public File externalCache() { return externalCache; }
	public void externalCache(File externalCache) { this.externalCache = externalCache; }
	
	public boolean initialized() { return warHome != null && externalCache != null; }
	
	public PropertyServiceManager propertyServiceManager() {
		if (propertyServiceMgr == null)
			propertyServiceMgr = new PropertyServiceManager(warHome);
		return propertyServiceMgr;
	}
	
	public File simDbFile() {
		return propertyServiceManager().getSimDbDir();
	}
	
	public File toolkitxFile() {
		return new File(Installation.installation().warHome() + sep + "toolkitx");
	}
	
	public File environmentFile(String envName) {
		return new File(externalCache + sep + "environment" + sep + envName);
	}

	public File environmentFile() {
		return new File(externalCache + sep + "environment");
	}
	
	public File directSendLogFile(String userName) {
		return new File(externalCache + sep + "direct" + sep + "sendlog" + sep + userName);
	}

	public File sessionLogFile(String sessionId) {
		return new File(warHome + sep + "SessionCache" + sep + sessionId);
	}

	public File sessionCache() {
		return new File(warHome + sep + "SessionCache");
	}

	public File testLogFile(String user) {
		return new File(externalCache + sep + "TestLogCache" + sep + user);
	}

}
