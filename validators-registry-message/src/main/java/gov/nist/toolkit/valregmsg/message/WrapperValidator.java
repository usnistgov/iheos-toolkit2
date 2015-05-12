package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.utilities.xml.SchemaValidation;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

/**
 * Validate wrappers of a message.  Wrappers are the XML elements that wrap the metadata
 * contents of a message.
 * @author bill
 *
 */
public class WrapperValidator extends MessageValidator {
	OMElement xml;
	Map<String, List<String>> wrapperList = new HashMap<String, List<String>>();
	List<String> elementOrder = new ArrayList<String>();

	public WrapperValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.xml = xml;
		init();
	}
	
	void err(String msg, String ref) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msg, this, ref);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		String transaction = vc.getTransactionName();
		List<String> expectedWrappers = wrapperList.get(transaction);

		if (xml == null) {
			err("No content present", "");
			return;
		}

		if (expectedWrappers == null) {
//			er.err("Do not have expected wrappers for validation type of " + vc.getTransactionName(), "Internal Error");
			return;
		}

		validateWrappers(xml, expectedWrappers);

//		checkElementOrder(xml);

	}

	void initElementOrder() {
		elementOrder.add("Slot");
		elementOrder.add("Name");
		elementOrder.add("Description");
		elementOrder.add("VersionInfo");
		elementOrder.add("Classification");
		elementOrder.add("ExternalIdentifier");
	}

	void initWrapperList() {
		List<String> x;
		ValidationContext v;

		v = new ValidationContext();
		x = new ArrayList<String>();
		x.add("ProvideAndRegisterDocumentSetRequest");
		x.add("SubmitObjectsRequest");
		x.add("RegistryObjectList");
		v.isPnR = true;
		v.isRequest = true;
		wrapperList.put(v.getTransactionName(), x);

		v = new ValidationContext();
		x = new ArrayList<String>();
		x.add("SubmitObjectsRequest");
		x.add("RegistryObjectList");
		v.isR = true;
		v.isRequest = true;
		wrapperList.put(v.getTransactionName(), x);
		v.isR = false;
		v.isXDM = true;
		wrapperList.put(v.getTransactionName(), x);
		v.isXDM = false;

		v = new ValidationContext();
		x = new ArrayList<String>();
		x.add("AdhocQueryRequest");
		v.isSQ = true;
		v.isRequest = true;
		wrapperList.put(v.getTransactionName(), x);
		v.isXC = true;
		wrapperList.put(v.getTransactionName(), x);

		v = new ValidationContext();
		x = new ArrayList<String>();
		x.add("AdhocQueryResponse");
		x.add("RegistryObjectList");
		v.isSQ = true;
		v.isResponse = true;
		wrapperList.put(v.getTransactionName(), x);
		v.isXC = true;
		wrapperList.put(v.getTransactionName(), x);

		v = new ValidationContext();
		x = new ArrayList<String>();
		x.add("RetrieveDocumentSetRequest");
		x.add("DocumentRequest");
		v.isRet = true;
		v.isRequest = true;
		wrapperList.put(v.getTransactionName(), x);
		v.isXC = true;
		wrapperList.put(v.getTransactionName(), x);
	}

	@SuppressWarnings("unchecked")
	void checkElementOrder(OMElement ele) {
		if (ele == null)
			return;
		for (Iterator<OMElement> it = ele.getChildElements(); it.hasNext(); ) {
			OMElement ele1 = it.next();
			String ele1Name = ele1.getLocalName();
			OMElement ele2 = getNextOMElementSibling(ele1);
			if (ele2 != null) {
				String ele2Name = ele2.getLocalName();
				if (!canFollow(ele1Name, ele2Name))
					err(
							"Child elements of " + ele.getLocalName() + "(id=" + new Metadata().getId(ele) + ")" +
							" are out of Schema required order:   " +
							" element " + ele2.getLocalName() + " cannot follow element " + ele1.getLocalName() + 
							". Elements must be in this order " + elementOrder
							," ebRIM 3.0 Schema");
			}
			checkElementOrder(ele1);
		}
	}

	boolean canFollow(String element, String nextElement) {
		if (element == null || element.equals(""))
			return false;
		if (nextElement == null || nextElement.equals(""))
			return false;
		int elementI = elementOrder.indexOf(element);
		int nextElementI = elementOrder.indexOf(nextElement);
		return elementI == -1 || nextElementI == -1 || elementI <= nextElementI;
	}

	void init() {
		initWrapperList();
		initElementOrder();
	}

	OMElement getNextOMElementSibling(OMElement ele) {
		OMNode n = null;
		for (n = ele.getNextOMSibling(); n != null && !(n instanceof OMElement); n = n.getNextOMSibling())
			;
		return (OMElement) n; 
	}

	String schema_validate(OMElement ahqr, int metadata_type) throws XdsInternalException {
		String schema_messages = null;
		try {
			schema_messages = SchemaValidation.validate(ahqr, metadata_type);
			return schema_messages;
		} catch (Exception e) {
			throw new XdsInternalException("Schema Validation threw internal error: " + e.getMessage());
		}
	}

	boolean isSubmission() {
		return vc.isSubmit();
	}

	boolean hasPeerElement(OMElement ele) {
		OMNode next = ele.getNextOMSibling();
		if (next != null && (next instanceof OMElement))
			return true;
		OMNode prev = ele.getPreviousOMSibling();
		if (prev != null && (prev instanceof OMElement)) 
			return true;
		return false;
	}
	
	OMElement findPeerWithName(OMElement ele, String name) {
		if (name == null)
			return null;
		
		OMNode focus = ele;
		
		while (focus != null) {
			if ((focus instanceof OMElement)) {
				OMElement focusEle = (OMElement) focus;
				if (focusEle.getLocalName().equals(name))
				return focusEle;
			}
			focus = focus.getNextOMSibling();
		}
		return null;
	}

	void validateWrappers(OMElement ele, List<String> expectedWrappers) {
		List<String> foundWrappers = new ArrayList<String>();
		for (String wrapper : expectedWrappers) {
			OMElement focus = findPeerWithName(ele, wrapper);
			if (focus == null) {
				err("Expected metadata wrappers " + expectedWrappers +
						" but found " + foundWrappers + " but not " + wrapper, "ebRS 3.0");
				return;
			}
			foundWrappers.add(wrapper);
			ele = focus.getFirstElement();
		}
	}



}
