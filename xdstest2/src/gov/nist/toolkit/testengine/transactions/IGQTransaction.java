package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.StepContext;

import org.apache.axiom.om.OMElement;

public class IGQTransaction extends StoredQueryTransaction {

	public IGQTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	protected String getBasicTransactionName() {
		return "igq";
	}


	public void configure() {
		useAddressing = true;
		soap_1_2 = true;
	}

}
