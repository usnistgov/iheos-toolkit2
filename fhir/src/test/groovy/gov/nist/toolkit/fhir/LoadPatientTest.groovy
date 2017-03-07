package gov.nist.toolkit.fhir

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.dstu2.resource.Patient
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Specification
/**
 *
 */
class LoadPatientTest extends Specification {

    def 'simple test'() {
        when:  '''load a Patient resource'''
        String patientString = this.getClass().getResource('/Patient1.json').text

        then:
        patientString && patientString != ''

        when:
        FhirContext ctx = FhirContext.forDstu2()
        IParser parser = ctx.newJsonParser()
        IBaseResource res = parser.parseResource(patientString)

        then:
        res instanceof Patient

        when:
        Patient patient = (Patient) res

        then:
        patient.name[0].family[0].getValue() == 'Chalmers'
    }
}
