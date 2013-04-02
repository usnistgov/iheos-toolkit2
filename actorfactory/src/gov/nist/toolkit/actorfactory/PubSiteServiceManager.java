package gov.nist.toolkit.actorfactory;

import org.apache.log4j.Logger;

/**
 * Top level site management API referenced by calls from the GUI.  One
 * instance is shared between all sessions. This guarantees that
 * common sites are common. Calls to get session specific simulators
 * are managed through this class. Those calls are passed through to
 * a session specific cache managed by SimManager.
 * @author bill
 *
 */
public class PubSiteServiceManager extends CommonSiteServiceManager {
	static PubSiteServiceManager siteServiceManager = null;

	static Logger logger = Logger.getLogger(PubSiteServiceManager.class);

	private PubSiteServiceManager() {
	}
	
	static public PubSiteServiceManager getSiteServiceManager() {
		if (siteServiceManager == null)	
			siteServiceManager = new PubSiteServiceManager();
		return siteServiceManager;
	}


}
