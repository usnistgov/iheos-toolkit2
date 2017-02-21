package gov.nist.toolkit.errorrecording.xml

import gov.nist.toolkit.errorrecording.xml.assertions.structures.XdsDocumentType
import org.apache.commons.lang.StringUtils

/**
 * Created by diane on 8/12/2016.
 *
 * Takes a message item from the XMLErrorRecorder output and processes it to extract separate pieces of information.
 * Example: in <SectionHeading message="DocumentEntry(Document01)">, the message "DocumentEntry(Document01)" is separated into two pieces of
 * information - the type of the message, DocumentEntry, and its name, "Document01".
 */
class XMLErrorRecorderMessage {
    String id;
    String message;
    XdsDocumentType XDS_DOCUMENT_TYPE;

    public XMLErrorRecorderMessage(String msg) {
        processMessage(msg);
    }

    private XMLErrorRecorderMessage processMessage(String msg) {
        String xdsDocType = StringUtils.substringBefore(msg, "(");
        switch (xdsDocType) {
            case "DocumentEntry":
                XDS_DOCUMENT_TYPE = XdsDocumentType.DOCUMENT_ENTRY;
                break;
            case "SubmissionSet":
                XDS_DOCUMENT_TYPE = XdsDocumentType.SUBMISSION_SET;
                break;
            case "Association":
                XDS_DOCUMENT_TYPE = XdsDocumentType.ASSOCIATION;
                break;
                other:
                return null;
        }
        id = StringUtils.substringBetween(msg, "(", ")");
        return this;
    }

    //---- Getters -----

    String getId() {
        return id
    }

    String getMessage() {
        return message
    }

    String getXDS_DOCUMENT_TYPE() {
        switch (XDS_DOCUMENT_TYPE) {
            case XdsDocumentType.DOCUMENT_ENTRY:
                return "DocumentEntry";
            case XdsDocumentType.SUBMISSION_SET:
                return "SubmissionSet";
            case XdsDocumentType.ASSOCIATION:
                return "Association";
                other:
                return "UnrecognizedType";

        }
        return null;
    }

}
