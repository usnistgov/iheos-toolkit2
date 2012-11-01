package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.axiom.om.OMElement;

public class RetrieveOrderValidator extends OrderValidator {

	public RetrieveOrderValidator(ValidationContext vc, OMElement xml) {
		super(vc, xml);
		init("Schema");
	}

	protected void initElementOrder() {
		elementOrder.add("HomeCommunityId");
		elementOrder.add("RepositoryUniqueId");
		elementOrder.add("DocumentUniqueId");
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		// TODO Auto-generated method stub
		
	}

}
