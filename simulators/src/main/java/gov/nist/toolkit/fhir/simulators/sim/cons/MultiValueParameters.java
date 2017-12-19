package gov.nist.toolkit.fhir.simulators.sim.cons;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MultiValueParameters {

    static List<String> names = new ArrayList<>();
    static {
        names.add("$XDSDocumentEntryClassCode");
        names.add("$XDSDocumentEntryTypeCode");
        names.add("$XDSDocumentEntryPracticeSettingCode");
        names.add("$XDSDocumentEntryHealthcareFacilityTypeCode");
        names.add("$XDSDocumentEntryEventCodeList");
        names.add("$XDSDocumentEntryConfidentialityCode");
        names.add("$XDSDocumentEntryAuthorPerson");
        names.add("$XDSDocumentEntryFormatCode");
        names.add("$XDSDocumentEntryStatus");
        names.add("$XDSDocumentEntryType");
        names.add("$XDSSubmissionSetSourceId");
        names.add("$XDSSubmissionSetContentType");
        names.add("$XDSSubmissionSetStatus");
        names.add("$XDSFolderCodeList");
        names.add("$XDSFolderStatus");
        names.add("$XDSDocumentEntryEntryUUID");
        names.add("$XDSDocumentEntryUniqueId");
        names.add("$XDSFolderEntryUUID");
        names.add("$XDSFolderUniqueId");
        names.add("$uuid");
        names.add("$AssociationTypes");
        names.add("$XDSDocumentEntryReferenceIdList");
    }

    static public boolean supportsMultipleValues(String parameterName) {
        return names.contains(parameterName);
    }
}
