package gov.nist.toolkit.configDatatypes.server

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.datatypes.MessageTechnology
import gov.nist.toolkit.configDatatypes.server.datatypes.MessageType
import gov.nist.toolkit.configDatatypes.server.datatypes.Multipart
import spock.lang.Specification

import java.nio.file.Paths


/**
 *
 */
class MessageTypeScannerTest extends Specification {

    def 'xml test'() {
        when:
        String xmlText = Paths.get(getClass().getResource('/').toURI()).resolve('soap.xml').toFile().text
        // String xmlText = getClass().getResource('/soap.xml').text
        assert xmlText
        MessageType type = new MessageTypeScanner('Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"',  xmlText).scan()

        then:
        type
        type.messageTechnology == MessageTechnology.SOAP
        type.soapAction == TransactionType.PROVIDE_AND_REGISTER.requestAction
    }

    def 'part parser test'() {
        given:
        def body = '''
--MIMEBoundary_ba2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <0.8a2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62@apache.org>

<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"/>

--MIMEBoundary_ba2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62--
'''

        when:
        Multipart multipart = MultipartParser.parse('Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"',  body)

        then:
        multipart.parts.size() == 1
        multipart.parts[0].header.startsWith('Content-Type: application/xo')
        multipart.parts[0].body.startsWith('<?')
        multipart.parts[0].id == '0.8a2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62@apache.org'
    }

    def 'multipart parser test'() {
        given:
        def body = '''
--MIMEBoundary_ba2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"
Content-Transfer-Encoding: binary
Content-ID: <0.8a2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62@apache.org>

<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"/>

--MIMEBoundary_ba2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62
Content-Type: text/plain

My Content

--MIMEBoundary_ba2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62--
'''

        when:
        Multipart multipart = MultipartParser.parse('Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"',  body)

        then:
        multipart.parts.size() == 2
        multipart.parts[0].header.startsWith('Content-Type: application')
        multipart.parts[0].body.startsWith('<?')
        multipart.parts[1].header.startsWith('Content-Type: text')
        multipart.parts[1].body.startsWith('My Co')
        multipart.parts[0].id == '0.8a2e384099b4a244bc1c7bab88c735b4f94aeb2925619b62@apache.org'
    }

    def 'multipart test'() {
        when:
        String messageHeader = Paths.get(getClass().getResource('/').toURI()).resolve('multipart.header.txt').toFile().text
        //String messageHeader = getClass().getResource('/multipart.header.txt').text
        String messageBody = Paths.get(getClass().getResource('/').toURI()).resolve('multipart.body.txt').toFile().text
        //String messageBody = getClass().getResource('/multipart.body.txt').text
        assert messageHeader
        assert messageBody
        MessageType type = new MessageTypeScanner(messageHeader, messageBody).scan()

        then:
        type
        type.messageTechnology == MessageTechnology.SOAP
        type.soapAction == TransactionType.PROVIDE_AND_REGISTER.requestAction
    }

    def 'simple test'() {
        when:
        String messageHeader = Paths.get(getClass().getResource('/').toURI()).resolve('simple.header.txt').toFile().text
        String messageBody = Paths.get(getClass().getResource('/').toURI()).resolve('simple.body.txt').toFile().text
        // String messageHeader = getClass().getResource('/simple.header.txt').text
        // String messageBody = getClass().getResource('/simple.body.txt').text
        assert messageHeader
        assert messageBody
        MessageType type = new MessageTypeScanner(messageHeader, messageBody).scan()

        then:
        type
        type.messageTechnology == MessageTechnology.SOAP
        type.soapAction == TransactionType.REGISTER.requestAction
    }

}
