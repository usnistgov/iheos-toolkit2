package gov.nist.toolkit.fhirServer.prototype

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.dstu2.resource.Patient
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Specification
/**
 *
 */
class LoadHapiPatientTest extends Specification {

    def 'simple load patient with HAPI test'() {
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
        patient.identifier[0].system == 'urn:oid:1.2.36.146.595.217.0.1'
        patient.identifier[0].value == '12345'
    }
}
