package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.axiom.om.OMElement;


/**
 * Validate the order of elements in a RetrieveResponse message.
 * @author bill
 *
 */
public class RetrieveResponseOrderValidator extends OrderValidator {

	public RetrieveResponseOrderValidator(ValidationContext vc, OMElement xml) {
		super(vc, xml);
		init("Schema");	}

	protected void initElementOrder() {
		elementOrder.add("HomeCommunityId");
		elementOrder.add("RepositoryUniqueId");
		elementOrder.add("DocumentUniqueId");
		elementOrder.add("mimeType");
		elementOrder.add("Document");
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		// TODO Auto-generated method stub
		
	}

}
