package gov.nist.toolkit.desktop.client.legacy.siteActorManagers;


import gov.nist.toolkit.desktop.client.legacy.genericQueryTab.GenericQueryTab;

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
	

//	abstract public SiteSpec verifySiteSelection();

}
