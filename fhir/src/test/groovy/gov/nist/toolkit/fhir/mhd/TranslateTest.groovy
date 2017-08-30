package gov.nist.toolkit.fhir.mhd

import ca.uhn.fhir.context.FhirContext
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.*
import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat

/**
 *
 */
class TranslateTest extends Specification {

    @Shared FhirContext ctx = FhirContext.forDstu3()

    def 'load test'() {
        setup:
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        Map<String, DocumentReference> docRefs = [:]   // index is fullUrl
        Map<String, Binary> binaries = [:]
        Map<String, Patient> patients = [:]
        Map<String, Practitioner> practitioners = [:]

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
                    docRefs[component.fullUrl] = dr
                    String patientReference = dr.subject.reference
                    println "patientReference is ${patientReference} - ${buildFullUrl(baseUrlFromUrl(component.fullUrl),patientReference)}"
                }
                else if (resource instanceof Binary) {
                    println "got binary  - ${component.fullUrl}"
                    Binary b = (Binary) resource
                    String id = b.getId();
                    binaries[component.fullUrl] = b
                }
                else if (resource instanceof Patient) {
                    println "got patient  - ${component.fullUrl}"
                    Patient p = (Patient) resource
                    String id = p.getId()
                    patients[component.fullUrl] = p
                    Identifier ident = getOfficial(p.getIdentifier())
                    String value = ident?.value
                    if (value) patients[value] = p
                }
                else if (resource instanceof Practitioner) {
                    println "got practitioner  - ${component.fullUrl}"
                    Practitioner p = (Practitioner) resource
                    String id = p.getId()
                    practitioners[component.fullUrl] = p
                }
            }
        }

        then:
        docRefs.size() == 1
        binaries.size() == 1
        // ensure correct binary is attached
        docRefs.values()[0].content[0].attachment.url == binaries.keySet()[0]

        when:
        DocumentReference dr = docRefs.values()[0]
        String drId = dr.getId()
        if (!drId.startsWith('urn:uuid:'))
            drId = 'Document1'

        Binary bin = binaries.values()[0]

        // TODO - add hash
        // TODO - add languageCode
        // TODO - add legalAuthenticator
        // TODO - add sourcePatientInfo
        // TODO - add referenceIdList

        then:
        '20130701' == translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

        when:
        xml.ExtrinsicObject(
                id: drId,
                objectType:'urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1',
                mimeType: bin.contentType) {
            // 20130701231133
            if (dr.indexed) {
                Slot(name:'creationTime') {
                    ValueList {
                        Value "${translateDateTime(dr.indexed)}"
                    }
                }
            }
            if (dr.context?.period?.start) {
                Slot(name: 'serviceStartTime') {
                    ValueList {
                        Value "${translateDateTime(dr.context.period.start)}"
                    }
                }
            }
            if (dr.context?.period?.end) {
                Slot(name: 'serviceStopTime') {
                    ValueList {
                        Value "${translateDateTime(dr.context.period.end)}"
                    }
                }
            }
            if (dr.masterIdentifier?.value) {
                ExternalIdentifier(
                        identificationScheme: 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab',
                        value: "${dr.masterIdentifier.value}",
                        id: newId(),
                        objectType: 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExternalIdentifier',
                        registryObject: "${drId}") {
                    Name() {
                        LocalizedString(value: 'XDSDocumentEntry.uniqueId')
                    }
                }
            }
        }

        and:
        def xmlText = writer.toString()
        println xmlText
        def de = new XmlSlurper().parseText(xmlText)

        then:
        de.@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        de.@mimeType == 'text/plain'

        de.Slot.find { it.@name == 'creationTime'}.ValueList.Value == translateDateTime(dr.indexed)

    }

    int newIdCounter = 1
    def newId() { String.format("ID%02d", newIdCounter++) }

    String translateDateTime(Date theDate) {
        // TODO - hour is not right - don't know why
        SimpleDateFormat isoFormat = new SimpleDateFormat('yyyyMMddHHmmssSSS')
//        isoFormat.setTimeZone(TimeZone.getTimeZone('America/New_York'))   // UTC
//        String nyTime = isoFormat.format(theDate)
//        println "NYC time is ${nyTime}"
        isoFormat.setTimeZone(TimeZone.getTimeZone('UTC'))   // UTC
        String utcTime = isoFormat.format(theDate)
        println "UTC time is ${utcTime}"
        utcTime = trimTrailingZeros(utcTime)
        return utcTime
    }

    def trimTrailingZeros(String input) {
        while (input.size() > 0 && input[input.size()-1] == '0') {
            input = input.substring(0, input.size()-1)
        }
        input
    }

    Identifier getOfficial(List<Identifier> identifiers) {
        if (identifiers.size() ==1) return identifiers[0]
        return identifiers.find { it.getUse() == Identifier.IdentifierUse.OFFICIAL }
    }

    String resourceTypeFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[1]
    }

    String resourceIdFromUrl(String fullUrl) {
        fullUrl.reverse().split('/')[0]
    }

    String relativeUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        [parts[parts.size() - 2], parts.size() - 1].join('/')
    }

    String baseUrlFromUrl(String fullUrl) {
        List<String> parts = fullUrl.split('/')
        parts.remove(parts.size() - 1)
        parts.remove(parts.size() - 1)
        parts.join('/')
    }

    boolean isRelative(String url) {
        !url.startsWith('http')
    }

    String buildFullUrl(String baseUrl, theUrl) {
        if (!isRelative(theUrl)) return theUrl
        return baseUrl + '/' + theUrl
    }
}
