package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class InternalClassification extends AbstractRegistryObject {
	String classifiedObjectId;
	String classificationNode;
	
	public InternalClassification(String id, String classifiedObject, String classificationNode) {
		super(id);
		this.classifiedObjectId = classifiedObject;
		this.classificationNode = classificationNode;
	}
	
	public boolean equals(InternalClassification ic) {
		if (ic == null) 
			return false;
		if (!id.equals(ic.id))
			return false;
		if (!classifiedObjectId.equals(ic.classifiedObjectId))
			return false;
		if (!classificationNode.equals(ic.classificationNode))
			return false;
		return super.equals(ic);
	}
	
	public String getClassificationNode() {
		return classificationNode;
	}
	
	public InternalClassification(Metadata m, OMElement ro) throws XdsInternalException  {
		super(m, ro);
		classifiedObjectId = ro.getAttributeValue(MetadataSupport.classified_object_qname);
		classificationNode = ro.getAttributeValue(MetadataSupport.classificationnode_qname);
	}
	
	static public boolean isInternalClassification(OMElement ele) {
		return ele.getAttribute(MetadataSupport.classificationnode_qname) != null;
	}

	public String identifyingString() {
		return "Classification(" + getId() + ")";
	}

	public OMElement toXml() throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.classification_qnamens);
		ro.addAttribute("id", id, null);
		ro.addAttribute("classifiedObject", classifiedObjectId, null);
		ro.addAttribute("classificationNode", classificationNode, null);
 
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

	public void validateRequiredSlotsPresent(ErrorRecorder er,
			ValidationContext vc) {
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er,
			ValidationContext vc) {
	}

	public void validateSlotsLegal(ErrorRecorder er) {
	}

}
