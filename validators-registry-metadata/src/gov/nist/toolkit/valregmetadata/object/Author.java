package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class Author extends AbstractRegistryObject {
	String classificationScheme = null;

	public Author(String id, String person) {
		super(id);
		addSlot("authorPerson", person);
	}

	public void addInstitution(String institution) {
		Slot s = getSlot("authorInstitution");
		if (s == null)
			addSlot("authorInstitution", institution);
		else
			s.addValue(institution);
	}

	public void addRole(String roleName) {
		Slot s = getSlot("authorRole");
		if (s == null)
			addSlot("authorRole", roleName);
		else
			s.addValue(roleName);
	}

	public void addSpecialty(String specialtyName) {
		Slot s = getSlot("authorSpecialty");
		if (s == null)
			addSlot("authorSpecialty", specialtyName);
		else
			s.addValue(specialtyName);
	}

	public boolean equals(Author a) {
		try {
			if (!getClassificationScheme().equals(a.getClassificationScheme()))
				return false;
		} catch (XdsInternalException e) {
			return false;
		}
		return super.equals(a);
	}

	public String getAuthorPerson() throws MetadataException {
		try {
			String value = getSlot("authorPerson").getValue(0);
			if (value == null)
				return "";
			return value;
		} catch (Exception e) {
			return "";
		}
	}

	public List<String> getAuthorInstitutions() {
		try {
			return getSlot("authorInstitution").getValues();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public List<String> getAuthorRoles() {
		try {
			return getSlot("authorRole").getValues();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public List<String> getAuthorSpecialties() {
		try {
			return getSlot("authorSpecialty").getValues();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public Author(Metadata m, OMElement cl) throws XdsInternalException  {
		super(m, cl);

		classificationScheme = cl.getAttributeValue(MetadataSupport.classificationscheme_qname);
	}

	public void validateStructure(ErrorRecorder er, ValidationContext vc)  {
		validateId(er, vc, "entryUUID", id, null);
		OMElement parentEle = (OMElement) ro.getParent();
		String parentEleId =  ((parentEle == null) ? "null" :
			parentEle.getAttributeValue(MetadataSupport.id_qname));
		String classifiedObjectId = ro.getAttributeValue(MetadataSupport.classified_object_qname);

		if (parentEle != null && !parentEleId.equals(classifiedObjectId))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": is a child of object " + parentEleId + " but the classifiedObject value is " + 
					classifiedObjectId + ", they must match", this, "ITI TF-3: 4.1.12.2");

		try {
			if (getClassificationScheme() == null || getClassificationScheme().equals(""))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": does not have a value for the classificationScheme attribute", this, "ebRIM 3.0 section 4.3.1");
			else if (!getClassificationScheme().startsWith("urn:uuid:"))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": classificationScheme attribute value is not have urn:uuid: prefix", this, "ITI TF-3: 4.3.1");
		} catch (XdsInternalException e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
	}

	String getPerson() {
		try {
			return getAuthorPerson();
		} catch (MetadataException e) {
			return "";
		}
	}

	static public boolean isAuthorClassification(OMElement ele) {
		String classificationScheme = ele.getAttributeValue(MetadataSupport.classificationscheme_qname);
		return isAuthorClassification(classificationScheme);
	}

	static public boolean isAuthorClassification(String classificationScheme) {
		if (MetadataSupport.XDSDocumentEntry_author_uuid.equals(classificationScheme))
			return true;
		if (MetadataSupport.XDSSubmissionSet_author_uuid.equals(classificationScheme))
			return true;
		return false;
	}

	public String identifyingString() {
		return "Author (" + getPerson() + ")";
	}

	public String getClassificationScheme() throws XdsInternalException  {
		return getClassificationScheme(null);
	}

	public String getClassificationScheme(OMElement parent) throws XdsInternalException {
		if (parent != null) {
			if ("ExtrinsicObject".equals(parent.getLocalName()))
				return MetadataSupport.XDSDocumentEntry_author_uuid;
			if ("RegistryPackage".equals(parent.getLocalName()))
				return MetadataSupport.XDSSubmissionSet_author_uuid;
		}

		if (classificationScheme != null)
			return classificationScheme;
		if (owner == null)
			throw new XdsInternalException("Cannot determine proper classificationScheme for Author, no owner specified to infer classificationScheme from");
		if ("ExtrinsicObject".equals(owner.getLocalName()))
			return MetadataSupport.XDSDocumentEntry_author_uuid;
		if ("RegistryPackage".equals(owner.getLocalName()))
			return MetadataSupport.XDSSubmissionSet_author_uuid;
		throw new XdsInternalException("Cannot determine proper classificationScheme for Author, owner element type " + owner.getLocalName() + " is not understood");
	}

	public OMElement toXml(OMElement parent) throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.classification_qnamens);
		ro.addAttribute(MetadataSupport.classificationscheme_qname.getLocalPart(), getClassificationScheme(parent), null);
		ro.addAttribute(MetadataSupport.classified_object_qname.getLocalPart(), parent.getAttributeValue(MetadataSupport.id_qname), null);
		ro.addAttribute(MetadataSupport.noderepresentation_qname.getLocalPart(), "", null);

		addSlotsXml(ro);
		addNameToXml(ro);
		addDescriptionXml(ro);
		addClassificationsXml(ro);
		addExternalIdentifiersXml(ro);

		return ro;
	}

	public OMElement toXml() throws XdsInternalException   {
		return toXml(owner);
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
