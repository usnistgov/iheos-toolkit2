package gov.nist.toolkit.testengine

import gov.nist.toolkit.testengine.fhir.FhirSupport
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.Practitioner
import spock.lang.Specification

class WithoutContainedTest extends Specification {

    def 'test 1' () {
        when:
        DocumentReference dr = new DocumentReference()
        Practitioner pr = new Practitioner()
        dr.addContained(pr)

        then:
        dr.contained.size() == 1

        when:
        DocumentReference drWithout = (DocumentReference) FhirSupport.withoutContained(dr)

        then:
        drWithout.contained.size() == 0

        when:
        String string = FhirSupport.format(drWithout)
        DocumentReference dr2 = (DocumentReference) FhirSupport.parse(string)

        then:
        dr2.contained.size() == 0
    }

    def 'test duplicate' () {
        when:
        DocumentReference dr = new DocumentReference()
        dr = FhirSupport.duplicate(dr)
        Practitioner pr = new Practitioner()
        dr.addContained(pr)

        then:
        dr.contained.size() == 1

        when:
        DocumentReference drWithout = (DocumentReference) FhirSupport.withoutContained(dr)

        then:
        drWithout.contained.size() == 0

        when:
        String string = FhirSupport.format(drWithout)
        DocumentReference dr2 = (DocumentReference) FhirSupport.parse(string)

        then:
        dr2.contained.size() == 0
    }

}
