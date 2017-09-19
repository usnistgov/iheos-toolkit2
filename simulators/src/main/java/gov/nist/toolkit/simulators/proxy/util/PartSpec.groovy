package gov.nist.toolkit.simulators.proxy.util

/**
 *
 */
class PartSpec {
    String contentType
    String content

    static final String PLAINTEXT = 'text/plain'
    static final String SOAPXOP = 'application/xop+xml; charset=UTF-8; type="application/soap+xml"'

    PartSpec(String contentType, String content) {
        this.contentType = contentType
        this.content = content
    }
}
