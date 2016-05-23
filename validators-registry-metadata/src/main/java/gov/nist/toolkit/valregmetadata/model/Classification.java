package gov.nist.toolkit.valregmetadata.model;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrymetadata.Metadata;
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
		return "Classification (classificationScheme=" + classification_scheme + " codingScheme=" + cs + ") of model " + parent_id();
	}

	public String parent_id() {
		OMElement parent = (OMElement) ro.getParent();
		if (parent == null) return "Unknown";
		return parent.getAttributeValue(MetadataSupport.id_qname);
	}


	public OMElement toXml() throws XdsInternalException  {
		return toXml(null);
	}




}
