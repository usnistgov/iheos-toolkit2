package gov.nist.toolkit.valregmetadata.model;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.validators.RegistryObjectValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.axiom.om.OMElement;

public class Association extends AbstractRegistryObject implements TopLevelObject {
	String source = "";
	String target = "";
	String type = "";
	ValidationContext vc;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ValidationContext getVc() {
		return vc;
	}

	public void setVc(ValidationContext vc) {
		this.vc = vc;
	}

	public Association(Metadata m, OMElement ro, ValidationContext vc) throws XdsInternalException  {
		super(m, ro);
		source = ro.getAttributeValue(MetadataSupport.source_object_qname);
		target = ro.getAttributeValue(MetadataSupport.target_object_qname);
		type = ro.getAttributeValue(MetadataSupport.association_type_qname);
		normalize();
		this.vc = vc;
	}

	public Association(String id, String type, String source, String target) {
		super(id);
		this.type = type;
		this.source = source;
		this.target = target;
		normalize();
	}

	void normalize() {
		if (source == null) source="";
		if (target == null) target = "";
		if (type == null) type = "";
	}

	public String identifyingString() {
		return "Association(" + getId() + ", " + type + ")";
	}

	public boolean equals(Association a) {
		if (!id.equals(a.id))
			return false;
		if (!source.equals(a.source))
			return false;
		if (!target.equals(a.target))
			return false;
		if (!type.equals(a.type))
			return false;
		return super.equals(a);

	}

	public OMElement toXml() throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.association_qnamens);
		ro.addAttribute("id", id, null);
		ro.addAttribute("sourceObject", source, null);
		ro.addAttribute("targetObject", target, null);
		ro.addAttribute("associationType", type, null);
		if (status != null)
			ro.addAttribute("status", status, null);
		if (home != null)
			ro.addAttribute("home", home, null);

		addSlotsXml(ro);
		addNameToXml(ro);
		addDescriptionXml(ro);
		addClassificationsXml(ro);
		addAuthorsXml(ro);
		addExternalIdentifiersXml(ro);

		return ro;
	}

}
