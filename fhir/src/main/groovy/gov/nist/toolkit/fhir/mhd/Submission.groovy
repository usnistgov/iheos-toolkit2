package gov.nist.toolkit.fhir.mhd

/**
 *
 */
class Submission {

    class Attachment {
        String contentType
        byte[] content
    }

    String metadata
    List<Attachment> attachments = []

}
