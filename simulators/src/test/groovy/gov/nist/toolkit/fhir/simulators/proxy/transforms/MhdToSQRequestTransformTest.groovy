package gov.nist.toolkit.fhir.simulators.proxy.transforms

import spock.lang.Specification

class MhdToSQRequestTransformTest extends Specification {
    SQParamTransform paramXfrm = new SQParamTransform()
    SQTransform xfrm = new SQTransform()

    def 'patient.identifier'() {
        when:
        def f = 'patient.identifier=urn:oid:1.2.3|123;indexed=lt2013-01-14;indexed=gt2011-01-14'
        String sq = xfrm.run(f)
        println sq

        then:
        true
    }
}
