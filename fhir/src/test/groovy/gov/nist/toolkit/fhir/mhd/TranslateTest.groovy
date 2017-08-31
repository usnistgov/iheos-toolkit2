package gov.nist.toolkit.fhir.mhd

import ca.uhn.fhir.context.FhirContext
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.*
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class TranslateTest extends Specification {

    @Shared FhirContext ctx = FhirContext.forDstu3()

    def 'utilities test'() {
        when:
        MhdUtility u = new MhdUtility()
        def fullUrl = 'http://localhost:80/fhir/Partient/A'

        then:
        'http://localhost:80/fhir' == u.baseUrlFromUrl(fullUrl)
    }

    def 'load test'() {
        setup:
        MhdUtility u = new MhdUtility()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)


        when:
        Bundle bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefbundle1.xml').text)

        then:
        bundle
        bundle.type == Bundle.BundleType.TRANSACTION

        when:
        bundle.getEntry().each { Bundle.BundleEntryComponent component ->
            if (component.hasResource()) {
                Resource resource = component.getResource()
                if (resource instanceof DocumentReference) {
                    println "got documentreference  - ${component.fullUrl}"
                    DocumentReference dr = (DocumentReference) resource
                    u.docRefs[component.fullUrl] = dr
                    String patientReference = dr.subject.reference
                    println "patientReference is ${patientReference} - ${u.buildFullUrl(u.baseUrlFromUrl(component.fullUrl),patientReference)}"
                }
                else if (resource instanceof Binary) {
                    println "got binary  - ${component.fullUrl}"
                    Binary b = (Binary) resource
                    String id = b.getId();
                    u.binaries[component.fullUrl] = b
                }
                else if (resource instanceof Patient) {
                    println "got patient  - ${component.fullUrl}"
                    Patient p = (Patient) resource
                    String id = p.getId()
                    u.patients[component.fullUrl] = p
                    Identifier ident = u.getOfficial(p.getIdentifier())
                    String value = ident?.value
                    if (value) u.patients[value] = p
                }
                else if (resource instanceof Practitioner) {
                    println "got practitioner  - ${component.fullUrl}"
                    Practitioner p = (Practitioner) resource
                    String id = p.getId()
                    u.practitioners[component.fullUrl] = p
                }
            }
        }

        then:
        u.docRefs.size() == 1
        u.binaries.size() == 1
        // ensure correct binary is attached
        u.docRefs.values()[0].content[0].attachment.url == u.binaries.keySet()[0]

        when:
        DocumentReference dr = u.docRefs.values()[0]
        String drId = u.resourceIdFromUrl(dr.getId())
        drId = (u.isUUID(drId)) ?  u.asUUID(drId) : 'Document1'

        Binary bin = u.binaries.values()[0]

        // TODO - add hash
        // TODO - add languageCode
        // TODO - add legalAuthenticator
        // TODO - add sourcePatientInfo
        // TODO - add referenceIdList
        // TODO - add case where Patient not in bundle

        then:
        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

        when:
        xml.RegistryObjectList(xmlns: 'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0') {
            u.addExtrinsicObject(xml, dr.getId(), dr, bin)
        }

        and:
        def xmlText = writer.toString()
        println xmlText
        def ol = new XmlSlurper().parseText(xmlText)

        then:
        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        ol.ExtrinsicObject[0].@mimeType == 'text/plain'

        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)

        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN'
    }

}
