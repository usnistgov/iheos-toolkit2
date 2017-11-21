package gov.nist.toolkit.fhir.simulators.proxy.transforms

import spock.lang.Ignore
import spock.lang.Specification

class MhdToSQParamRequestTransformTest extends Specification {
    SQParamTransform xfrm = new SQParamTransform()

    // TODO - add tests using search modifiers

    @Ignore
    def 'patient' () {
        when:
        def x = 1

        then:
        false
    }

    def 'patient.identifier'() {
        when:
        def f = 'patient.identifier=urn:oid:1.2.3|123'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryPatientId' : ['123^^^&1.2.3&ISO']] + fd()
    }

    def 'status.current'() {
        when:
        def f = 'status=current'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryStatus' : ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved']] + fd()
    }

    def 'status.superseded'() {
        when:
        def f = 'status=superseded'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryStatus' : ['urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated']] + fd()
    }

    def 'indexed eq'() {
        when:
        def f = 'indexed=eq2013-01-14'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryCreationTimeFrom' : ['20130114'],
                         '$XDSDocumentEntryCreationTimeTo' : ['20130114']] + fd()
    }

    def 'indexed eq year'() {
        when:
        def f = 'indexed=eq2013'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryCreationTimeFrom' : ['2013'],
                         '$XDSDocumentEntryCreationTimeTo' : ['2013']] + fd()
    }

    def 'indexed.lt'() {
        when:
        def f = 'indexed=lt2013-01-14'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryCreationTimeTo' : ['20130114']] + fd()
    }

    def 'indexed gt'() {
        when:
        def f = 'indexed=gt2013-01-14'

        then:
        xfrm.run(f) ==  ['$XDSDocumentEntryCreationTimeFrom' : ['20130114']] + fd()
    }

    def 'indexed lt gt'() {
        when:
        def f1 = 'indexed=gt2013-01-14'
        def f2 = 'indexed=lt2013-02-17'

        then:
        xfrm.run([f1, f2]) ==  ['$XDSDocumentEntryCreationTimeFrom' : ['20130114'],
                         '$XDSDocumentEntryCreationTimeTo' : ['20130217']
        ] + fd()
    }

    @Ignore
    def 'author.given'() {

    }

    @Ignore
    def 'author.family'() {

    }

    def 'class'() {
        when:
        def f= 'class=urn:class:system|class1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryClassCode' : ['class1^^1.2.3.6677']] + fd()
    }

    def 'type'() {
        when:
        def f= 'type=urn:type:system|type1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryTypeCode' : ['type1^^1.2.3.6677.1']] + fd()
    }

    def 'setting'() {
        when:
        def f= 'setting=urn:setting:system|setting1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryPracticeSettingCode' : ['setting1^^1.2.3.6677.2']] + fd()
    }

    def 'period eq'() {
        when:
        def f= 'period=eq2010-03-14'

        then:
        xfrm.run(f) == [
                '$XDSDocumentEntryServiceStartTimeFrom' : ['20100314'],
                '$XDSDocumentEntryServiceStopTimeTo' : ['20100314']] + fd()
    }

    def 'period lt'() {
        when:
        def f= 'period=lt2010-03-14'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryServiceStopTimeTo' : ['20100314']] + fd()
    }

    def 'period gt'() {
        when:
        def f= 'period=gt2010-03-14'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryServiceStartTimeFrom' : ['20100314']] + fd()
    }

    def 'period lt gt'() {
        when:
        def f1= 'period=gt2010-03-14'
        def f2= 'period=lt2011-03-14'

        then:
        xfrm.run([f1, f2]) == [
                '$XDSDocumentEntryServiceStartTimeFrom' : ['20100314'],
                '$XDSDocumentEntryServiceStopTimeTo' : ['20110314']
        ]+ fd()
    }
    def 'facility'() {
        when:
        def f= 'facility=urn:facility:system|facility1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryHealthcareFacilityTypeCode' : ['facility1^^1.2.3.6677.3']] + fd()
    }

    def 'event'() {
        when:
        def f= 'event=urn:event:system|event1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryEventCodeList' : ['event1^^1.2.3.6677.4']] + fd()
    }

    def 'securityLabel'() {
        when:
        def f= 'securityLabel=urn:securityLabel:system|securityLabel1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryConfidentialityCode' : ['securityLabel1^^1.2.3.6677.5']] + fd()
    }

    def 'format'() {
        when:
        def f= 'format=urn:format:system|format1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryFormatCode' : ['format1^^1.2.3.6677.6']] + fd()
    }

    def 'related-id'() {
        when:
        def f= 'related-id=urn:relatedid:system|relatedid1'

        then:
        xfrm.run(f) == ['$XDSDocumentEntryReferenceIdList' : ['relatedid1^^1.2.3.6677.7']] + fdr()
    }

    Map fd() {
        ['QueryType':[SQParamTransform.FindDocsKey]]
    }

    Map fdr() {
        ['QueryType':[SQParamTransform.FindDocsByRefIdKey]]
    }

}
