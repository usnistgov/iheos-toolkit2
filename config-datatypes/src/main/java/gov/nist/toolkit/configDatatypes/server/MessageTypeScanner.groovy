package gov.nist.toolkit.configDatatypes.server

import gov.nist.toolkit.configDatatypes.server.datatypes.MessageTechnology
import gov.nist.toolkit.configDatatypes.server.datatypes.MessageType
import gov.nist.toolkit.configDatatypes.server.datatypes.Multipart
import gov.nist.toolkit.configDatatypes.server.datatypes.Part

/**
 * Scan message and determine whether it is a SOAP message (and what its SOAP Action is
 * or a FHIR message
 */
class MessageTypeScanner {
    String httpHeader
    String httpBody

    MessageTypeScanner(String httpHeader, String httpBody) {
        assert httpHeader
        assert httpBody
        this.httpHeader = httpHeader
        this.httpBody = httpBody.trim()
    }

    MessageType scan() {
        if (httpBody.startsWith('{'))
            return new MessageType(MessageTechnology.FHIR, null)
        if (httpBody.startsWith('<'))
            return scanEnvelope(httpBody)
        if (httpBody.startsWith('--'))
            return scanMultipart()
        return null
    }

    private static MessageType scanEnvelope(String xml) {
        def env = new XmlSlurper().parseText(xml)
        if (env.name() != 'Envelope')
            return null
        String action = env?.Header?.Action
        if (action) {
            return new MessageType(MessageTechnology.SOAP, action)
        }
        return null
    }

    private MessageType scanMultipart() {
        Multipart multipart = MultipartParser.parse(httpHeader, httpBody)
        Part startPart = multipart.getStartPart()

        return scanEnvelope(startPart.body)
    }
}
