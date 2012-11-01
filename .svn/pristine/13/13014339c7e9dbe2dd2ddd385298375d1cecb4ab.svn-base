package gov.nist.toolkit.installation;


import java.io.File;

public class Installation {
	File warHome = null;
	File externalCache = null;
	
	PropertyServiceManager propertyServiceMgr = null;

	static Installation me = null;

	static public Installation installation() {
		if (me == null)
			me = new Installation();
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

}
