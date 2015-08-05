package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import org.apache.axiom.om.OMElement;

public class ValidatorCommon implements ErrorRecorder {
	Metadata m;
	RegistryErrorListGenerator rel;
	ValidationContext valCtx = DefaultValidationContextFactory.validationContext();
	List<ErrorRecorder> children = new ArrayList<>();

	public static String NeedReference = "Need Reference";

	public ValidatorCommon(Metadata m) {
		this.m = m;
	}

	// not public on purpose - only to be used by subclasses that
	// take care of initializing Metadata m
	ValidatorCommon() {

	}

	public void cloneEnvironment(ValidatorCommon vc) {
		m         = vc.m;
		rel       = vc.rel;

		valCtx.clone(vc.valCtx);
	}

	/**
	 * Validate ExternalIdentifier
	 * @param parentObjectType
	 * @param parentObjectId
	 * @param externalIds - all ExternalIdentifiers for parentObject
	 * @param name - ExternalIdentifier type
	 * @param id_scheme - ExternalIdentifier identificationScheme
	 * @param is_oid - must value be oid?
	 */
	void validate_ext_id_present(String parentObjectType, String parentObjectId, List<OMElement> externalIds,
			String name, String id_scheme, boolean is_oid) {
		int count = 0;
		for (int i=0; i<externalIds.size(); i++) {
			OMElement e_id = (OMElement) externalIds.get(i);
			String idscheme = e_id.getAttributeValue(MetadataSupport.identificationscheme_qname);
			if ( idscheme == null || ! idscheme.equals(id_scheme))
				continue;
			count++;

			String name_value = m.getNameValue(e_id);
			if (name_value == null)
				err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") has no internal Name element");
			else if ( !name_value.equals(name))
				err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") has incorrect internal Name element (" + name_value + ")");


