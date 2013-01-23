package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class DsubPublishTransaction extends RegisterTransaction {

	public DsubPublishTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		
		xds_version = BasicTransaction.xds_b;
		parse_metadata = false;  // not a valid XDS submission
	}
	
	public void run(OMElement metadata_element) 
	throws XdsException {

		validate_xds_version();

			useAddressing = true;
			soap_1_2 = true;



		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for RegisterTransaction instruction within step " + this.s_ctx.get("step_id"));


		OMElement request = metadata_element;

		if (testConfig.prepare_only)
			return;

		setMetadata(request);



		try {
			//soapSend();

		} 
		catch (Exception e) {
			fail(e.getMessage());
		}


	}
	


	
	protected String getBasicTransactionName() {
		return "dsub_pub";
	}

	protected void validate_response(OMElement result) throws XdsException {
		validate_registry_response(
				result, 
				(xds_version == xds_a) ? MetadataTypes.METADATA_TYPE_R : MetadataTypes.METADATA_TYPE_SQ);
	}

	protected String getRequestAction() {
			return "http://docs.oasis-open.org/wsn/bw-2/NotificationConsumer/Notify";
	}



}
