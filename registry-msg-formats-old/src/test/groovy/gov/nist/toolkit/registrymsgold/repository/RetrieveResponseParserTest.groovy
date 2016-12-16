package gov.nist.toolkit.registrymsgold.repository

import gov.nist.toolkit.registrymsg.repository.RetrieveDocumentResponseGenerator
import gov.nist.toolkit.utilities.xml.OMFormatter
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import spock.lang.Specification
/**
 *
 */

class RetrieveResponseParserTest extends Specification {
// Taken from ITI-TF Rev 12.0 Section 3.39.5.1.2.1 Synchronous Web Services Exchange
// (with one of two DocumentResponse elements removed)
    def singleDocumentMessage = '''
<RetrieveDocumentSetResponse
    xmlns="urn:ihe:iti:xds-b:2007"
    xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0"
    xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
    xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0">
    <rs:RegistryResponse status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/>
    <DocumentResponse>
        <HomeCommunityId>urn:oid:1.2.3.4</HomeCommunityId>
        <RepositoryUniqueId>1.3.6.1.4...1000</RepositoryUniqueId>
        <DocumentUniqueId>1.3.6.1.4...2300</DocumentUniqueId>
        <mimeType>text/xml</mimeType>
        <Document>UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi</Document>
    </DocumentResponse>
</RetrieveDocumentSetResponse>
'''


    def 'Parse single document message' () {
        given:
        OMElement message = Util.parse_xml(singleDocumentMessage)

        when:
        RetrievedDocumentsModel models = new RetrieveResponseParser(message).get()

        then:
        models.size() == 1

        when:
        RetrievedDocumentModel model = models.values().iterator().next()

        then:
        model.home == 'urn:oid:1.2.3.4'
        model.repUid == '1.3.6.1.4...1000'
        model.docUid == '1.3.6.1.4...2300'
        model.content_type == 'text/xml'
        model.contents != null
    }

    def 'Validate DocumentResponse namespace' () {
        given:
        OMElement message = Util.parse_xml(singleDocumentMessage)

        when:
        Iterator<OMElement> it = message.childElements
        def registryResponse = it.next()

        then: registryResponse.localName == 'RegistryResponse'

        when:
        OMElement documentResponse = it.next()
        println documentResponse.getNamespace().namespaceURI

        then: documentResponse.localName == 'DocumentResponse'
    }

    // Taken from ITI-TF Rev 12.0 Section 3.39.5.1.2.1 Synchronous Web Services Exchange
    def doubleDocumentMessage = '''
<RetrieveDocumentSetResponse xmlns="urn:ihe:iti:xds-b:2007"
    xmlns:lcm="urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0"
    xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
    xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0">
    <rs:RegistryResponse status="urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success"/>
    <DocumentResponse>
        <HomeCommunityId>urn:oid:1.2.3.4</HomeCommunityId>
        <RepositoryUniqueId>1.3.6.1.4...1000</RepositoryUniqueId>
        <DocumentUniqueId>1.3.6.1.4...2300</DocumentUniqueId>
        <mimeType>text/xml</mimeType>
        <Document>UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi</Document>
    </DocumentResponse>
    <DocumentResponse>
        <HomeCommunityId>urn:oid:1.2.3.5</HomeCommunityId>
        <RepositoryUniqueId>1.3.6.1.4...2000</RepositoryUniqueId>
        <DocumentUniqueId>1.3.6.1.4...2301</DocumentUniqueId>
        <mimeType>text/xml</mimeType>
        <Document>UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi</Document>
    </DocumentResponse>
</RetrieveDocumentSetResponse>
'''

    def 'Parse two document message' () {
        given:
        OMElement message = Util.parse_xml(doubleDocumentMessage)

        when:
        RetrievedDocumentsModel models = new RetrieveResponseParser(message).get()

        then:
        models.size() == 2

        when:
        Iterator<RetrievedDocumentModel> iterator = models.values().iterator()
        RetrievedDocumentModel model1 = iterator.next()
        RetrievedDocumentModel model2 = iterator.next()

        then:
        model1.home == 'urn:oid:1.2.3.4'
        model1.repUid == '1.3.6.1.4...1000'
        model1.docUid == '1.3.6.1.4...2300'
        model1.content_type == 'text/xml'
        model1.contents != null

        model2.home == 'urn:oid:1.2.3.5'
        model2.repUid == '1.3.6.1.4...2000'
        model2.docUid == '1.3.6.1.4...2301'
        model2.content_type == 'text/xml'
        model2.contents != null
    }

    def 'Generator - parser compare' () {
        given:
        OMElement message = Util.parse_xml(singleDocumentMessage)
        RetrievedDocumentsModel model = new RetrieveResponseParser(message).get()

        when:
        OMElement xml = new RetrieveDocumentResponseGenerator(model).get()
        println new OMFormatter(xml).toString()
        RetrievedDocumentsModel model2 = new RetrieveResponseParser(xml).get()
        println model2

        then:
        model == model2
    }

}
