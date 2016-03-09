package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;

public class RegisterODDETransaction extends BasicTransaction {

	public RegisterODDETransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		//		this.xds_version = xds_version;
	}

	String transactionName() {
		if (async)
			return "rodde.as";
		else
			return "rodde";
	}

	public void run(OMElement request) 
	throws XdsException {

		useMtom = false;

		// ITI-61 uses xds.b style because it was introduced after .b
		useAddressing = true;
		soap_1_2 = true;

		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for RegisterODDETransaction instruction within step " + this.s_ctx.get("step_id"));

		if (parse_metadata && !no_convert) {
			Metadata metadata = MetadataParser.parse(request);
			request = metadata.getV3SubmitObjectsRequest();
		}

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
	
	protected void validate_response(OMElement result) throws XdsException {
		validate_registry_response(
				result,
				MetadataTypes.METADATA_TYPE_SQ);
	}

	protected String getRequestAction() {
		if (async)
			return "urn:ihe:iti:2010:RegisterOnDemandDocumentEntry";
		else
			return "urn:ihe:iti:2010:RegisterOnDemandDocumentEntry";
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		parseBasicInstruction(part);
	}

	protected String getBasicTransactionName() {
		return "rodde";
	}


}
