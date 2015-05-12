package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.axiom.om.OMElement;

public class MetadataOrderValidator extends OrderValidator {


	public MetadataOrderValidator(ValidationContext vc, OMElement xml) {
		super(vc, xml);
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

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		// TODO Auto-generated method stub
		
	}



}
