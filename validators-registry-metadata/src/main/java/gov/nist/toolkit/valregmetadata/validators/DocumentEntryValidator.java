package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.datatype.*;
import gov.nist.toolkit.valregmetadata.model.ClassAndIdDescription;
import gov.nist.toolkit.valregmetadata.model.DocumentEntry;
import gov.nist.toolkit.valregmetadata.model.Slot;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;

import java.util.*;

/**
 *
 */
public class DocumentEntryValidator implements ObjectValidator {
    DocumentEntry mo;
    static public String table415 = "ITI TF-3: Table 4.2.3.2-1"; // Rev 12.1 Final Text

    static List<String> definedSlots =
            Arrays.asList(
                    "creationTime",
                    "languageCode",
                    "sourcePatientId",
                    "sourcePatientInfo",
                    "legalAuthenticator",
                    "serviceStartTime",
                    "serviceStopTime",
                    "hash",
                    "size",
                    "URI",
                    "repositoryUniqueId",
                    "documentAvailability",
                    "urn:ihe:iti:xds:2013:referenceIdList"
            );

    static public ClassAndIdDescription directClassificationDescription = new ClassAndIdDescription();
    static {
        directClassificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_classCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_confCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_eventCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_formatCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_hcftCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_psCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_typeCode_uuid,
                        MetadataSupport.XDSDocumentEntry_author_uuid
                );
        directClassificationDescription.requiredSchemes =
                Arrays.asList(
                );
        directClassificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_author_uuid,
                        MetadataSupport.XDSDocumentEntry_confCode_uuid,
                        MetadataSupport.XDSDocumentEntry_eventCode_uuid
                );
        directClassificationDescription.names = new HashMap<String, String>();
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_classCode_uuid, "Class Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_confCode_uuid, "Confidentiality Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_eventCode_uuid, "Event Codelist");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_formatCode_uuid, "Format Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_hcftCode_uuid, "Healthcare Facility Type Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_psCode_uuid, "Practice Setting Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_typeCode_uuid, "Type Code");
        directClassificationDescription.names.put(MetadataSupport.XDSDocumentEntry_author_uuid, "Author");
    }

    static public ClassAndIdDescription classificationDescription = new ClassAndIdDescription();
    static {
        classificationDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_classCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_confCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_eventCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_formatCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_hcftCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_psCode_uuid ,
                        MetadataSupport.XDSDocumentEntry_typeCode_uuid,
                        MetadataSupport.XDSDocumentEntry_author_uuid
                );
        classificationDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_classCode_uuid,
                        MetadataSupport.XDSDocumentEntry_confCode_uuid,
                        MetadataSupport.XDSDocumentEntry_formatCode_uuid,
                        MetadataSupport.XDSDocumentEntry_hcftCode_uuid,
                        MetadataSupport.XDSDocumentEntry_psCode_uuid,
                        MetadataSupport.XDSDocumentEntry_typeCode_uuid
                );
        classificationDescription.multipleSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_author_uuid,
                        MetadataSupport.XDSDocumentEntry_confCode_uuid,
                        MetadataSupport.XDSDocumentEntry_eventCode_uuid
                );
        classificationDescription.names = new HashMap<String, String>();
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_classCode_uuid, "Class Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_confCode_uuid, "Confidentiality Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_eventCode_uuid, "Event Codelist");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_formatCode_uuid, "Format Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_hcftCode_uuid, "Healthcare Facility Type Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_psCode_uuid, "Practice Setting Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_typeCode_uuid, "Type Code");
        classificationDescription.names.put(MetadataSupport.XDSDocumentEntry_author_uuid, "Author");
    }

    static public ClassAndIdDescription directExternalIdentifierDescription = new ClassAndIdDescription();
    static {
        directExternalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_patientid_uuid,
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );

        directExternalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );
        directExternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        directExternalIdentifierDescription.names = new HashMap<String, String>();
        directExternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
        directExternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
    }

    static public ClassAndIdDescription XDMexternalIdentifierDescription = new ClassAndIdDescription();
    static {
        XDMexternalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_patientid_uuid,
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );

        XDMexternalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );
        XDMexternalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        XDMexternalIdentifierDescription.names = new HashMap<String, String>();
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
        XDMexternalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
    }

    static public ClassAndIdDescription externalIdentifierDescription = new ClassAndIdDescription();
    static {
        externalIdentifierDescription.definedSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_patientid_uuid,
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );

        externalIdentifierDescription.requiredSchemes =
                Arrays.asList(
                        MetadataSupport.XDSDocumentEntry_patientid_uuid,
                        MetadataSupport.XDSDocumentEntry_uniqueid_uuid
                );
        externalIdentifierDescription.multipleSchemes = new ArrayList<String>();

        externalIdentifierDescription.names = new HashMap<String, String>();
        externalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_patientid_uuid, "Patient ID");
        externalIdentifierDescription.names.put(MetadataSupport.XDSDocumentEntry_uniqueid_uuid, "Unique ID");
    }

    static List<String> statusValues =
            Arrays.asList(
                    MetadataSupport.status_type_namespace + "Approved",
                    MetadataSupport.status_type_namespace + "Deprecated"
            );


    public DocumentEntryValidator(DocumentEntry mo) {
        this.mo = mo;
    }

    /**
     * Validate all slots present are legal for DocumentEntry
     * @param er
     */
    public void validateSlotsLegal(ErrorRecorder er)  {
        new RegistryObjectValidator(mo, this).verifySlotsUnique(er);
        for (Slot slot : mo.getSlots()) {
            if ( ! legal_slot_name(slot.getName()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + slot.getName() + " is not a legal slot name for a DocumentEntry",   this, table415);
        }
    }


    boolean legal_slot_name(String name) {
        if (name == null) return false;
        if (name.startsWith("urn:")) return true;
        return definedSlots.contains(name);
    }

    // TODO: Is it ok to leave off the TF-2b reference?
    // Was "ITI TF-3: Table 4.1-5, TF-2b: Table 3.41.4.1.2-2"

    // this takes in two circumstances:
    //	Slots always required
    //  Optional Slots required by this transaction
    public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc) {
        // Slots always required

        if (vc.isXDRMinimal) {
            for (String slotName : mo.directRequiredSlots) {
                if (mo.getSlot(slotName) == null)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table415);
            }
        }
        else if (vc.isStableOrODDE) {

        } else if (vc.isRODDE) {
            for (String slotName : mo.roddeRequiredSlots) {
                if (mo.getSlot(slotName) == null)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table415);
            }
        } else if (!(vc.isXDM || vc.isXDRLimited)) {
            for (String slotName : mo.requiredSlots) {
                if (mo.getSlot(slotName) == null)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slotName + " missing", this, table415);
            }
        }

        //  Optional Slots required by this transaction
        if (vc.hashRequired() && mo.getSlot("hash") == null)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot hash required in this context", this, table415);

        if (vc.sizeRequired() && mo.getSlot("size") == null)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot size required in this context", this, table415);

        if (vc.repositoryUniqueIdRequired() && mo.getSlot("repositoryUniqueId") == null /*|| (vc.isXDR || vc.isXDRMinimal || vc.isXDRLimited) */)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot repositoryUniqueId required in this context...\n" + vc.toString(), this, table415);

        if (vc.uriRequired() && mo.getSlot("URI") == null)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot URI required in this context", this, table415);

    }

    public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc)  {
        RegistryObjectValidator v = new RegistryObjectValidator(mo, this);
        //                    name				   multi	format                                                  resource
        v.validateSlot(er, 	"creationTime", 	   false, 	new DtmFormat(er, "Slot creationTime",      table415),  table415);
        v.validateSlot(er, 	"languageCode",		   false, 	new Rfc3066Format(er, "Slot languageCode",      table415),  table415);
        v.validateSlot(er, 	"legalAuthenticator",  false, 	new XcnFormat(er, "Slot legalAuthenticator",table415),  table415);
        v.validateSlot(er, 	"serviceStartTime",	   false, 	new DtmFormat(er, "Slot serviceStartTime",  table415),  table415);
        v.validateSlot(er, 	"serviceStopTime",	   false, 	new DtmFormat(er, "Slot serviceStopTime",   table415),  table415);
        v.validateSlot(er, 	"sourcePatientInfo",   true, 	new SourcePatientInfoFormat(er, "Slot sourcePatientInfo", table415),  table415);
        v.validateSlot(er, 	"sourcePatientId",     false, 	new CxFormat(er, "Slot sourcePatientId",   table415),  table415);
        v.validateSlot(er, 	"hash",			 	   false, 	new HashFormat(er, "Slot hash",   null), 		        table415);
        v.validateSlot(er, 	"size",				   false, 	new IntFormat(er, "Slot size",   table415),             table415);
        v.validateSlot(er, 	"URI",				   true, 	new AnyFormat(er, "Slot URI",   table415),   table415);
        v.validateSlot(er, 	"repositoryUniqueId",	false, 	new OidFormat(er, "Slot repositoryUniqueId",   table415),   table415);


        if ( mo.getSlot("URI") != null ) {
            try {
                mo.getMetadata().getURIAttribute(mo.getRo(), !vc.isXDM);
            } catch (MetadataException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Slot URI: " + e.getMessage(), this, table415);
            }
        }

        Slot docAvail = mo.getSlot("documentAvailability");
        if (docAvail != null) {
            if (docAvail.getValues().size() > 1)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Slot documentAvailability shall have a single value", this, table415);
            String val;
            try {
                val = docAvail.getValue(0);
                if (MetadataSupport.documentAvailability_offline.equals(val)   ||
                        MetadataSupport.documentAvailability_online.equals(val)) {

                } else {
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Slot documentAvailability must have one of two values: " + MetadataSupport.documentAvailability_offline + " or " +
                            MetadataSupport.documentAvailability_online + ". Found instead " + val, this, table415
                    );
                }
            } catch (Exception e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
            }
        }
    }

    public void validate(ErrorRecorder er, ValidationContext vc, Set<String> knownIds) {

        if (vc.skipInternalStructure)
            return;

        if (vc.isXDR || vc.isPartOfRecipient)
            vc.isXDRLimited = mo.isMetadataLimited();

        if (vc.isXDRLimited)
            er.sectionHeading("is labeled as Limited Metadata");

        // A registry response can contain both stable and on-demand model types. Use the model type to prepare the validation context at runtime.
        if (vc.isResponse && vc.isStableOrODDE) {
            vc.isRODDE = MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid.equals(mo.getObjectType());
        }

        validateTopAtts(er, vc);

        new RegistryObjectValidator(mo, this).validateSlots(er, vc);

        if (vc.isXDRMinimal)
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, directClassificationDescription, table415);
        else
            new RegistryObjectValidator(mo, this).validateClassifications(er, vc, classificationDescription, table415);

        if (vc.isXDRMinimal)
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, directExternalIdentifierDescription, table415);
        else if (vc.isXDM || vc.isXDRLimited)
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, XDMexternalIdentifierDescription, table415);
        else
            new RegistryObjectValidator(mo, this).validateExternalIdentifiers(er, vc, externalIdentifierDescription, table415);

        new RegistryObjectValidator(mo, this).verifyIdsUnique(er, knownIds);

        // Restore the dynamic validation flag
        if (vc.isResponse && vc.isStableOrODDE) {
            vc.isRODDE = false;
        }
    }





    public void validateTopAtts(ErrorRecorder er, ValidationContext vc) {
        if(vc.isRODDE) {
            if (!MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid.equals(mo.getObjectType()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": On-Demand objectType must be " + MetadataSupport.XDSRODDEDocumentEntry_objectType_uuid + " (found " + mo.getObjectType() + ")", this, table415);

        } else if (!MetadataSupport.XDSDocumentEntry_objectType_uuid.equals(mo.getObjectType()))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": objectType must be " + MetadataSupport.XDSDocumentEntry_objectType_uuid + " (found " + mo.getObjectType() + ")", this, table415);

        if (mo.getMimeType() == null || mo.getMimeType().equals(""))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": mimeType attribute missing or empty", this, table415);

        new RegistryObjectValidator(mo, this).validateTopAtts(er, vc, table415, statusValues);

    }



}
