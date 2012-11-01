package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

public class SimpleTransaction extends BasicTransaction {
	OMElement metadata_ele = null;
	String action = null;
	
	public SimpleTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}
	
	
	public void run(OMElement ignore)  throws XdsInternalException {

		if (metadata_ele == null)
			throw new XdsInternalException("SimpleTransaction: metadata_ele is null");

		xds_version = this.xds_b;

		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for RegisterTransaction instruction within step " + this.s_ctx.get("step_id"));

		OMElement request = metadata_ele;

//		log_metadata(request);

		if (testConfig.prepare_only)
			return;
		
		soap_1_2 = false;

		try {
//			setMetadata(request);
			soapCall(request);
			OMElement result = getSoapResult();
			validate_registry_response(
					result, 
					(xds_version == xds_a) ? MetadataTypes.METADATA_TYPE_R : MetadataTypes.METADATA_TYPE_SQ);


			if (result != null) {
				this.s_ctx.add_name_value(instruction_output, "Result", result);
			} else {
				this.s_ctx.add_name_value(instruction_output, "Result", "None");
				s_ctx.set_error("Result was null");
			}

			s_ctx.add_name_value(instruction_output, "Result", result);

			RegistryResponseParser registry_response = new RegistryResponseParser(result);

			if ( registry_response.is_error())
				fail(registry_response.get_regrep_error_msg());

		} 
		catch (Exception e) {
			fail(e.getMessage());
		}


	}


	protected void parseInstruction(OMElement part)
			throws FactoryConfigurationError, XdsInternalException {
		String part_name = part.getLocalName();
		if (part_name.equals("MetadataFile")) {
			metadata_filename = testConfig.testplanDir + part.getText();
			metadata_ele = Util.parse_xml(new File(metadata_filename));
			s_ctx.add_name_value(instruction_output, "MetadataFile", metadata_filename);
		} else if (part_name.equals("Metadata")) { 
			metadata_filename = "";
			metadata_ele = part.getFirstElement();
		}
		else if (part_name.equals("Action")) {
			action = part.getText();
		}
		else if (part_name.equals("Addressing")) {
			useAddressing = true;
		}
		else if (part_name.equals("Mtom")) {
			useMtom = true;
		}
		else
			parseBasicInstruction(part);
	}


	protected String getRequestAction() {
		return action;
	}


	protected String getBasicTransactionName() {
		return "s";
	}

}
