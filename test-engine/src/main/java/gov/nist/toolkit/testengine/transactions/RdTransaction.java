package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.axiom.om.OMElement;

public class RdTransaction extends RegisterTransaction {

	public RdTransaction(StepContext s_ctx, OMElement instruction,
                         OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	public void run(OMElement request) throws XdsException {
		doSoapCall(request);
	}

		protected String getRequestAction() {
		return "urn:ihe:iti:2017:RemoveDocuments";
	}

	protected String getBasicTransactionName() {
		return "rd";
	}
}
