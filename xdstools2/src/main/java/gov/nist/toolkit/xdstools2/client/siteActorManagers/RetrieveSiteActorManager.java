package gov.nist.toolkit.xdstools2.client.siteActorManagers;

public class RetrieveSiteActorManager extends GetDocumentsSiteActorManager {

	public String getEndpointSelectionHelp() {
		return "This tool can take on one of serveral roles: a Document Consumer sending a Retrieve Document Set " +
		"to a Document Repository; a Document Consumer sending a Retrieve Document Set to an Initiating Gateway; " +
		"or an Initiating Gateway sending a Cross-Community Retrieve to a Responding Gateway. To act as a " +
		"Document Consumer sending a Retrieve Document Request to a Document Repository, select a Repository. " +
		"To act as an Initiating Gateway sending a Cross-Community Retrieve to a Responding Gateway, select " +
		"a Responding Gateway.  To act as a Document Consumer sending a Retrieve Document Set to an Initiating " +
		"Gateway, select both an Initiating Gateway (target of the transaction) and a Responding Gateway (" +
	"establishing the homeCommunityId to be placed in the Retrieve Document Set request.";
	}

}
