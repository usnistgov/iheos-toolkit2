package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import junit.framework.TestCase;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

public class TestSupport extends TestCase {

	OMElement root;
	OMElement wrapper;
	protected Metadata m;
	short version = 3;
	
	String metadataToString() {
		return root.toString();
	}

	OMFactory fac() { return MetadataSupport.om_factory; }

	OMElement add_child(String name, OMElement parent) {
		OMElement c = fac().createOMElement(name, (version == 3) ? MetadataSupport.ebRIMns3 : MetadataSupport.ebRIMns2);
		parent.addChild(c);
		return c;
	}
	
	OMAttribute add_att(String name, String value, OMElement parent) {
		OMAttribute att = fac().createOMAttribute(name, null, value);
		parent.addAttribute(att);
		return att;
	}
	
	OMElement add_name(String name_value, OMElement parent) {
		OMElement name_ele = add_child("Name", parent);
		OMElement loc = add_child("LocalizedString", name_ele);
		add_att("value", name_value, loc);
		return name_ele;
	}
	
	OMElement add_class(String class_scheme, String classified_object_id, OMElement parent) {
		OMElement class_ele = add_child("Classification", parent);
		add_att("classificationScheme", class_scheme, class_ele);
		add_att("classifiedObject", classified_object_id, class_ele);
		return class_ele;
	}

	OMElement add_main_class(String class_scheme, String classified_object_id, OMElement parent) {
		OMElement class_ele = add_child("Classification", parent);
		add_att("classificationNode", class_scheme, class_ele);
		add_att("classifiedObject", classified_object_id, class_ele);
		return class_ele;
	}

	protected OMElement add_object(String name) {
		return add_child(name, wrapper);
	}

	protected OMElement add_object(String name, String id) {
		OMElement obj = add_object(name);
		obj.addAttribute("id", id, null);
		return obj;
	}

	protected OMElement add_slot(String name, String value, OMElement parent) {
		OMElement slot = add_child("Slot", parent);
		slot.addAttribute("name", name, null);
		OMElement value_list = add_child("ValueList", slot);
		OMElement value_ele = add_child("Value", value_list);
		value_ele.addChild(fac().createOMText(value));
		return slot;
	}

	protected OMElement add_slot(String name, String value, String value2, OMElement parent) {
		OMElement slot = add_child("Slot", parent);
		slot.addAttribute("name", name, null);
		OMElement value_list = add_child("ValueList", slot);
		OMElement value_ele = add_child("Value", value_list);
		value_ele.addChild(fac().createOMText(value));
		OMElement value_ele2 = add_child("Value", value_list);
		value_ele2.addChild(fac().createOMText(value2));
		return slot;
	}

	OMElement add_ext_id(String identificationScheme, String value, OMElement parent) {
		OMElement e = add_child("ExternalIdentifier", parent);
		e.addAttribute("identificationScheme", identificationScheme, null);
		e.addAttribute("value", value, null);
		return e;
	}

	protected OMElement add_ss(String id) {
		OMElement ss = add_object("RegistryPackage");
		add_ext_id("urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8", "myuid", ss);
		ss.addAttribute("id", id, null);
		return ss;
	}

	protected OMElement add_fol(String id) {
		OMElement fol = add_object("RegistryPackage");
		add_ext_id("urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a", "myuid", fol);
		fol.addAttribute("id", id, null);
		return fol;
	}

	protected OMElement add_assoc(String source, String type, String target) {
		OMElement a = add_object("Association");
		a.addAttribute("sourceObject", source, null);
		a.addAttribute("associationType", (version == 2) ? type : "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type, null);
		a.addAttribute("targetObject", target, null);
		return a;
	}
	
	protected OMElement add_bad_namespace_assoc(String source, String type, String target) {
		OMElement a = add_object("Association");
		a.addAttribute("sourceObject", source, null);
		a.addAttribute("associationType", (version == 3) ? type : "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type, null);
		a.addAttribute("targetObject", target, null);
		return a;
	}
	
	public void setUp() {
		root = fac().createOMElement("SubmitObjectsRequest", MetadataSupport.ebRSns3);
		wrapper = add_child("LeafRegistryObjectList", root);
	}

}
