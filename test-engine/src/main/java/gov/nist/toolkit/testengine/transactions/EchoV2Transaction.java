package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.*;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import java.io.File;
import java.util.Iterator;

public class EchoV2Transaction  extends BasicTransaction {
	
	public EchoV2Transaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}
	public void run(OMElement metadata_element) 
	throws XdsException {
		String metadata_filename = null;
		Iterator elements = instruction.getChildElements();
		while (elements.hasNext()) {
			OMElement part = (OMElement) elements.next();
			String part_name = part.getLocalName();
			if (part_name.equals("MetadataFile")) {
				metadata_filename = part.getText();
				testLog.add_name_value(instruction_output, "MetadataFile", metadata_filename);
			} else if (part_name.equals("ParseMetadata")) {
				String value = part.getText();
				testLog.add_name_value(instruction_output, "ParseMetadata", value);
				if (value.equals("False"))
					parse_metadata = false;
			}
		}

		if (s_ctx.getRegistryEndpoint() == null)
			throw new XdsInternalException("No registry endpoint specified");
		if (metadata_filename == null)
			throw new XdsInternalException("No MetadataFile element found for EchoV2Transaction instruction within step " + s_ctx.get("step_id"));


		// input file is read twice. Adding to log and then sending through Axis2 results in
		// no output in the log. Axiom does not seem to have a recursive cloner available.
		// so this is easier for now.

		Metadata metadata = null;
		try {
			metadata = new Metadata(new File(metadata_filename), parse_metadata);
			
		} 
		catch (MetadataValidationException e) {
			throw new XdsInternalException(ExceptionUtil.exception_details(e));
		}
		catch (MetadataException e) {
			e.printStackTrace();
			throw new XdsInternalException(ExceptionUtil.exception_details(e, "Error parsing metadata: filename is " + metadata_filename + ", Error is: " + e.getMessage()));
		}

		if (parse_metadata) {
			// verify input is correct top-level request
			if ( ! metadata.getRoot().getLocalName().equals("EchoV2Metadata"))
				throw new XdsInternalException("Echo Transaction (as coded in testplan step '" + s_ctx.get("step_id") + "') must reference a file containing an EchoV2Metadata");

		} 
		testLog.add_name_value(	instruction_output, 
				"InputMetadata", 
				Util.deep_copy(metadata.getRoot()));


		Options options = new Options();
		options.setTo(new EndpointReference(s_ctx.getRegistryEndpoint())); // this sets the location of MyService service
		try {
			ServiceClient serviceClient = new ServiceClient();
			serviceClient.setOptions(options);
			OMElement result = serviceClient.sendReceive(metadata.getRoot());

			if (result != null) {
//				testLog.add_name_value(instruction_output, "Result", result);

				validate_registry_response(result, MetadataTypes.METADATA_TYPE_R);
			}

		} catch (AxisFault e) {
			throw new XdsInternalException(ExceptionUtil.exception_details(e));
		}

	}
	@Override
	protected String getRequestAction() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void parseInstruction(OMElement part) throws XdsInternalException {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected String getBasicTransactionName() {
		// TODO Auto-generated method stub
		return null;
	}


}
