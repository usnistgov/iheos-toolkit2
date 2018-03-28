package gov.nist.toolkit.itTests.cat.patientManagement

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.HumanName
import org.hl7.fhir.dstu3.model.Identifier
import org.hl7.fhir.dstu3.model.Patient
import spock.lang.Shared
import spock.lang.Specification
/**
 * this takes a Patient resource bundle and generates the pids file for toolkit
 */
class ConvertPatients2018 extends Specification {
    @Shared String inputFileName = '/testdata/patientManagement/2018.xml'
    @Shared String outputFileName = '/Users/bill/tmp/toolkit2a/environment/cat/pids.txt'
    @Shared File datasets = new File('/Users/bill/tmp/toolkit2a/datasets')
    @Shared FhirContext ctx = ToolkitFhirContext.get()
    @Shared def redAA = '1.3.6.1.4.1.21367.13.20.1000'
    @Shared def greenAA = '1.3.6.1.4.1.21367.13.20.2000'
    @Shared def blueAA = '1.3.6.1.4.1.21367.13.20.3000'
    @Shared Bundle patientBundle = (Bundle) ctx.newXmlParser().parseResource(this.class.getResource(inputFileName).text)

    def 'selftest 1' () {
        when:
        Bundle.BundleEntryComponent comp = (Bundle.BundleEntryComponent)patientBundle.getEntry().first()
        def resource = comp.getResource()
        //def resource = patientBundle.getResourceById(new IdDt('Patient', '8329'))

        then:
        resource instanceof Patient

        when:
        Patient patient = (Patient) resource

        then:
        redPID(patient) == 'IHERED-992^^^&1.3.6.1.4.1.21367.13.20.1000&ISO'
        greenPID(patient) == 'IHEGREEN-992^^^&1.3.6.1.4.1.21367.13.20.2000&ISO'
        bluePID(patient) == 'IHEBLUE-992^^^&1.3.6.1.4.1.21367.13.20.3000&ISO'
        name(patient) == 'Moore; Chip'
    }

    def 'run V2 data' () {
        when:
        StringBuilder buf = new StringBuilder()
        buf.append('[')
        patientBundle.getEntry().each { Bundle.BundleEntryComponent comp ->
            Patient patient = (Patient) comp.getResource()
            if (name(patient)) {
                buf
                        .append(redPID(patient)).append(',')
                        .append(greenPID(patient)).append(',')
                        .append(bluePID(patient)).append(',')
                        .append(name(patient))
                        .append('\n')
            }
        }
        buf.append(']')
        new File(outputFileName).text = buf.toString()

        then:
        true
    }

    def 'run FHIR Patient resources' () {
        when:
        File patients = new File(new File(datasets, 'CAT'), 'Patients')
        patients.mkdirs()
        patientBundle.getEntry().each { Bundle.BundleEntryComponent comp ->
            Patient patient = (Patient) comp.getResource()
            String name = filename(patient)
            new File(patients, name).text = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(patient)
        }

        then:
        true
    }


    String redPID(Patient patient) {
        Identifier ident = identifier(patient, redAA)
        assert ident
        "${ident.value}^^^&${oid(ident.system)}&ISO"
    }

    String greenPID(Patient patient) {
        Identifier ident = identifier(patient, greenAA)
        assert ident
        "${ident.value}^^^&${oid(ident.system)}&ISO"
    }

    String bluePID(Patient patient) {
        Identifier ident = identifier(patient, blueAA)
        assert ident
        "${ident.value}^^^&${oid(ident.system)}&ISO"
    }

    String name(Patient patient) {
        HumanName name = patient.getNameFirstRep()
        String family = name.family
        String given = (name.given) ? name.given.first() : ''
        "${nameCase(family)}; ${nameCase(given)}"
    }

    String filename(Patient patient) {
        HumanName name = patient.getNameFirstRep()
        String family = name.family
        String given = (name.given) ? name.given.first() : ''
        "${nameCase(family)}_${nameCase(given)}"
    }

    String nameCase(String str) {
        if (!str)
            return str
        String first = str.substring(0, 1)
        String rest = str.substring(1)
        "${first.toUpperCase()}${rest.toLowerCase()}"
    }

    Identifier identifier(Patient patient, String systemSuffix) {
        patient.getIdentifier().find { Identifier id -> id.system.endsWith(systemSuffix)}
    }

    String oid(String oid) {
        if (oid.startsWith('urn:oid:'))
            oid.split(':')[2]
        else
            oid
    }
}
