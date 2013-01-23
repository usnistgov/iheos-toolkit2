package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.HomeAttribute;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class XCQTransaction extends StoredQueryTransaction {
	String expectedHomeCommunityId = null;

	public XCQTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	public void run(OMElement metadata_element) throws XdsException {
//		expectedHomeCommunityId = s_ctx.get("homeCommunityId");
		this.xds_version = BasicTransaction.xds_b;

		// manual linkage
		if (metadata_element == null) throw new XdsInternalException("XGQTransaction.run(): metadata is null");

		setIsXCA(true);
		OMElement result_ele = runSQ(metadata_element);


		if (result_ele == null) {
			String fail = getFail();
			if (fail == null)
				throw new XdsInternalException("Result to XCA Query is null");
			else
				throw new XdsInternalException(fail);
		}
		Metadata result_metadata = MetadataParser.parseNonSubmission(result_ele);

		if (expectedHomeCommunityId != null) {
			if ( !expectedHomeCommunityId.startsWith("urn:oid:")) {
				s_ctx.set_error("Expected homeCommunityId value is [" + expectedHomeCommunityId + "]. It is required to have a [urn:oid:] prefix.");
				failed();
			}

			HomeAttribute homeAtt = new HomeAttribute(expectedHomeCommunityId);
			String errors = homeAtt.validate(result_ele);
			if ( ! errors.equals("")) {
				s_ctx.set_error(errors);
				failed();
			}

		
			for (OMElement registryError : MetadataSupport.decendentsWithLocalName(result_ele, "RegistryError")) {
				String location = registryError.getAttributeValue(MetadataSupport.location_qname);
				if (location == null || !location.equals(expectedHomeCommunityId)) {
					s_ctx.set_error("location attribute on RegistryError must be set to homeCommunityId of the Responding Gateway, found instead " + location);
					failed();
				}
			}
		}
		
		
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		String part_name = part.getLocalName();
		if (part_name.equals("homeCommunityId")) {
			expectedHomeCommunityId =  part.getText();
			testLog.add_name_value(instruction_output, "homeCommunityId", expectedHomeCommunityId);
		} 
		else
			super.parseInstruction(part);
	}

	protected String getBasicTransactionName() {
		return "xcq";
	}

	public void configure() {
		useAddressing = true;
		soap_1_2 = true;
	}

	protected String getRequestAction() {
		return "urn:ihe:iti:2007:CrossGatewayQuery"; 
	}



}
