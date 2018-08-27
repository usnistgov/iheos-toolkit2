package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.StepContext;
import org.apache.axiom.om.OMElement;

public class RmuTransaction extends RegisterTransaction {

	public RmuTransaction(StepContext s_ctx, OMElement instruction,
                          OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	protected String getRequestAction() {
		return "urn:ihe:iti:2018:RestrictedUpdateDocumentSet";
	}

	protected String getBasicTransactionName() {
		return "rmu";
	}
}
