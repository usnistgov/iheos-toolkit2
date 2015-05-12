package gov.nist.toolkit.xdstools2.client.siteActorManagers;

import gov.nist.toolkit.results.client.SiteSpec;

public class FindDocumentsSiteActorManager extends BaseSiteActorManager {


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
		"Gateway, select an Initiating Gateway (target of the transaction). Since this query (Find Documents) " +
		"includes a Patient ID parameter, the Initiating Gateway will broadcast the query to cooperating Responding Gateways " +
		"and return the aggregated results.";
	}



}
