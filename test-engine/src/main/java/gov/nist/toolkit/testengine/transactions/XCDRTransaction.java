package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import org.apache.axiom.om.OMElement;

public class XCDRTransaction extends
		ProvideAndRegisterTransaction {

	public XCDRTransaction(StepContext s_ctx,
                           OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		headerRequiresHomeCommunityId = true;
	}
	
	protected  String getBasicTransactionName() {
		return "xcdrp";
	}

	@Override
	protected String getRequestAction() {
		if (xds_version == BasicTransaction.xds_b) {
			if (async) {
				return SoapActionFactory.iti_80_transaction;
			}
			return SoapActionFactory.iti_80_transaction;
//			return SoapActionFactory.pnr_b_action;
		}
		return SoapActionFactory.anon_action;
	}
}
