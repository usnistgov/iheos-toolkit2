package gov.nist.toolkit.simProxy.server.util

import org.apache.commons.lang.text.StrSubstitutor

/**
 * Build either SIMPLE SOAP or MTOM message from components
 */
class SoapBuilder {

    /**
     * wrap bodyXml in simple soap frame
     * @param bodyXml
     * @return [ http header,  htto body  ]
     */
    List<String> simpleSoap(String service, String host, String port, String action, String bodyXml) {
        def map = [theService: service, theHost: host, thePort: port, theAction: action]
        return [buildHeader('/templates/simple_header.txt', map), bodyXml]
    }

    /**
     *
     * @param service
     * @param host
     * @param port
     * @param action
     * @param parts - first is start part
     * @return
     */
    List<String> mtomSoap(String service, String host, String port, String action, List<PartSpec> parts) {
        def map = [theService: service, theHost: host, thePort: port, theAction: action]
        return [
                buildHeader('/templates/mtom_header.txt', map),  // contentId pre-set to "first part" id
                buildMtomBody(parts)
        ]
    }

    private String buildMtomBody(List<PartSpec> parts) {
        def contentIdBase = '.694859fac46e21a68c012f8f4fe208a370fc32b6e07ae79f'
        StringBuilder buf = new StringBuilder()

        def partHeaderTemplate = getClass().getResource('/templates/part_header.txt').text
        int index = 1
        parts.each {
            def map = [theContentType: it.contentType, theContentId:"${index}${contentIdBase}"]
            buf.append(new StrSubstitutor(map).replace(partHeaderTemplate))  // header
            buf.append(it.content)   // body
            index++
        }
        buf.append(getClass().getResource('/templates/mtom_close.txt').text)

        return buf.toString()
    }

    private String buildHeader(def hdrFile, def map) {
        def hdrTemplate = getClass().getResource(hdrFile).text
        return new StrSubstitutor(map).replace(hdrTemplate)
    }
}
