package gov.nist.toolkit.fhir.simulators.mhd

import spock.lang.Specification

class DateTransformTest extends Specification {

    def 'dtm to FHIR' () {
        expect:
        DateTransform.dtmToFhir('20140922') == '2014-09-22'
        DateTransform.dtmToFhir('201509220800') == '2015-09-22T08:00'
        DateTransform.dtmToFhir('201609220822') == '2016-09-22T08:22'
        DateTransform.dtmToFhir('20160922082213') == '2016-09-22T08:22:13'
    }

    def 'FHIR to dtm' () {
        expect:
        DateTransform.fhirToDtm('2014-09-22') == '20140922'
        DateTransform.fhirToDtm('2015-09-22T08:00') == '201509220800'
        DateTransform.fhirToDtm('2016-09-22T08:22') == '201609220822'
        DateTransform.fhirToDtm('2016-09-22T08:22:13') == '20160922082213'
    }
}
