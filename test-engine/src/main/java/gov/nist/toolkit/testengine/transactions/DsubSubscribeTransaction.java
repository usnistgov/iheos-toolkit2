package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.HashMap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

public class DsubSubscribeTransaction extends BasicTransaction {

	public DsubSubscribeTransaction(StepContext s_ctx, OMElement instruction,
			OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);

		setXdsVersion(BasicTransaction.xds_b); 

		useAddressing = true;
		soap_1_2 = true;
		useMtom = false;

	}

	protected String getRequestAction() {
		return MetadataSupport.dsub_subscribe_action;
	}

	protected String getBasicTransactionName() {
		return "dsub_sub";
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		parseBasicInstruction(part);		
	}

	protected void run(OMElement metadata_element) throws XdsException {
		OMElement result = null;
		OMElement expected_contents = null;

		try {
//			this.setMetadata(metadata_element);
			soapCall(metadata_element);
			result = getSoapResult();

		}
		catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
		}

		int metadata_type = MetadataTypes.METADATA_TYPE_SQ;
		validateSchema(result, metadata_type);


		if (expected_contents != null ) {
			String errors = validate_assertions(result, metadata_type, expected_contents);

			if (errors.length() > 0) {
				fail(errors);
			}
		}
		
		HashMap<String, String> response_parms_map = new HashMap<String, String>();
		
		System.out.println("result is " + result.getLocalName());
		
		String sub_ref = xpathGet(result, "subscriptionReference", "//*[local-name()='SubscribeResponse']/*[local-name()='SubscriptionReference']/*[local-name()='Address']");
		String sub_id = xpathGet(result, "subscriptionId", "//*[local-name()='SubscribeResponse']/*[local-name()='SubscriptionReference']/*[local-name()='ReferenceParameters']/*[local-name()='SubscriptionId']");
		
		response_parms_map.put("subscriptionRef", sub_ref);
		response_parms_map.put("subscriptionId", sub_id);
		
		
		testLog.add_name_value(instruction_output, generate_xml("SubscriptionParms", response_parms_map));


		add_step_status_to_output();


	}
	
	String xpathGet(OMElement doc, String description, String xpath) throws XdsException {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath (xpath);
			return xpathExpression.stringValueOf(doc);
		} catch (JaxenException e) {
			throw new XdsInternalException("Error extracting " + description + ": " + e.getMessage() + "\n", e);
		}
		
	}
	
	
}
