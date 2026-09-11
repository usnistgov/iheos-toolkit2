package gov.nist.toolkit.registrymsg.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ProvideAndRegisterModel {
    private String documentEntryFirstAuthorName = "";
    private String documentEntryClassCode = "";
    private String documentEntryCreationTime = "";
    private String documentEntryFormatCode = "";
    private String documentEntryPatientId = "";
    private String documentEntrySourcePatientId = "";
    private String documentEntryTypeCode = "";
    private String documentEntryMimeType = "";
    private String documentEntryUniqueId = "";

    public ProvideAndRegisterModel() {
    }

    public String getDocumentEntryFirstAuthorName() {
        return documentEntryFirstAuthorName;
    }

    public void setDocumentEntryFirstAuthorName(String documentEntryFirstAuthorName) {
        this.documentEntryFirstAuthorName = documentEntryFirstAuthorName;
    }

    public String getDocumentEntryClassCode() {
        return documentEntryClassCode;
    }

    public void setDocumentEntryClassCode(String documentEntryClassCode) {
        this.documentEntryClassCode = documentEntryClassCode;
    }

    public String getDocumentEntryCreationTime() {
        return documentEntryCreationTime;
    }

    public void setDocumentEntryCreationTime(String documentEntryCreationTime) {
        this.documentEntryCreationTime = documentEntryCreationTime;
    }

    public String getDocumentEntryFormatCode() {
        return documentEntryFormatCode;
    }

    public void setDocumentEntryFormatCode(String documentEntryFormatCode) {
        this.documentEntryFormatCode = documentEntryFormatCode;
    }

    public String getDocumentEntryPatientId() {
        return documentEntryPatientId;
    }

    public void setDocumentEntryPatientId(String documentEntryPatientId) {
        this.documentEntryPatientId = documentEntryPatientId;
    }

    public String getDocumentEntrySourcePatientId() {
        return documentEntrySourcePatientId;
    }

    public void setDocumentEntrySourcePatientId(String documentEntrySourcePatientId) {
        this.documentEntrySourcePatientId = documentEntrySourcePatientId;
    }

    public String getDocumentEntryTypeCode() {
        return documentEntryTypeCode;
    }

    public void setDocumentEntryTypeCode(String documentEntryTypeCode) {
        this.documentEntryTypeCode = documentEntryTypeCode;
    }

    public String getDocumentEntryMimeType() {
        return documentEntryMimeType;
    }

    public void setDocumentEntryMimeType(String documentEntryMimeType) {
        this.documentEntryMimeType = documentEntryMimeType;
    }

    public String getDocumentEntryUniqueId() {
        return documentEntryUniqueId;
    }

    public void setDocumentEntryUniqueId(String documentEntryUniqueId) {
        this.documentEntryUniqueId = documentEntryUniqueId;
    }
}
