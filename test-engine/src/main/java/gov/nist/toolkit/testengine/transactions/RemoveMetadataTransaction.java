package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import org.apache.axiom.om.OMElement;

/* TODO KM Changes - new class */
/* TODO - override run() once ready */
public class RemoveMetadataTransaction extends RegisterTransaction {

	public RemoveMetadataTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		
		/* TODO KM Change - need to find out where is set...config? */
		//noMetadataProcessing = true;
		
	}

	protected String getRequestAction() {
		return "urn:ihe:iti:2010:DeleteDocumentSet";
	}

	protected String getBasicTransactionName() {
		return "rm";
	}
	
	@Override
	public void run(OMElement request) 
	throws XdsException {

		validate_xds_version();

		useMtom = false;
		if (isB()) {
			useAddressing = true;
			soap_1_2 = true;
		} else {
			useAddressing = false;
			soap_1_2 = false;
		}

		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for RegisterTransaction instruction within step " + this.s_ctx.get("step_id"));

		/* TODO KM change - interfering with send */
		/*
		if (parse_metadata && !no_convert) {
			Metadata metadata = MetadataParser.parse(request);
			request = (xds_version == BasicTransaction.xds_a) ? metadata.getV2SubmitObjectsRequest() : metadata.getV3SubmitObjectsRequest();
		}
		*/

		if (testConfig.prepare_only)
			return;

		try {
			soapCall(request);
			OMElement result = getSoapResult();
			if (result != null) {
//				testLog.add_name_value(instruction_output, "Result", result);

				validate_response(result);

			} else {
				testLog.add_name_value(instruction_output, "Result", "None");
				s_ctx.set_error("Result was null");
			}

		} 
		catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
			System.out.println(ExceptionUtil.exception_details(e));
		}


	}
	
}
