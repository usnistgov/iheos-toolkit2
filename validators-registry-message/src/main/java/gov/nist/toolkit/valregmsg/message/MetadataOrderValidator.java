package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.valsupport.client.ValidationContext;

public class MetadataOrderValidator extends OrderValidator {


	public MetadataOrderValidator(ValidationContext vc) {
		super(vc);
		init("ebRIM 3.0 Schema");
	}

	protected void initElementOrder() {
		elementOrder.add("Slot");
		elementOrder.add("Name");
		elementOrder.add("Description");
		elementOrder.add("VersionInfo");
		elementOrder.add("Classification");
		elementOrder.add("ExternalIdentifier");
	}


}
