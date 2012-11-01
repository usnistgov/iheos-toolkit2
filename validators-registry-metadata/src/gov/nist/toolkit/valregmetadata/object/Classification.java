package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class Classification extends AbstractRegistryObject {
	String classification_scheme = "";
	String code_value = "";
	//String code_display_name = "";
	//String coding_scheme = "";
	String classification_node = "";

	public Classification(String id, String classificationScheme, String code, String codingScheme, String displayName) {
		super(id);
		classification_scheme = classificationScheme;
		code_value = code;
		addSlot("codingScheme", codingScheme);
		//coding_scheme = codingScheme;
		name = displayName;
	}

	public boolean equals(Classification c) {
		if (!c.classification_scheme.equals(classification_scheme)) 
			return false;
		if (!c.code_value.equals(code_value)) 
			return false;
		if (!c.name.equals(name)) 
			return false;
		//		if (!c.coding_scheme.equals(coding_scheme)) 
		//			return false;
		if (!c.classification_node.equals(classification_node)) 
			return false;
		return super.equals(c);
	}

	public OMElement toXml(OMElement parent) throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.classification_qnamens);
		ro.addAttribute(MetadataSupport.id_qname.getLocalPart(), id, null);
		ro.addAttribute(MetadataSupport.classificationscheme_qname.getLocalPart(), classification_scheme, null);
		ro.addAttribute(MetadataSupport.classified_object_qname.getLocalPart(), parent.getAttributeValue(MetadataSupport.id_qname), null);
		ro.addAttribute(MetadataSupport.noderepresentation_qname.getLocalPart(), code_value, null);

		addSlotsXml(ro);
		addNameToXml(ro);
		addDescriptionXml(ro);
		addClassificationsXml(ro);
		addExternalIdentifiersXml(ro);

		return ro;
	}

	public Classification(Metadata m, OMElement cl) throws XdsInternalException  {
		super(m, cl);
		parse();
	}

	void parse()  {
		classification_scheme = ro.getAttributeValue(MetadataSupport.classificationscheme_qname);
		classification_node = ro.getAttributeValue(MetadataSupport.classificationnode_qname);
		if (classification_node == null) classification_node = "";
		code_value = ro.getAttributeValue(MetadataSupport.noderepresentation_qname);

		//		try {
		//			coding_scheme = getSlot("codingScheme").getValue(0);
		//		} catch (Exception e) { }
		//		
		//		//name = getName();
		//		
		//		if (code_value == null) code_value = "";
		//		//if (code_display_name == null) code_display_name = "";
		//		if (coding_scheme == null) coding_scheme = "";
	}

	public String getCodeValue() { return code_value; }
	public String getCodeDisplayName() { return name; }
	public String getCodeScheme()  {
		try {
			return getSlot("codingScheme").getValue(0);
		} catch (Exception e) {
			return "";
		}
	}
	public String getClassificationScheme() { return classification_scheme; }
	public String getClassificationNode() { return classification_node; }

	public String identifyingString() {
		return identifying_string();
	}

	public String identifying_string() {
		String cs = "";
		try {
			cs = getCodeScheme();
		} catch (Exception e) {}
		return "Classification (classificationScheme=" + classification_scheme + " codingScheme=" + cs + ") of object " + parent_id(); 
	}

	public String parent_id() {
		OMElement parent = (OMElement) ro.getParent();
		if (parent == null) return "Unknown";
		return parent.getAttributeValue(MetadataSupport.id_qname);
	}

	public void validateStructure(ErrorRecorder er, ValidationContext vc) {
		validateId(er, vc, "entryUUID", id, "ITI TF-3: 4.1.12.2");
		OMElement parentEle = (OMElement) ro.getParent();
		String parentEleId =  ((parentEle == null) ? "null" :
			parentEle.getAttributeValue(MetadataSupport.id_qname));
		String classifiedObjectId = ro.getAttributeValue(MetadataSupport.classified_object_qname);

		if (parentEle != null && !parentEleId.equals(classifiedObjectId))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": is a child of object " + parentEleId + " but the classifiedObject value is " + 
					classifiedObjectId + ", they must match", this, "ITI TF-3: 4.1.12.2");

		if (getClassificationScheme() == null || getClassificationScheme().equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": does not have a value for the classificationScheme attribute", this, "ebRIM 3.0 section 4.3.1");
		else if (!getClassificationScheme().startsWith("urn:uuid:"))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": classificationScheme attribute value is not have urn:uuid: prefix", this, "ITI TF-3: 4.3.1");

		if (getCodeValue().equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": nodeRepresentation attribute is missing or empty", this, "ebRIM 3.0 section 4.3.1");

		if (getCodeDisplayName().equals("")) 
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": no name attribute", this, "ITI TF-3: 4.1.12.2");

		if (getCodeScheme().equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": no codingScheme Slot", this, "ITI TF-3: 4.1.12.2");

	}

	public OMElement toXml() throws XdsInternalException  {
		return toXml(null);
	}

	public void validateRequiredSlotsPresent(ErrorRecorder er,
			ValidationContext vc) {		
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er,
			ValidationContext vc) {
	}

	public void validateSlotsLegal(ErrorRecorder er) {
	}



}
