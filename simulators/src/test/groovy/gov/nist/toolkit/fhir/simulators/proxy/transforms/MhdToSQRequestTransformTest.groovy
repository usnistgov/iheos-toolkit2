package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.mhd.SQParamTranslator
import gov.nist.toolkit.fhir.simulators.mhd.SQTranslator
import spock.lang.Specification

class MhdToSQRequestTransformTest extends Specification {
    SQParamTranslator paramXfrm = new SQParamTranslator()
    SQTranslator xfrm = new SQTranslator()

    def 'patient.identifier'() {
        when:
        def f = 'patient.identifier=urn:oid:1.2.3|123;indexed=lt2013-01-14;indexed=gt2011-01-14;class=urn:class:system|class1'
        String sq = xfrm.run(f)
        println sq

        then:
        true
    }
}
