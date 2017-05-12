package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.valsupport.client.ValidationContext;

public class RetrieveOrderValidator extends OrderValidator {

	public RetrieveOrderValidator(ValidationContext vc) {
		super(vc);
		init("Schema");
	}

	protected void initElementOrder() {
		elementOrder.add("HomeCommunityId");
		elementOrder.add("RepositoryUniqueId");
		elementOrder.add("DocumentUniqueId");
	}

}
