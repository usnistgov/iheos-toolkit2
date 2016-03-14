package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class BasicTransactionWrapper extends BasicTransaction {

	public void run() throws XdsException {

	}

	protected BasicTransactionWrapper(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		
	}

	@Override
	protected String getRequestAction() {
		return null;
	}

	@Override
	protected String getBasicTransactionName() {
		return null;
	}

	@Override
	protected void parseInstruction(OMElement part) throws XdsInternalException {

	}

	@Override
	protected void run(OMElement request) throws XdsException {

	}
}
