package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class MockTransaction extends BasicTransaction {

	public MockTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	@Override
	protected void run(OMElement request) throws Exception {
		testLog.add_name_value(instruction_output, "Endpoint", "http://localhost:8080/myendpoint");
		testLog.add_name_value(	instruction_output,
				"InputMetadata", MetadataSupport.om_factory.createOMElement("MyTopElement", null));
		testLog.add_name_value(instruction_output, "OutHeader", MetadataSupport.om_factory.createOMElement("Header", null));
		testLog.add_name_value(instruction_output, "OutAction", "Action");
		testLog.add_name_value(instruction_output, "ExpectedInAction", "Action");
		testLog.add_name_value(instruction_output, "InHeader", MetadataSupport.om_factory.createOMElement("Header", null));
		testLog.add_name_value(instruction_output, "Result", MetadataSupport.om_factory.createOMElement("RegistryResponse", null));
	}

	@Override
	protected void parseInstruction(OMElement part)
			throws XdsInternalException, MetadataException {
		parseBasicInstruction(part);
	}

	@Override
	protected String getRequestAction() {
		return "Request";
	}

	@Override
	protected String getBasicTransactionName() {
		return "Mock";
	}

}
