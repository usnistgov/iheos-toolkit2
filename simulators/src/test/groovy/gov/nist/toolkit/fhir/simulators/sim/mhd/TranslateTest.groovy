package gov.nist.toolkit.fhir.simulators.sim.mhd

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.server.resourceMgr.FileSystemResourceCache
import gov.nist.toolkit.fhir.server.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.fhir.server.resourceMgr.ResourceMgr
import gov.nist.toolkit.fhir.server.resourceMgr.TestResourceCacheFactory
import gov.nist.toolkit.fhir.server.utility.UriBuilder
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.simcoresupport.mhd.MhdGenerator
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Patient
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class TranslateTest extends Specification {
    @Shared FhirContext ctx = FileSystemResourceCache.ctx
    @Shared ResourceCacheMgr resourceCacheMgr
    @Shared MhdGenerator u
    @Shared SimProxyBase proxyBase
    @Shared ResourceMgr r

    def setupSpec() {
        Installation.setTestRunning(true)
        resourceCacheMgr = TestResourceCacheFactory.getResourceCacheMgr()
//        Installation.instance().resourceCacheMgr(resourceCacheMgr)
        u = new MhdGenerator(proxyBase, resourceCacheMgr)
        r = new ResourceMgr()
    }

    def 'is uuid test'() {
        when:
        def value = '3fdc72f4-a11d-4a9d-9260-a9f745779e1d'

        then:
        r.isUUID(value)
    }


    def 'base from url'() {
        when:
        def fullUrl = 'http://localhost:80/fhir/Partient/A'

        then:
        'http://localhost:80/fhir' == r.baseUrlFromUrl(UriBuilder.build(fullUrl)).toString()
    }

    def 'relative reference in bundle' () {
        when:
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefrelativebundle1.xml').text)
        r = new ResourceMgr(bundle, null)

        then: // only one Resource with relative url Practitioner/a3
        r.resolveReference('urn:uuid:1', 'Practitioner/a3')[0].toString() == 'http://localhost:9556/svc/fhir/Practitioner/a3'
    }

    def 'patient id from patient resource' () {
        when:
        Installation.instance().setTestRunning(true)
        ResourceCacheMgr cacheMgr = TestResourceCacheFactory.getResourceCacheMgr()
        def patient = cacheMgr.getResource('http://localhost:8080/fhir/Patient/a2')

        then:
        patient instanceof Patient

        when:
        def pid = u.cxiFromPatient(patient)

        then:
        pid == 'MRN^^^&1.2.3.4.5.6&ISO^urn:ihe:iti:xds:2013:accession'
    }

    def 'slot test' () {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        when:
        u.addSlot(xml, 'myslot', ['value1', 'value2'])

        and:
        def xmlText = writer.toString()
        def o = new XmlSlurper().parseText(xmlText)

        then:
        o.name() == 'Slot'
        o.@name == 'myslot'
        o.ValueList.Value[0] == 'value1'
        o.ValueList.Value[1] == 'value2'
    }

    def 'load docref with absolute ref to bin test'() {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docrefabsolutebundle1.xml').text)

        when:
        u.translateBundle(xml, bundle, true)

        and:
        DocumentReference dr = u.rMgr.getResourcesByType('DocumentReference')[0][1]    // returns List of [url, Resource]
        def xmlText = writer.toString()
        println xmlText
        def ol = new XmlSlurper().parseText(xmlText)

        then:
        u.rMgr.getResourcesByType('DocumentReference').size() == 1
        u.rMgr.getResourcesByType('Binary').size() == 1

        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

//        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        ol.ExtrinsicObject[0].@mimeType == 'text/plain'

        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)

        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
//        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN^^^&1.2.3.4.5&ISO'

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
        u.translateBundle(xml, bundle, true)

        and:
        DocumentReference dr = u.rMgr.getResourcesByType('DocumentReference')[0][1]
        def xmlText = writer.toString()
        println xmlText
        def ol = new XmlSlurper().parseText(xmlText)

        then:
        println '================   Error Logger output  =================='
        println u.errorLogger.asString()
        println '=========================================================='

        u.errorLogger.size() == 0

        u.rMgr.getResourcesByType('DocumentReference').size() == 1
        u.rMgr.getResourcesByType('Binary').size() == 1

        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
//        '20130701231133' == utcTime   // this tests for "wrong" hour

//        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
        ol.ExtrinsicObject[0].@mimeType == 'text/plain'

        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)

        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
