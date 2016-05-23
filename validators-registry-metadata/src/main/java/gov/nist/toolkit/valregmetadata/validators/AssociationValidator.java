package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.model.Association;
import gov.nist.toolkit.valregmetadata.model.ClassAndIdDescription;
import gov.nist.toolkit.valregmetadata.model.Classification;
import gov.nist.toolkit.valregmetadata.model.Slot;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.*;

/**
 *
 */
public class AssociationValidator implements ObjectValidator {
    Association mo;
    static public String table415 = "ITI TF-3: Table 4.2.3.2-1"; // Rev 12.1 Final Text

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


    public AssociationValidator(Association mo) {
        this.mo = mo;
    }

    public void validate(ErrorRecorder er, ValidationContext vc,
                         Set<String> knownIds) {
        if (vc.skipInternalStructure)
            return;

        validateTopAtts(er, vc);

        new RegistryObjectValidator(mo, this).validateSlots(er, vc);

        validateClassifications(er, vc);

        new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, externalIdentifierDescription, "ITI TF-3 4.1.3");

        new RegistryObjectValidator(mo, this).verifyIdsUnique(er, knownIds);

        verifyNotReferenceSelf(er);
    }

    void verifyNotReferenceSelf(ErrorRecorder er) {
        if (mo.getSource().equals(mo.getId()))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + " sourceObject attribute references self", this, "???");
        if (mo.getTarget().equals(mo.getId()))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + " targetObject attribute references self", this, "???");
    }

    static List<String> assocs_with_documentation =
            Arrays.asList(
                    MetadataSupport.assoctype_rplc,
                    MetadataSupport.assoctype_xfrm,
                    MetadataSupport.assoctype_apnd,
                    MetadataSupport.assoctype_xfrm_rplc
            );

    public void validateClassifications(ErrorRecorder er, ValidationContext vc) {

        er.challenge("Classifications present are legal");

        List<Classification> c = mo.getClassificationsByClassificationScheme(MetadataSupport.XDSAssociationDocumentation_uuid);
        if (c.size() == 0)
            ;
        else if (c.size() > 1)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() +
                    ": may contain only a single documentation classification (classificationScheme=" +
                    MetadataSupport.XDSAssociationDocumentation_uuid + ")", this, "ITI TF-3 4.1.6.1");
        else {
            if (!assocs_with_documentation.contains(mo.getType()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() +
                        ": documentation classification (classificationScheme=" +
                        MetadataSupport.XDSAssociationDocumentation_uuid +
                        ") may only be present on the following association types: " +
                        assocs_with_documentation, this, "ITI TF-3 4.1.6.1");
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

    public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
        new RegistryObjectValidator(mo, this).validateId(er, vc, "entryUUID", mo.getId(), null);

        new RegistryObjectValidator(mo, this).validateId(er, vc, "sourceObject", mo.getSource(), null);
        new RegistryObjectValidator(mo, this).validateId(er, vc, "targetObject", mo.getTarget(), null);


        boolean muReq = vc.isMU && vc.isRequest;
        boolean basicType = assocTypes.contains(mo.getType());
        boolean muType = assocTypesMU.contains(mo.getType());

        if (muReq) {
            if (basicType == false && muType == false)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": associationType " + mo.getType() + " unknown. Known assocationTypes are " + assocTypes + " and " + assocTypesMU, this, "ITI TF-3 Table 4.1-2.1");
        }

        else if (vc.isResponse) {
            if (!assocTypes.contains(mo.getType()) && !assocTypesMU.contains(mo.getType()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": associationType " + mo.getType() + " unknown. Known assocationTypes are " + assocTypes + " and " + assocTypesMU, this, "ITI TF-3 Table 4.1-2.1");
        }

        else if (!assocTypes.contains(mo.getType()))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": associationType " + mo.getType() + " unknown. Known assocationTypes are " + assocTypes, this, "ITI TF-3 Table 4.1-2.1");


    }

    public void validateRequiredSlotsPresent(ErrorRecorder er,
                                             ValidationContext vc) {
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

    public void validateSlotsCodedCorrectly(ErrorRecorder er,
                                            ValidationContext vc) {
        Slot s = mo.getSlot(MetadataSupport.assoc_slot_submission_set_status);
        if (s == null)
            return;
        if (s.getValues().size() == 1) {
            String value = s.getValues().get(0);
            if ("Original".equals(value) || "Reference".equals(value))
                ;
            else
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": SubmissionSetStatus Slot can only take value Original or Reference", this, "ITI TF-3: 4.1.4.1");
        } else {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": SubmissionSetStatus Slot must have only single value", this, "ITI TF-3: 4.1.4.1");
        }
    }

    public void validateSlotsLegal(ErrorRecorder er) {
        // work done by validateRequiredSlotsPresent
    }

}
