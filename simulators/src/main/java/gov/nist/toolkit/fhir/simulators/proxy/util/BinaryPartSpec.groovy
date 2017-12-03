package gov.nist.toolkit.fhir.simulators.proxy.util

/**
 *
 */
class BinaryPartSpec {
    String contentType
    byte[] content
    String contentString
    String contentId
    boolean isStartPart = false

    static final String PLAINTEXT = 'text/plain'
    static final String SOAPXOP = 'application/xop+xml; charset=UTF-8; type="application/soap+xml"'

    BinaryPartSpec(String contentType, byte[] content, String contentId) {
        this.contentType = contentType
        this.content = content
        this.contentString = new String(content)
        this.contentId = contentId
    }
}
