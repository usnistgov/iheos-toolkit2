package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class RegisterTransaction extends BasicTransaction {

	public RegisterTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		//		this.xds_version = xds_version;
	}

	String transactionName() {
		if (async) return "r.as";
		if (xds_version == BasicTransaction.xds_b) return "r.b";
		else return "r.a";
	}

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

		if (parse_metadata && !no_convert) {
			Metadata metadata = MetadataParser.parse(request);
			request = (xds_version == BasicTransaction.xds_a) ? metadata.getV2SubmitObjectsRequest() : metadata.getV3SubmitObjectsRequest();
		}

		if (testConfig.prepare_only)
			return;

		try {
			soapCall(request);
			OMElement result = getSoapResult();
			if (result != null) {
				testLog.add_name_value(instruction_output, "Result", result);

				validate_response(result);

			} else {
				testLog.add_name_value(instruction_output, "Result", "None");
				s_ctx.set_error("Result was null");
			}

		} 
		catch (Exception e) {
			fail(e.getMessage());
			System.out.println(ExceptionUtil.exception_details(e));
		}


	}
	
	protected void validate_response(OMElement result) throws XdsException {
		validate_registry_response(
				result, 
				(xds_version == xds_a) ? MetadataTypes.METADATA_TYPE_R : MetadataTypes.METADATA_TYPE_SQ);
	}

	protected String getRequestAction() {
		if (xds_version == BasicTransaction.xds_b) {
			if (async) 
				return "urn:ihe:iti:2007:RegisterDocumentSet-b";
			else
				return "urn:ihe:iti:2007:RegisterDocumentSet-b";
		} else {
			return "urn:anonOutInOp";
		}
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		parseBasicInstruction(part);
	}

	protected String getBasicTransactionName() {
		return "r";
	}


}
