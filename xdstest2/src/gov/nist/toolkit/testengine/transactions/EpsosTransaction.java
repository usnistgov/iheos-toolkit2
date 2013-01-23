package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;

import org.apache.axiom.om.OMElement;

public class EpsosTransaction extends XCQTransaction{

	public EpsosTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		// TODO Auto-generated constructor stub
	}

	protected String getBasicTransactionName() {
		return "epsos";
	}

	public void initializeMtom() {
		useMtom = true;
	}

	protected String getRequestAction() {
		return SoapActionFactory.epsos_xcqr_action;
	}

	protected int getMetadataType() {
		return MetadataTypes.METADATA_TYPE_EPSOS;
	}



}
