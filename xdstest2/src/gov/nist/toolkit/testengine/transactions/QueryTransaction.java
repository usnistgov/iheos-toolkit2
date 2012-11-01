package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.StepContext;

import org.apache.axiom.om.OMElement;

abstract public class QueryTransaction extends BasicTransaction {
	
	public QueryTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		parse_metadata = false;
	}


}
