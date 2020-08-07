package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.testengine.engine.HomeAttribute;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

public class XCRmuTransaction extends RegisterTransaction {

	public XCRmuTransaction(StepContext s_ctx, OMElement instruction,
							OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	protected String getRequestAction() {
		return "urn:ihe:iti:2018:RestrictedUpdateDocumentSet";
	}

	protected String getBasicTransactionName() {
		return "xcrmu";
	}

	public void run(OMElement metadata_element) throws XdsException {
		this.xds_version = BasicTransaction.xds_b;

		// manual linkage
		if (metadata_element == null) throw new XdsInternalException("XGQTransaction.run(): metadata is null");

		super.run(metadata_element);
	}

	protected void validate_response(OMElement result) throws XdsInternalException, MetadataException {
		validate_registry_response(
				result,
				"RegistryResponse",
				MetadataTypes.METADATA_TYPE_SQ);
	}

	public void configure() {
		useAddressing = true;
		soap_1_2 = true;
	}
}
