package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
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
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	static List<String> assocTypes =
			Arrays.asList(
					MetadataSupport.assoctype_has_member,
					MetadataSupport.assoctype_rplc,
					MetadataSupport.assoctype_xfrm,
					MetadataSupport.assoctype_apnd,
					MetadataSupport.assoctype_xfrm_rplc,
					MetadataSupport.assoctype_signs,
					MetadataSupport.assoctype_isSnapshotOf
			);

	static List<String> assocTypesMU =
			Arrays.asList(
					MetadataSupport.assoctype_update_availabilityStatus,
					MetadataSupport.assoctype_submitAssociation
			);

	static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
	static {
		externalIdentifierDescription.definedSchemes = new ArrayList<String>();

		externalIdentifierDescription.requiredSchemes = new ArrayList<String>();
		externalIdentifierDescription.multipleSchemes = new ArrayList<String>();

		externalIdentifierDescription.names = new HashMap<String, String>();
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

	public void validate(IErrorRecorder er, ValidationContext vc,
                         Set<String> knownIds) {
		if (vc.skipInternalStructure)
			return;

		validateTopAtts(er, vc);

		validateSlots(er, vc);

		validateClassifications(er, vc);

		validateExternalIdentifiers(er, vc, externalIdentifierDescription, "ITI TF-3 4.1.3");

		verifyIdsUnique(er, knownIds);

		verifyNotReferenceSelf(er);
	}

	void verifyNotReferenceSelf(IErrorRecorder er) {
		if (source.equals(id)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA031");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), "");
		}
		if (target.equals(id)) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA032");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), "");
		}
	}

	static List<String> assocs_with_documentation =
			Arrays.asList(
					MetadataSupport.assoctype_rplc,
					MetadataSupport.assoctype_xfrm,
					MetadataSupport.assoctype_apnd,
					MetadataSupport.assoctype_xfrm_rplc
			);

	public void validateClassifications(IErrorRecorder er, ValidationContext vc) {

		er.challenge("Classifications present are legal");

		List<Classification> c = getClassificationsByClassificationScheme(MetadataSupport.XDSAssociationDocumentation_uuid);
		if (c.size() == 0)
			;
		else if (c.size() > 1) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA033");
			String detail = "ClassificationScheme should be: '" + MetadataSupport.XDSAssociationDocumentation_uuid + "'";
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), detail);
		}
		else {
			if (!assocs_with_documentation.contains(type)) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA034");
				String detail = "Documentation classification (classificationScheme=" + MetadataSupport.XDSAssociationDocumentation_uuid +
						") may only be present on the following association types: " + assocs_with_documentation;
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), detail);
			}
		}
		er.challenge("Required Classifications present");
		er.challenge("Classifications coded correctly");
	}

	List<String> relationship_assocs =
			Arrays.asList(
					MetadataSupport.assoctype_rplc,
					MetadataSupport.assoctype_apnd,
					MetadataSupport.assoctype_xfrm,
					MetadataSupport.assoctype_xfrm_rplc
			);

	public void validateTopAtts(IErrorRecorder er, ValidationContext vc) {
		validateId(er, vc, "entryUUID", id, null);

		validateId(er, vc, "sourceObject", source, null);
		validateId(er, vc, "targetObject", target, null);


		boolean muReq = vc.isMU && vc.isRequest;
		boolean basicType = assocTypes.contains(type);
		boolean muType = assocTypesMU.contains(type);
		Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA035");

		if (muReq) {
			if (basicType == false && muType == false) {
				String detail = "AssociationType '" + type + "' unknown. Known associationTypes are " + assocTypes + " and " + assocTypesMU;
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), detail);
			}
		}

		else if (vc.isResponse) {
			if (!assocTypes.contains(type) && !assocTypesMU.contains(type)) {
				String detail = "AssociationType " + type + " unknown. Known associationTypes are " + assocTypes + " and " + assocTypesMU;
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), detail);
			}
		}

		else if (!assocTypes.contains(type)) {
			String detail = "AssociationType " + type + " unknown. Known associationTypes are " + assocTypes;
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), detail);
		}
	}

	public void validateRequiredSlotsPresent(IErrorRecorder er, ValidationContext vc) {
//		Metadata m = getMetadata();
//		if (type.equals(MetadataSupport.assoctype_has_member) &&
//				m.isSubmissionSet(source) &&
//				m.isDocument(target)) {
//			if (getSlot(MetadataSupport.assoc_slot_submission_set_status) == null)
//				er.err(identifyingString() + ": SubmissionSet to DocumentEntry HasMember association must have a SubmissionSetStatus Slot", "ITI TF-3: 4.1.4.1");
//		} else {
//
//		}

	}

	public void validateSlotsCodedCorrectly(IErrorRecorder er, ValidationContext vc) {
		Slot s = getSlot(MetadataSupport.assoc_slot_submission_set_status);
		if (s == null)
			return;
		if (s.getValues().size() == 1) {
			String value = s.getValues().get(0);
			if ("Original".equals(value) || "Reference".equals(value));
			else {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA036");
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), "");
			}
		} else {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA037");
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assertion, this, identifyingString(), "");
		}
	}

	public void validateSlotsLegal(IErrorRecorder er) {
		// work done by validateRequiredSlotsPresent
	}

}
