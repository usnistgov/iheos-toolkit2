package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.datatype.XonXcnXtnFormat;
import gov.nist.toolkit.valregmetadata.model.ClassAndIdDescription;
import gov.nist.toolkit.valregmetadata.model.Slot;
import gov.nist.toolkit.valregmetadata.model.SubmissionSet;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.*;

/**
 *
 */
public class SubmissionSetValidator implements ObjectValidator {
    SubmissionSet mo;
    static public String table416 = "ITI TF-3: Table 4.1-6";


    static List<String> statusValues =
            Arrays.asList(
                    MetadataSupport.status_type_namespace + "Approved"
            );

    static List<String> definedSlots =
            Arrays.asList(
                    "intendedRecipient",
                    "submissionTime"
            );

    static List<String> requiredSlots =
            Arrays.asList(
                    "submissionTime"
            );

    static List<String> requiredSlotsMinimal =
            Arrays.asList(
                    "intendedRecipient"
            );

    static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
    static {
        classificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        classificationDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid
                );
        classificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        classificationDescription.names = new HashMap<String, String>();
        classificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
        classificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
    }

    static public ClassAndIdDescription XDMclassificationDescription = new ClassAndIdDescription();
    static {
        XDMclassificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        XDMclassificationDescription.requiredSchemes =
                Arrays.asList(
                );
        XDMclassificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        XDMclassificationDescription.names = new HashMap<String, String>();
        XDMclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
        XDMclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
    }

    static public ClassAndIdDescription MinimalclassificationDescription = new ClassAndIdDescription();
    static {
        MinimalclassificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid ,
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        MinimalclassificationDescription.requiredSchemes =
                Arrays.asList(
                );
        MinimalclassificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_author_uuid
                );
        MinimalclassificationDescription.names = new HashMap<String, String>();
        //MinimalclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid, "Content Type Code");
        MinimalclassificationDescription.names.put(MetadataSupport.XDSSubmissionSet_author_uuid, "Author");
    }

    static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
    static {
        externalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_patientid_uuid,
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );

        externalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_patientid_uuid,
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );
        externalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        externalIdentifierDescription.names = new HashMap<String, String>();
        externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
        externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
        externalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
    }

    static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
    static {
        XDMexternalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_patientid_uuid,
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );

        XDMexternalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );
        XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        XDMexternalIdentifierDescription.names = new HashMap<String, String>();
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
    }

    static public ClassAndIdDescription MinimalexternalIdentifierDescription = new ClassAndIdDescription();
    static {
        MinimalexternalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_patientid_uuid,
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );

        MinimalexternalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSSubmissionSet_uniqueid_uuid,
                        MetadataSupport.XDSSubmissionSet_sourceid_uuid
                );
        MinimalexternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        MinimalexternalIdentifierDescription.names = new HashMap<String, String>();
        //MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_patientid_uuid, "Patient ID");
        MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_uniqueid_uuid, "Unique ID");
        MinimalexternalIdentifierDescription.names.put(MetadataSupport.XDSSubmissionSet_sourceid_uuid, "Source ID");
    }


    public SubmissionSetValidator(SubmissionSet mo) {
            this.mo = mo;
    }

    public void validate(ErrorRecorder er, ValidationContext vc,
                         Set<String> knownIds) {

        if (vc.skipInternalStructure)
            return;

        if (vc.isXDR || vc.isPartOfRecipient)
            vc.isXDRLimited = mo.isMetadataLimited();

        if (vc.isXDRLimited)
            er.sectionHeading("is labeled as Limited Metadata");

        if (vc.isXDRMinimal)
            er.sectionHeading("is labeled as Minimal Metadata (Direct)");

        validateTopAtts(er, vc);

        new RegistryObjectValidator(mo, this).validateSlots(er, vc);

        if (vc.isXDM || vc.isXDRLimited)
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, XDMclassificationDescription, table416);
        else if (vc.isXDRMinimal)
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, MinimalclassificationDescription, table416);
        else
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, classificationDescription, table416);

        if (vc.isXDM || vc.isXDRLimited)
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table416);
        else if (vc.isXDRMinimal)
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, MinimalexternalIdentifierDescription, table416);
        else
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, externalIdentifierDescription, table416);

        new RegistryObjectValidator(mo, this).verifyIdsUnique(er, knownIds);
    }

    public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {
        if (vc.isXDRMinimal) {
            validateDirectSlotsCodedCorrectly(er, vc);
        } else {
            //                    name				   multi	format                                                  resource
            new RegistryObjectValidator(mo, this).validateSlot(er, 	"submissionTime", 	   false, 	new DtmFormat(er, "Slot submissionTime",            table416),  table416);
            new RegistryObjectValidator(mo, this).validateSlot(er, 	"intendedRecipient",   true, 	new XonXcnXtnFormat(er, "Slot intendedRecipient",      table416),  table416);
        }
    }

    public void validateDirectSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {

        //                    name				   multi	format                                                  resource
        new RegistryObjectValidator(mo, this).validateSlot(er, 	"submissionTime", 	   false, 	new DtmFormat(er, "Slot submissionTime",            table416),  table416);
        new RegistryObjectValidator(mo, this).validateSlot(er, 	"intendedRecipient",   true, 	new XonXcnXtnFormat(er, "Slot intendedRecipient",     table416),  table416);
    }

    public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
        // Slots always required
        if (vc.isXDRMinimal) {
            for (String slotName : requiredSlotsMinimal) {
                if (mo.getSlot(slotName) == null)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table416);
            }
        } else {
            for (String slotName : requiredSlots) {
                if (mo.getSlot(slotName) == null)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table416);
            }
        }
    }

    public void validateSlotsLegal(ErrorRecorder er)  {
        new RegistryObjectValidator(mo, this).verifySlotsUnique(er);
        for (Slot slot : mo.getSlots()) {
            if ( ! legal_slot_name(slot.getName()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + slot.getName() + " is not a legal slot name for a SubmissionSet",  this,  table416);

        }
    }

    boolean legal_slot_name(String name) {
        if (name == null) return false;
        if (name.startsWith("urn:")) return true;
        return definedSlots.contains(name);
    }

    public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
        new RegistryObjectValidator(mo, this).validateTopAtts(er, vc, table416, statusValues);
    }



}
