package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.valsupport.client.ValidationContext;


/**
 * Validate the order of elements in a RetrieveResponse message.
 * @author bill
 *
 */
public class RetrieveResponseOrderValidator extends OrderValidator {

	public RetrieveResponseOrderValidator(ValidationContext vc) {
		super(vc);
		init("Schema");	}

	protected void initElementOrder() {
		elementOrder.add("HomeCommunityId");
		elementOrder.add("RepositoryUniqueId");
		elementOrder.add("DocumentUniqueId");
		elementOrder.add("mimeType");
		elementOrder.add("Document");
	}


}
