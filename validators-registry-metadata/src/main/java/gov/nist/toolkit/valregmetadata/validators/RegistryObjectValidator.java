package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmetadata.datatype.CxFormat;
import gov.nist.toolkit.valregmetadata.datatype.FormatValidator;
import gov.nist.toolkit.valregmetadata.datatype.OidFormat;
import gov.nist.toolkit.valregmetadata.datatype.UuidFormat;
import gov.nist.toolkit.valregmetadata.model.*;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import org.apache.axiom.om.OMElement;

import java.net.URISyntaxException;
import java.util.*;
import java.net.URI;

/**
 *
 */
public class RegistryObjectValidator {
    AbstractRegistryObject mo;
    ObjectValidator ov;

    public RegistryObjectValidator(AbstractRegistryObject mo, ObjectValidator ov) {
        this.mo = mo;
        this.ov = ov;
    }

    public void validateRequiredClassificationsPresent(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        if (!(vc.isXDM || vc.isXDRLimited)) {
            for (String cScheme : desc.getRequiredSchemes()) {
                List<Classification> cs = mo.getClassificationsByClassificationScheme(cScheme);
                if (cs.size() == 0)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + mo.classificationDescription(desc, cScheme) + " is required but missing", this, resource);
            }
        }
    }

    public void validateClassificationsLegal(ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        List<String> cSchemes = new ArrayList<String>();

        for (Classification c : mo.getClassifications()) {
            String cScheme = c.getClassificationScheme();
            if (cScheme == null || cScheme.equals("") || !desc.getDefinedSchemes().contains(cScheme)) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + c.identifyingString() + " has an unknown classificationScheme attribute value: " + cScheme, this, resource);
            } else {
                cSchemes.add(cScheme);
            }
        }

        Set<String> cSchemeSet = new HashSet<String>();
        cSchemeSet.addAll(cSchemes);
        for (String cScheme : cSchemeSet) {
            if (count(cSchemes, cScheme) > 1 && !desc.getMultipleSchemes().contains(cScheme))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + mo.classificationDescription(desc, cScheme) + " is specified multiple times, only one allowed", this, resource);
        }
    }

    public void validateClassificationsCodedCorrectly(ErrorRecorder er, ValidationContext vc) {
        for (Classification c : mo.getClassifications())
            new ClassificationValidator(c).validateStructure(er, vc);

        for (Author a : mo.getAuthors())
            new AuthorValidator(a).validateStructure(er, vc);
    }

    public void validateClassifications(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource)  {
        er.challenge("Validating Classifications present are legal");
        validateClassificationsLegal(er, desc, resource);
        er.challenge("Validating Required Classifications present");
        validateRequiredClassificationsPresent(er, vc, desc, resource);
        er.challenge("Validating Classifications coded correctly");
        validateClassificationsCodedCorrectly(er, vc);
    }

    public void validateExternalIdentifiers(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        er.challenge("Validating ExternalIdentifiers present are legal");
        validateExternalIdentifiersLegal(er, desc, resource);
        er.challenge("Validating Required ExternalIdentifiers present");
        validateRequiredExternalIdentifiersPresent(er, vc, desc, resource);
        er.challenge("Validating ExternalIdentifiers coded correctly");
        validateExternalIdentifiersCodedCorrectly(er, vc, desc, resource);
    }

    public void validateExternalIdentifiersCodedCorrectly(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        for (ExternalIdentifier ei : mo.getExternalIdentifiers()) {
            new ExternalIdentifierValidator(ei).validateStructure(er, vc);
            if (MetadataSupport.XDSDocumentEntry_uniqueid_uuid.equals(ei.getIdentificationScheme())) {
                // Moved the code that was previously here to the method validateDocumentEntryUniqueId
                // This was done to incorporate changes triggered by ITI CP 808 that allows
                // different formats for DocumnentEntry.uniqueId
                validateDocumentEntryUniqueId(er, vc, ei, desc, resource);
            } else if (MetadataSupport.XDSDocumentEntry_patientid_uuid.equals(ei.getIdentificationScheme())){
                new CxFormat(er, mo.identifyingString() + ": " + ei.identifyingString(), "ITI TF-3: Table 4.1.7")
                        .validate(ei.getValue());
            } else if (MetadataSupport.XDSSubmissionSet_uniqueid_uuid.equals(ei.getIdentificationScheme())) {
                new OidFormat(er, mo.identifyingString() + ": " + ei.identifyingString(), externalIdentifierDescription(desc, ei.getIdentificationScheme()))
                        .validate(ei.getValue());
            }
        }
    }

    public void validateRequiredExternalIdentifiersPresent(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource)  {
        for (String idScheme : desc.getRequiredSchemes()) {
            List<ExternalIdentifier> eis = mo.getExternalIdentifiers(idScheme);
            if (eis.size() == 0)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + externalIdentifierDescription(desc, idScheme) + " is required but missing", this, resource);
            if (eis.size() > 1)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + externalIdentifierDescription(desc, idScheme) + " is specified multiple times, only one allowed", this, resource);
        }
    }

    public void validateExternalIdentifiersLegal(ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        for (ExternalIdentifier ei : mo.getExternalIdentifiers()) {
            String idScheme = ei.getIdentificationScheme();
            if (idScheme == null || idScheme.equals("") || !desc.getDefinedSchemes().contains(idScheme))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " has an unknown identificationScheme attribute value: " + idScheme, this, resource);
        }
    }

    public void validateSlot(ErrorRecorder er, String slotName, boolean multivalue, FormatValidator validator, String resource) {
        Slot slot = mo.getSlot(slotName);
        if (slot == null) {
            return;
        }

        new SlotValidator(slot).validate(er, multivalue, validator, resource);
    }

    public boolean verifySlotsUnique(ErrorRecorder er) {
        boolean ok = true;
        List<String> names = new ArrayList<String>();
        for (Slot slot : mo.getSlots()) {
            if (names.contains(slot.getName()))
                if (er != null) {
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": Slot " + slot.getName() + " is multiply defined", this, "ebRIM 3.0 section 2.8.2");
                    ok = false;
                }
                else
                    names.add(slot.getName());
        }
        return ok;
    }

    public void validateHome(ErrorRecorder er, String resource) {
        if (mo.getHome() == null)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": homeCommunityId attribute must be present", this, resource);
        else {
            if (mo.getHome().length() > 64)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": homeCommunityId is limited to 64 characters, found " + mo.getHome().length(), this, resource);

            String[] parts = mo.getHome().split(":");
            if (parts.length < 3 || !parts[0].equals("urn") || !parts[1].equals("oid"))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": homeCommunityId must begin with urn:oid: prefix, found [" + mo.getHome() + "]", this, resource);
            new OidFormat(er, mo.identifyingString() + " homeCommunityId", resource).validate(parts[parts.length-1]);
        }
    }

    public void validateTopAtts(ErrorRecorder er, ValidationContext vc, String tableRef, List<String> statusValues) {
        validateId(er, vc, "entryUUID", mo.getId(), null);

        if (vc.isSQ && vc.isResponse) {
            if (mo.getStatus() == null)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": availabilityStatus attribute (status attribute in XML) must be present", this, tableRef);
            else {
                if (!statusValues.contains(mo.getStatus()))
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": availabilityStatus attribute must take on one of these values: " + statusValues + ", found " + mo.getStatus(), this, "ITI TF-2a: 3.18.4.1.2.3.6");
            }

            validateId(er, vc, "lid", mo.getLid(), null);

            List<OMElement> versionInfos = XmlUtil.childrenWithLocalName(mo.getRo(), "VersionInfo");
            if (versionInfos.size() == 0) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": VersionInfo attribute missing", this, "ebRIM Section 2.5.1");
            }
        }

        if (vc.isSQ && vc.isXC && vc.isResponse) {
            validateHome(er, tableRef);
        }
    }

    public void validateId(ErrorRecorder er, ValidationContext vc, String attName, String attValue, String resource) {
        String defaultResource = "ITI TF-3: 4.1.12.3";
        if (attValue == null || attValue.equals("")) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + attName + " attribute empty or missing", this, (resource!=null) ? resource : defaultResource);
        } else {
            if (vc.isSQ && vc.isResponse) {
                new UuidFormat(er, mo.identifyingString() + " " + attName + " attribute must be a UUID (all lower case)", (resource!=null) ? resource : defaultResource).validate(mo.getId());
            } else if(mo.getId().startsWith("urn:uuid:")) {
                new UuidFormat(er, mo.identifyingString() + " " + attName + " attribute", (resource!=null) ? resource : defaultResource).validate(mo.getId());
            }
        }

        for (Classification c : mo.getClassifications())
            new RegistryObjectValidator(c, ov).validateId(er, vc, "entryUUID", c.getId(), resource);

        for (Author a : mo.getAuthors())
            new RegistryObjectValidator(a, ov).validateId(er, vc, "entryUUID", a.getId(), resource);

        for (ExternalIdentifier ei : mo.getExternalIdentifiers())
            new RegistryObjectValidator(ei, ov).validateId(er, vc, "entryUUID", ei.getId(), resource);

    }

    public void validateSlots(ErrorRecorder er, ValidationContext vc) {
        er.challenge("Validating that Slots present are legal");
        ov.validateSlotsLegal(er);
        er.challenge("Validating required Slots present");
        ov.validateRequiredSlotsPresent(er, vc);
        er.challenge("Validating Slots are coded correctly");
        ov.validateSlotsCodedCorrectly(er, vc);
    }

    public void verifyIdsUnique(ErrorRecorder er, Set<String> knownIds) {
        if (mo.getId() != null) {
            if (knownIds.contains(mo.getId()))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": entryUUID " + mo.getId() + "  identifies multiple objects", this, "ITI TF-3: 4.1.12.3 and ebRS 5.1.2");
            knownIds.add(mo.getId());
        }

        for (Classification c : mo.getClassifications())
            new RegistryObjectValidator(c, ov).verifyIdsUnique(er, knownIds);

        for (Author a : mo.getAuthors())
            new RegistryObjectValidator(a, ov).verifyIdsUnique(er, knownIds);

        for (ExternalIdentifier ei : mo.getExternalIdentifiers())
            new RegistryObjectValidator(ei, ov).verifyIdsUnique(er, knownIds);
    }


    private String externalIdentifierDescription(ClassAndIdDescription desc, String eiScheme) {
        return "ExternalIdentifier(" + eiScheme + ")(" + desc.getNames().get(eiScheme) + ")";
    }

    private int count(List<String> strings, String target) {
        int i=0;

        for (String s : strings)
            if (s.equals(target))
                i++;

        return i;
    }

    /* See IHE ITI CP-808.
       DocumentEntry.uniqueId is now classified as an Identifier with this definition
           A globally unique identifier. This may be one of OID, URI, UUID (as defined in this
           table) or any other format that employs effective mechanisms to ensure global uniqueness.
        The vc.metadataValidationDocumentIDCodes (String) is a simulator property that defines
        how we should validate the DocumentEntry.uniqueId format
            ""       This is the legacy mechanism. Validate as an OID
            CP-808   Declare valid if encoded per any of OID, URI, UUID
            a;b;c    The values for a,b,c are taken from "OID", "URI", "UUID".
                     The user may specify any combination in any order.
                     For example:  URI;OID
                     The identifier is considered valid if it conforms to one of the
                     encodings in the list. The delimiter is ; and not ,
     */
    private void validateDocumentEntryUniqueId(ErrorRecorder er, ValidationContext vc, ExternalIdentifier ei, ClassAndIdDescription desc, String resource) {
        String resourceForDocumentEntryUniqueId = "ITI TF-3: 4.2.3.2.26";
        List<String> validationCodes = new ArrayList<>();
        Set<String> recognizedValidationCodes = new HashSet<>();
        recognizedValidationCodes.add("OID");
        recognizedValidationCodes.add("URI");
        recognizedValidationCodes.add("UUID");

        // Define a local variable and seed with legacy method if validation context does not provide a different value.
        // The legacy method was originally defined for this identifier. ITI CP-808 expanded the definition, and that
        // expanded definition is enabled by updating simulator configuration.
        String validationMethods = ("".equals(vc.metadataValidationDocumentIDCodes)) ? "OID" : vc.metadataValidationDocumentIDCodes;
        if ("NONE".equals(validationMethods)) {
            // Special case where no validation is requested
        } else if ("CP-808".equals(validationMethods)) {
            // Shortcut for CP-808
            validationCodes.add("OID");
            validationCodes.add("URI");
            validationCodes.add("UUID");
        } else {
            // Validation methods are codes separated by ;
            String tokens[] = validationMethods.split(";");
            for (int i = 0; i < tokens.length; i++) {
                validationCodes.add(tokens[i]);
            }
        }

        // Ensure that we recognize all validation codes
        // If we don't, then there is a configuration error with the simulator
        Iterator<String> it = validationCodes.iterator();
        while (it.hasNext()) {
            String encodingMethod = it.next();
            if (! recognizedValidationCodes.contains(encodingMethod)) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " Unrecognized configuration code for Toolkit simulator: " + encodingMethod, this, resourceForDocumentEntryUniqueId);
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " Review/repair simulator configuration: " + vc.metadataValidationDocumentIDCodes, this, resourceForDocumentEntryUniqueId);
            }
        }

        boolean isValidEncoding = false;
        it = validationCodes.iterator();
        while (it.hasNext() && !isValidEncoding) {
            // Make a local recorder. We don't want to log errors in the full recorder
            // until we are sure no validations complete successfully
            ErrorRecorder localRecorder = new TextErrorRecorder();

            String encodingMethod = it.next();

            if ("OID".equals(encodingMethod)) {
                isValidEncoding = isValidOID(ei, localRecorder, desc, resourceForDocumentEntryUniqueId);
                if (isValidEncoding) {
                    // Run a second time with the actual Error Recorder
                    // This will pick up any warnings and report back
                    // Loop will exit because 'isValidEncoding' is now true
                    isValidOID(ei, er, desc, resourceForDocumentEntryUniqueId);
                }
            } else if ("URI".equals(encodingMethod)) {
                isValidEncoding = isValidURI(ei, localRecorder, desc, resourceForDocumentEntryUniqueId);
                if (isValidEncoding) {
                    // Run a second time with the actual Error Recorder
                    // This will pick up any warnings and report back
                    // Loop will exit because 'isValidEncoding' is now true
                    isValidURI(ei, er, desc, resourceForDocumentEntryUniqueId);
                }
            } else if ("UUID".equals(encodingMethod)) {
                isValidEncoding = isValidUUID(ei, localRecorder, desc, resourceForDocumentEntryUniqueId);
                    if (isValidEncoding) {
                        // Run a second time with the actual Error Recorder
                        // This will pick up any warnings and report back
                        // Loop will exit because 'isValidEncoding' is now true
                        isValidUUID(ei, er, desc, resourceForDocumentEntryUniqueId);
                    }
            } else {
                // We flagged this error above
            }
        }

        // If we did not find a valid encoding, run all validators again.
        // In this loop, we use the actual error recorder so we can pass the error results back
        if (! isValidEncoding) {
            it = validationCodes.iterator();
            while (it.hasNext() && !isValidEncoding) {
                String encodingMethod = it.next();

                if ("OID".equals(encodingMethod)) {
                    isValidEncoding = isValidOID(ei, er, desc, resourceForDocumentEntryUniqueId);
                } else if ("URI".equals(encodingMethod)) {
                    isValidEncoding = isValidURI(ei, er, desc, resourceForDocumentEntryUniqueId);
                } else if ("UUID".equals(encodingMethod)) {
                    isValidEncoding = isValidUUID(ei, er, desc, resourceForDocumentEntryUniqueId);
                } else {
                    // We flagged this error above
                }
            }
        }
        if (! isValidEncoding) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " Validation of DocumentEntry.uniqueId failed for all configured validation methods: " + validationMethods, this, resourceForDocumentEntryUniqueId);
        }
    }

    private boolean isValidOID(ExternalIdentifier ei, ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        String[] parts = ei.getValue().split("\\^");
        OidFormat oidFormat = new OidFormat(er, mo.identifyingString() + ": " + ei.identifyingString(), externalIdentifierDescription(desc, ei.getIdentificationScheme()));
        oidFormat.validate(parts[0]);

        if (parts[0].length() > 64)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " OID part of DocumentEntry uniqueID is limited to 64 digits", this, resource);
        if (parts.length > 1 && parts[1].length() > 16) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " extension part of DocumentEntry uniqueID is limited to 16 characters", this, resource);
        }

        return ! er.hasErrors();
    }
    private boolean isValidURI(ExternalIdentifier ei, ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        String candidate = ei.getValue();
        URI uri = null;
        try {
            uri = new URI(candidate);
        } catch (URISyntaxException e) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": " + ei.identifyingString() + " invalid syntax for URI per java.net.URI class: " + candidate, this, resource);
        }
        return ! er.hasErrors();
    }
    private boolean isValidUUID(ExternalIdentifier ei, ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        UuidFormat uuidFormat = new UuidFormat(er, mo.identifyingString(), resource);
        uuidFormat.validate(ei.getValue());
        return ! er.hasErrors();
    }
}
