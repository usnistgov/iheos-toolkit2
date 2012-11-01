package gov.nist.toolkit.xdstools2.client.siteActorManagers;

import gov.nist.toolkit.results.client.SiteSpec;

public class GetDocumentsSiteActorManager extends BaseSiteActorManager {

	public SiteSpec verifySiteSelection() {
		
		return queryTab.getQueryBoilerplate().getSiteSelection();
	}
	
	
	public String getEndpointSelectionHelp() {
		return "This tool can take on one of serveral roles: a Document Consumer sending a Registry Stored Query " +
		"to a Document Registry; a Document Consumer sending a Registry Stored Query to an Initiating Gateway; " +
		"or an Initiating Gateway sending a Cross-Community Query to a Responding Gateway. To act as a " +
		"Document Consumer sending a Registry Stored Query to a Document Registry, select a Registry. " +
		"To act as an Initiating Gateway sending a Cross-Community Query to a Responding Gateway, select " +
		"a Responding Gateway.  To act as a Document Consumer sending a Registry Stored Query to an Initiating " +
		"Gateway, select both an Initiating Gateway (target of the transaction) and a Responding Gateway (" +
	"establishing the homeCommunityId to be placed in the Registry Stored Query request.";
	}


}