			int child_count = 0;
			for (Iterator<OMElement> it = e_id.getChildElements(); it.hasNext(); ) {
				OMElement child = it.next();
				child_count++;
				String child_type = child.getLocalName();
				if ( !child_type.equals("Name") && !child_type.equals("Description") && !child_type.equals("VersionInfo"))
					err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") has invalid internal element (" + child_type + ")");
			}
			if (is_oid) {
				String value = e_id.getAttributeValue(MetadataSupport.value_qname);
				if (value == null || value.equals("") || !is_oid(value, valCtx.xds_b))
					err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") requires an OID format value, " + value + " was found");
			}

		}
		if (count == 0)
			err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") is missing");
		if (count > 1)
			err(parentObjectType + " " + parentObjectId + " : ExternalIdentifier of type " + id_scheme + " (" + name + ") is duplicated");
	}

	public void err(String msg) {
		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, "Attribute.java", null, null);
	}

	public void err(Exception e) {
		rel.add_error(MetadataSupport.XDSRegistryMetadataError, e.getMessage(), "Attribute.java", null, null);
	}

	public void err(String msg, String resource) {
		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, null, resource, null);	}

	public static boolean is_oid(String value, boolean xds_b) {
		if (value == null) return false;
		if (xds_b)
			return value.matches("\\d(?=\\d*\\.)(?:\\.(?=\\d)|\\d){0,255}");
		return value.matches("\\d(?=\\d*\\.)(?:\\.(?=\\d)|\\d){0,63}");
	}

	@SuppressWarnings("unchecked")
	void validate_slot(String type, String id, List<OMElement> slots, String name, boolean multivalue, boolean required, boolean number) {
		boolean found = false;
		for (int i=0; i<slots.size(); i++) {
			OMElement slot = (OMElement) slots.get(i);
			String slot_name = slot.getAttributeValue(MetadataSupport.slot_name_qname);
			if ( slot_name == null || !slot_name.equals(name))
				continue;
			if (found)
				err(type + " " + id + " has multiple slots with name " + name);
			found = true;
			OMElement value_list = XmlUtil.firstChildWithLocalName(slot, "ValueList");
			int value_count = 0;
			for (Iterator<OMElement> it=value_list.getChildElements(); it.hasNext(); ) {
				OMElement value = (OMElement) it.next();
				String value_string = value.getText();
				value_count++;
				if (number && !isInt(value_string)) {
					err(type + " " + id + " the value of slot " + name + "(" + value_string + ") is required to be an integer");
				}
			}
			if (	(value_count > 1 && ! multivalue)   ||
					value_count == 0
			)
				err(type + " " + id + " has slot " + name + " is required to have a single value");

		}
		if ( !found && required)
			err(type + " " + id + " does not have the required slot " + name);
	}

	static public boolean isInt(String value)  {
		for (int i=0; i<value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '0':
				continue;
			default:
				return false;
			}
		}
		return true;
	}

	void validate_class(String type, String id, List<OMElement> classs, String classification_scheme, String class_name, boolean required, boolean multiple) {
		int count = 0;
		for (int i=0; i<classs.size(); i++ ) {
			OMElement classif = (OMElement) classs.get(i);

			String scheme = classif.getAttributeValue(MetadataSupport.classificationscheme_qname);
			if ( scheme == null || !scheme.equals(classification_scheme))
				continue;
			count++;

			OMElement name_ele = XmlUtil.firstChildWithLocalName(classif, "Name") ;
			if (name_ele == null)
				err(type + " " + id + " : Classification of type " + classification_scheme + " ( " + class_name + " ) the name attribute is missing");

			OMElement slot_ele = XmlUtil.firstChildWithLocalName(classif, "Slot") ;
			if (slot_ele == null) {
				err(type + " " + id + " : Classification of type " + classification_scheme + " ( " + class_name + " ) the slot 'codingScheme' is missing");
				continue;
			}
			String slot_name = slot_ele.getAttributeValue(MetadataSupport.slot_name_qname);
			if (slot_name == null || slot_name.equals(""))
				err(type + " " + id + " : Classification of type " + classification_scheme + " ( " + class_name + " ) the slot 'codingScheme' is missing");
		}
		if (count == 0 && required)
			err(type + " " + id + " : Classification of type " + classification_scheme + " ( " + class_name + " ) is missing");
		if (count > 1 && !multiple)
			err(type + " " + id + " : Classification of type " + classification_scheme + " ( " + class_name + " ) is duplicated");
	}

	static public void validate_CX_datatype(ErrorRecorder er, String attName, String pid, String resource) {
		String err = validate_CX_datatype(pid);
		if (err != null)
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, attName + ": " + err, "ValidatorCommon", resource);
	}

	static public String validate_CX_datatype(String pid) {
		if (pid == null)
			return "No Patient ID found";
		String[] parts = pid.split("\\^\\^\\^");
		if (parts.length != 2)
			return "Not Patient ID format: ^^^ not found:";
		String part2 = parts[1];
		part2 = part2.replaceAll("&amp;", "&");
		String[] partsa = part2.split("&");
		if (partsa.length != 3)
			return "Expected &OID&ISO after ^^^ in CX data type";
		if (partsa[0].length() != 0)
			return "Expected &OID&ISO after ^^^ in CX data type";
		if ( !partsa[2].equals("ISO"))
			return "Expected &OID&ISO after ^^^ in CX data type";
		if ( !is_oid(partsa[1], false))
			return "Expected &OID&ISO after ^^^ in CX data type: OID part does not parse as an OID";
		return null;
	}

	public void sectionHeading(String msg) {
		// TODO Auto-generated method stub

	}

	public void challenge(String msg) {
		// TODO Auto-generated method stub

	}

	public void finish() {
		// TODO Auto-generated method stub

	}

	public void showErrorInfo() {
		// TODO Auto-generated method stub

	}

	public void detail(String msg) {
		// TODO Auto-generated method stub

	}

	public void externalChallenge(String msg) {
		// TODO Auto-generated method stub

	}

	public void err(String code, String msg, String location, String resource,
			Object logMessage) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, String msg, String location, String resource,
			Object log_message) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, String msg, String resource) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, Exception e) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, String msg, String location, String resource) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, String msg, Object location, String resource) {
		// TODO Auto-generated method stub

	}

	public boolean hasErrors() {
		if (rel == null)
			return false;
		return rel.has_errors();
	}

	public void err(String code, String msg, String location, String severity,
			String resource) {
		// TODO Auto-generated method stub

	}

	public void err(Code code, String msg, String location, String severity,
			String resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warning(String code, String msg, String location,
			String resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warning(Code code, String msg, String location, String resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public ErrorRecorder buildNewErrorRecorder() {
		return this;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

	@Override
	public int getNbErrors() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void concat(ErrorRecorder er) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ValidatorErrorItem> getErrMsgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ErrorRecorder> getChildren() {
		return children;
	}

	@Override
	public int depth() {
		int depth = 1;

		int maxChildDepth = 0;
		for (ErrorRecorder er : children) {
			int childDepth = er.depth();
			if (childDepth > maxChildDepth) maxChildDepth = childDepth;
		}

		return depth + maxChildDepth;
	}

	@Override
	public void success(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warning(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub

	}

	@Override
	public void info(String dts, String name, String found, String expected, String RFC) {
		// TODO Auto-generated method stub

	}

	@Override
	public void summary(String msg, boolean success, boolean part) {
		// TODO Auto-generated method stub

	}



}
