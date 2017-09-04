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
    @Shared MhdUtility u = new MhdUtility()
    @Shared FhirContext ctx = u.ctx

    def 'is uuid test'() {
        when:
        def value = '3fdc72f4-a11d-4a9d-9260-a9f745779e1d'

        then:
        u.isUUID(value)
    }


    def 'base from url'() {
        when:
        def fullUrl = 'http://localhost:80/fhir/Partient/A'

        then:
        'http://localhost:80/fhir' == u.baseUrlFromUrl(fullUrl)
    }

    def 'relative reference in bundle' () {
        when:
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefrelativebundle1.xml').text)
        u.loadBundle(bundle)

        then: // only one Resource with relative url Patient/a2
        u.rMgr.resolveReference('urn:uuid:1', 'Patient/a2')[0] == 'http://localhost:9556/svc/fhir/Patient/a2'
    }

    def 'load docref with absolute ref to bin test'() {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefabsolutebundle1.xml').text)

        when:
        u.translateBundle(xml, bundle)

        and:
        DocumentReference dr = u.rMgr.getAllOfType('DocumentReference')[0][1]    // returns List of [url, Resource]
        def xmlText = writer.toString()
        println xmlText
        def ol = new XmlSlurper().parseText(xmlText)

        then:
        u.rMgr.getAllOfType('DocumentReference').size() == 1
        u.rMgr.getAllOfType('Binary').size() == 1

        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        ol.ExtrinsicObject[0].@mimeType == 'text/plain'

        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)

        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN'

        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983'}.@nodeRepresentation == 'History and Physical'
        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a'}.@nodeRepresentation == '47039-3'
    }

    def 'load docref with relative ref to bin test'() {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefrelativebundle1.xml').text)

        when:
        u.translateBundle(xml, bundle)

        and:
        DocumentReference dr = u.rMgr.getAllOfType('DocumentReference')[0][1]
        def xmlText = writer.toString()
        println xmlText
        def ol = new XmlSlurper().parseText(xmlText)

        then:
        u.rMgr.getAllOfType('DocumentReference').size() == 1
        u.rMgr.getAllOfType('Binary').size() == 1

        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        ol.ExtrinsicObject[0].@mimeType == 'text/plain'

        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)

        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN'

        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983'}.@nodeRepresentation == 'History and Physical'
        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a'}.@nodeRepresentation == '47039-3'
    }

}
