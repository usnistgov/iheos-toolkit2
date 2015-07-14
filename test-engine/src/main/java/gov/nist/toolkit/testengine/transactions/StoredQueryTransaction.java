package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.testengine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.Iterator;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;


public class StoredQueryTransaction extends QueryTransaction {
	OMElement expected_contents = null;
	boolean is_xca = false;
	boolean clean_params = false;

	public StoredQueryTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
	}

	public void run(OMElement request) throws XdsException {

		runSQ(request);
	}
	
	// need to do this logging later - changes made in runSQ
	protected void logInputMetadata(OMElement metadata) throws XdsInternalException {
	}


	public void configure() {
		isSQ = true;
		if (isB()) {
			useAddressing = true;
			soap_1_2 = true;
		} else {  // xds.a
			useAddressing = false;
			soap_1_2 = false;
		}
	}

	public void setIsXCA(boolean isXca) { is_xca = isXca; }
	
	public void initializeMtom() {
		useMtom = false;
	}
	
	protected int getMetadataType() {
		return MetadataTypes.METADATA_TYPE_SQ;
	}

	protected OMElement runSQ(OMElement request) throws XdsInternalException, FactoryConfigurationError,
	XdsException {
		OMElement result = null;

		configure();

		initializeMtom();

		OMNamespace ns = request.getNamespace();
		String ns_uri = (ns != null) ? ns.getNamespaceURI() : null;
		int metadata_type = getMetadataType();


		// verify input is correct top-level request
		if (parse_metadata) {
			if (ns_uri == null)
				throw new XdsInternalException("Don't understand version of metadata (namespace on root element): " + ns_uri);
			if (! ns_uri.equals("urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0")  )
				throw new XdsInternalException("Don't understand version of metadata (namespace on root element): " + ns_uri);
			if (! request.getLocalName().equals("AdhocQueryRequest")) 
				throw new XdsInternalException("Stored Query Transaction (as coded in testplan step '" + s_ctx.get("step_id") + 
				"') must reference a file containing an AdhocQueryRequest");
		}

		if (clean_params)
			cleanSqParams(request);
		
		try {
			soapCall(request);
			result = getSoapResult();

		}
		catch (Exception e) {
			fail(ExceptionUtil.exception_details(e));
		}
		validate_registry_response_no_set_status(result, metadata_type);

		if (expected_contents != null ) {
			String errors = validate_assertions(result, metadata_type, expected_contents);

			if (errors.length() > 0) {
				fail(errors);
			}
		}

		add_step_status_to_output();



		return result;
	}

	protected String getRequestAction() {
		if (xds_version == BasicTransaction.xds_a)
			return null;
		if (async) {
				return "urn:ihe:iti:2007:RegistryStoredQuery";
		} else {
				return "urn:ihe:iti:2007:RegistryStoredQuery";
		}
	}

	protected void parseInstruction(OMElement part) throws XdsInternalException {
		String part_name = part.getLocalName();
		if (part_name.equals("ExpectedContents")) {
			expected_contents = part;
			testLog.add_name_value(instruction_output, "ExpectedContents", part);
		} 
		else if (part_name.equals("UseXPath")) {
			use_xpath.add(part);
			testLog.add_name_value(instruction_output, "UseXRef", part);
		}
		else if (part_name.equals("UseObjectRef")) {
			use_object_ref.add(part);
			testLog.add_name_value(instruction_output, "UseObjectRef", part);
		} 
		else if (part_name.equals("SOAP11")) {
			soap_1_2 = false;
			testLog.add_name_value(instruction_output, "SOAP11", part);
		}
		else if (part_name.equals("SOAP12")) {
			soap_1_2 = true;
			testLog.add_name_value(instruction_output, "SOAP12", part);
		}
		else if (part_name.equals("CleanParams")) {
			clean_params = true;
			testLog.add_name_value(instruction_output, "CleanParams", part);
		}
		else {
			parseBasicInstruction(part);
		}
	}

	protected String getBasicTransactionName() {
		return "sq";
	}

	void cleanSqParams(OMElement ele) {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath ("//*[local-name() = 'AdhocQuery']");
			OMElement adhocQuery = (OMElement) xpathExpression.selectSingleNode(ele);
			if (adhocQuery == null)
				return;
			
			for (Iterator<OMAttribute> it=adhocQuery.getAllAttributes(); it.hasNext(); ) {
				OMAttribute at = it.next();
				System.out.println("attvalue is " + at.getAttributeValue());
				if (at.getAttributeValue().indexOf("$") != -1)
					adhocQuery.removeAttribute(at);
			}

			for (OMElement slot : MetadataSupport.childrenWithLocalName(adhocQuery, "Slot")) {
				OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "ValueList");
				for (OMElement value : MetadataSupport.childrenWithLocalName(valueList, "Value")) {
					String valueStr = value.getText();
					int i = valueStr.indexOf("$");
					if (i == -1)
						continue;
					i = valueStr.indexOf("$", i+1);
					if (i == -1)
						continue;
					value.detach();
				}
				if (!valueList.getChildElements().hasNext()) {
					valueList.detach();
					slot.detach();
				}
			}
		} catch (JaxenException e) {
		}

	}

}
