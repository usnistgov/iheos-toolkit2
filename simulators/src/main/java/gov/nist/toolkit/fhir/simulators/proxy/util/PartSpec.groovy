package gov.nist.toolkit.fhir.simulators.proxy.util

/**
 *
 */
class PartSpec {
    String contentType
    String content
    String contentId
    boolean isStartPart = false

    static final String PLAINTEXT = 'text/plain'
    static final String SOAPXOP = 'application/xop+xml; charset=UTF-8; type="application/soap+xml"'

    PartSpec(String contentType, String content, String contentId) {
        this.contentType = contentType
        this.content = content
        this.contentId = contentId
    }
}
