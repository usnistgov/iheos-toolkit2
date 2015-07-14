package gov.nist.toolkit.xdstools2.client.siteActorManagers;

import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

public abstract class BaseSiteActorManager {
	GenericQueryTab queryTab;
	BaseSiteActorManager sam;
	

	public BaseSiteActorManager() {
		sam = this;
	}
	
	public void setGenericQueryTab(GenericQueryTab qt) {
		queryTab = qt;
	}
	
	public abstract String getEndpointSelectionHelp();
	

	abstract public SiteSpec verifySiteSelection(); 

}
