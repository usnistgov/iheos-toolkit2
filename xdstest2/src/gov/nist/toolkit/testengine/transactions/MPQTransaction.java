package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.StepContext;

import org.apache.axiom.om.OMElement;

public class MPQTransaction extends StoredQueryTransaction {

	public MPQTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	protected String getRequestAction() {
		if (xds_version == BasicTransaction.xds_a)
			return null;
//		if (async) {
//			if (is_xca) {
//				return "urn:ihe:iti:2007:CrossGatewayQuery"; 
//			} else {
//				return "urn:ihe:iti:2007:RegistryStoredQuery";
//			}
//		} else {
//			if (is_xca) {
//				return "urn:ihe:iti:2007:CrossGatewayQuery";
//			} else {
				return "urn:ihe:iti:2009:MultiPatientStoredQuery";
//			}
//		}
	}

	public void configure() {
		useAddressing = true;
		soap_1_2 = true;
	}

	protected String getBasicTransactionName() {
		return "mpq";
	}



}
