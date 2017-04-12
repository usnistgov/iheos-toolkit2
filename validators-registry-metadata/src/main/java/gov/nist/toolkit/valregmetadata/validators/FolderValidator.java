package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.datatype.DtmFormat;
import gov.nist.toolkit.valregmetadata.model.ClassAndIdDescription;
import gov.nist.toolkit.valregmetadata.model.Folder;
import gov.nist.toolkit.valregmetadata.model.Slot;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.*;

/**
 *
 */
public class FolderValidator  implements ObjectValidator {
    Folder mo;
    static public String table417 = "ITI TF-3: Table 4.1-7";

    static List<String> statusValues =
            Arrays.asList(
                    MetadataSupport.status_type_namespace + "Approved"
            );

    static List<String> definedSlots =
            Arrays.asList(
                    "lastUpdateTime"
            );

    static List<String> requiredSlots = new ArrayList<String>();

    static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
    static {
        classificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_codeList_uuid
                );
        classificationDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_codeList_uuid
                );
        classificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_codeList_uuid
                );
        classificationDescription.names = new HashMap<String, String>();
        classificationDescription.names.put(MetadataSupport.XDSFolder_codeList_uuid, "Code List");
    }

    static public ClassAndIdDescription XDMclassificationDescription = new ClassAndIdDescription();
    static {
        XDMclassificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_codeList_uuid
                );
        XDMclassificationDescription.requiredSchemes =
                Arrays.asList(
                );
        XDMclassificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_codeList_uuid
                );
        XDMclassificationDescription.names = new HashMap<String, String>();
        XDMclassificationDescription.names.put(MetadataSupport.XDSFolder_codeList_uuid, "Code List");
    }

    static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
    static {
        externalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_patientid_uuid,
                        MetadataSupport.XDSFolder_uniqueid_uuid
                );

        externalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_patientid_uuid,
                        MetadataSupport.XDSFolder_uniqueid_uuid
                );
        externalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        externalIdentifierDescription.names = new HashMap<String, String>();
        externalIdentifierDescription.names.put(MetadataSupport.XDSFolder_patientid_uuid, "Patient ID");
        externalIdentifierDescription.names.put(MetadataSupport.XDSFolder_uniqueid_uuid, "Unique ID");
    }

    static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
    static {
        XDMexternalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_patientid_uuid,
                        MetadataSupport.XDSFolder_uniqueid_uuid
                );

        XDMexternalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSFolder_uniqueid_uuid
                );
        XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        XDMexternalIdentifierDescription.names = new HashMap<String, String>();
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSFolder_patientid_uuid, "Patient ID");
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSFolder_uniqueid_uuid, "Unique ID");
    }



    public FolderValidator(Folder mo) {
        this.mo = mo;
    }

    public void validate(ErrorRecorder er, ValidationContext vc,
                         Set<String> knownIds) {

        if (vc.skipInternalStructure)
            return;

        if (vc.isXDR || vc.isPartOfRecipient)
            vc.isXDRLimited = mo.isMetadataLimited();

        if (vc.isXDRLimited)
            er.sectionHeading("Limited Metadata");

        validateTopAtts(er, vc);

        new RegistryObjectValidator(mo, this).validateSlots(er, vc);

        if (vc.isXDM || vc.isXDRLimited)
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, XDMclassificationDescription, table417);
        else
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, classificationDescription, table417);

        if (vc.isXDM || vc.isXDRLimited)
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table417);
        else
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, externalIdentifierDescription, table417);

        new RegistryObjectValidator(mo, this).verifyIdsUnique(er, knownIds);
    }

    public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {

        //                    name				   multi	format                                                  resource
        new RegistryObjectValidator(mo, this).validateSlot(er, 	"lastUpdateTime", 	   false, 	new DtmFormat(er, "Slot lastUpdateTime",            table417),  table417);
        // waiting for CP 949 to be integrated
//        new RegistryObjectValidator(mo, this).validateSlot(er, 	"lastUpdateTime", 	   false, 	new DtmFormatWithMinSize(er, "Slot lastUpdateTime",            table417, 14),  table417);
    }

    public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
        // Slots always required
        for (String slotName : requiredSlots) {
            if (mo.getSlot(slotName) == null)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table417);
        }
    }

    public void validateSlotsLegal(ErrorRecorder er)  {
        new RegistryObjectValidator(mo, this).verifySlotsUnique(er);
        for (Slot slot : mo.getSlots()) {
            if ( ! legal_slot_name(slot.getName()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + slot.getName() + " is not a legal slot name for a SubmissionSet",  this,  table417);

        }
    }

    boolean legal_slot_name(String name) {
        if (name == null) return false;
        if (name.startsWith("urn:")) return true;
        return definedSlots.contains(name);
    }

    public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
        new RegistryObjectValidator(mo, this).validateTopAtts(er, vc, table417, statusValues);
    }



}