//        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN'

        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983'}.@nodeRepresentation == 'History and Physical'
        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a'}.@nodeRepresentation == '47039-3'

    }

//    def 'load docref with with reference to Patient in bundle'() {
//        setup:
//        u.clear()
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def bundle = ctx.newXmlParser().parseResource(getClass().readResource('/resources/docrefpatientinbundle.xml').text)
//
//        when:
//        u.translateBundle(xml, bundle, true)
//
//        and:
//        DocumentReference dr = u.rMgr.getResourcesByType('DocumentReference')[0][1]
//        def xmlText = writer.toString()
//        println xmlText
//        def ol = new XmlSlurper().parseText(xmlText)
//
//        then:
//        println '================   Error Logger output  =================='
//        println u.errorLogger.asString()
//        println '=========================================================='
//
//        u.errorLogger.size() == 0
//
//        u.rMgr.getResourcesByType('DocumentReference').size() == 1
//        u.rMgr.getResourcesByType('Binary').size() == 1
//
//        '20130701' == u.translateDateTime(dr.indexed).substring(0, 8)
////        '20130701231133' == utcTime   // this tests for "wrong" hour
//
////        ol.ExtrinsicObject[0].@id == 'urn:uuid:3fdc72f4-a11d-4a9d-9260-a9f745779e1d'
//        ol.ExtrinsicObject[0].@mimeType == 'text/plain'
//
//        ol.ExtrinsicObject[0].Slot.find {it.@name == 'creationTime'}.ValueList.Value == u.translateDateTime(dr.indexed)
//        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStartTime'}.ValueList.Value == u.translateDateTime(dr.context.period.start)
//        ol.ExtrinsicObject[0].Slot.find {it.@name == 'serviceStopTime'}.ValueList.Value == u.translateDateTime(dr.context.period.end)
//
//        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab'}.@value == '129.6.58.92.88336'
//        ol.ExtrinsicObject[0].ExternalIdentifier.find {it.@identificationScheme == 'urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427'}.@value == 'MRN'
//
//        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:f0306f51-975f-434e-a61c-c59651d33983'}.@nodeRepresentation == 'History and Physical'
//        ol.ExtrinsicObject[0].Classification.find {it.@classificationScheme == 'urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a'}.@nodeRepresentation == '47039-3'
//
//    }

//    def 'load docref with with relative reference to Patient in bundle'() {
//        setup:
//        u.clear()
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def bundle = ctx.newXmlParser().parseResource(getClass().readResource('/resources/docrefpatientrelinbundle.xml').text)
//
//        when:
//        u.translateBundle(xml, bundle, true)
//
//        and:
//        DocumentReference dr = u.rMgr.getResourcesByType('DocumentReference')[0][1]
//        def xmlText = writer.toString()
//        println xmlText
//        def ol = new XmlSlurper().parseText(xmlText)
//
//        then:
//        println '================   Error Logger output  =================='
//        println u.errorLogger.asString()
//        println '=========================================================='
//
//        u.errorLogger.size() == 1
//        def error = u.errorLogger.getError(0)
//        assert error instanceof ResourceNotAvailable
//        error.referencedUrl == 'Patient/a5'
//    }

    def 'load document manifest' () {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def resource = ctx.newXmlParser().parseResource(getClass().getResource('/resources/docman1.xml').text)

        when:
        u.translateResource(xml, resource)
        def xmlText = writer.toString()
        println xmlText
        println '================   Error Logger output  =================='
        println u.errorLogger.asString()
        println '=========================================================='
        println u.rMgr

        then:
        u.errorLogger.size() == 0
    }

    def 'single doc submit' () {
        setup:
        u.clear()
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def bundle = ctx.newXmlParser().parseResource(getClass().getResource('/resources/singledocsubmit.xml').text)

        when:
        u.translateBundle(xml, bundle, true)
        def xmlText = writer.toString()
        println xmlText
        println '================   Error Logger output  =================='
        println u.errorLogger.asString()
        println '=========================================================='

        then:
        u.errorLogger.size() == 0
    }

}
