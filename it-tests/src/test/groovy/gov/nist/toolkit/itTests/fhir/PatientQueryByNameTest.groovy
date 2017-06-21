package gov.nist.toolkit.itTests.fhir

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.fhir.support.SimIndexManager
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.FhirId
import gov.nist.toolkit.itTests.support.FhirSpecification
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Resource
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Shared
/**
 *
 */
class PatientQueryByNameTest extends FhirSpecification {
    @Shared SimId simId = new SimId('default', 'test')
    @Shared FhirContext ourCtx = FhirContext.forDstu3()

    def setupSpec() {
        ResDb.delete(simId)

        startGrizzly('8889')   // sets up Grizzly server on remoteToolkitPort

        new ResDb().mkSim(simId)
    }

    def cleanupSpec() {
        SimIndexManager.close()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
    }

    def 'submit patient'() {
        when:
        def (BasicStatusLine statusLine, String results, FhirId locationHeader) = post("http://localhost:${remoteToolkitPort}/xdstools2/fsim/${simId}/Patient", patient)
        OperationOutcome oo
        if (results) {
            IBaseResource resource = ourCtx.newJsonParser().parseResource(results)
            if (resource instanceof OperationOutcome) {
                oo = (OperationOutcome) resource
                println results
            }
        }

        then:
        statusLine.statusCode == 201
        !oo
    }

    // depends on previous
    def 'query by last name'() {
        when:
        def (BasicStatusLine statusLine2, String results2) = get("http://localhost:${remoteToolkitPort}/xdstools2/fsim/${simId}/Patient?family=Chalmers")

        then:
        statusLine2.statusCode == 200

        when:
        IParser parser = ourCtx.newJsonParser()
        IBaseResource bundleResource = parser.parseResource(results2)

        then:
        bundleResource instanceof Bundle

        when:
        Bundle bundle = (Bundle) bundleResource
        def patients = bundle.getEntry().collect { Bundle.BundleEntryComponent comp ->
            Resource resource = comp.getResource()
            assert resource instanceof Patient
            Patient patient = (Patient) resource
            assert patient.name.get(0).family == 'Chalmers'
            resource
        }

        then:
        patients.size() == 1
    }

    // depends on above submission
    def 'query by first and last name'() {
        when:
        def (BasicStatusLine statusLine2, String results2) = get("http://localhost:${remoteToolkitPort}/xdstools2/fsim/${simId}/Patient?family=Chalmers&given=Peter")

        then:
        statusLine2.statusCode == 200

        when:
        IParser parser = ourCtx.newJsonParser()
        IBaseResource bundleResource = parser.parseResource(results2)

        then:
        bundleResource instanceof Bundle

        when:
        Bundle bundle = (Bundle) bundleResource
        def patients = bundle.getEntry().collect { Bundle.BundleEntryComponent comp ->
            Resource resource = comp.getResource()
            assert resource instanceof Patient
            Patient patient = (Patient) resource
            assert patient.name.get(0).family == 'Chalmers'
            resource
        }

        then:
        patients.size() == 1
    }

    def patient = '''
{
  "resourceType": "Patient",
  "text": {
    "status": "generated",
    "div": "<div>\\n      \\n      <table>\\n        \\n        <tbody>\\n          \\n          <tr>\\n            \\n            <td>Name</td>\\n            \\n            <td>Peter James \\n              <b>Chalmers</b> (&quot;Jim&quot;)\\n            </td>\\n          \\n          </tr>\\n          \\n          <tr>\\n            \\n            <td>Address</td>\\n            \\n            <td>534 Erewhon, Pleasantville, Vic, 3999</td>\\n          \\n          </tr>\\n          \\n          <tr>\\n            \\n            <td>Contacts</td>\\n            \\n            <td>Home: unknown. Work: (03) 5555 6473</td>\\n          \\n          </tr>\\n          \\n          <tr>\\n            \\n            <td>Id</td>\\n            \\n            <td>MRN: 12345 (Acme Healthcare)</td>\\n          \\n          </tr>\\n        \\n        </tbody>\\n      \\n      </table>    \\n    \\n    </div>"
  },
  "identifier": [
    {
      "fhir_comments": [
        "   MRN assigned by ACME healthcare on 6-May 2001   "
      ],
      "use": "usual",
      "type": {
        "coding": [
          {
            "system": "http://hl7.org/fhir/v2/0203",
            "code": "MR"
          }
        ]
      },
      "system": "urn:oid:1.2.36.146.595.217.0.1",
      "value": "12345",
      "period": {
        "start": "2001-05-06"
      },
      "assigner": {
        "display": "Acme Healthcare"
      }
    }
  ],
  "active": true,
  "name": [
    {
      "fhir_comments": [
        "   Peter James Chalmers, but called \\"Jim\\"   "
      ],
      "use": "official",
      "family": [
        "Chalmers"
      ],
      "given": [
        "Peter",
        "James"
      ]
    }
  ],
  "telecom": [
    {
      "fhir_comments": [
        "   home communication details aren't known   "
      ],
      "use": "home"
    },
    {
      "system": "phone",
      "value": "(03) 5555 6473",
      "use": "work"
    }
  ],
  "gender": "male",
  "_gender": {
    "fhir_comments": [
      "   use FHIR code system for male / female   "
    ]
  },
  "birthDate": "1974-12-25",
  "_birthDate": {
    "extension": [
      {
        "url": "http://hl7.org/fhir/StructureDefinition/patient-birthTime",
        "valueDateTime": "1974-12-25T14:35:45-05:00"
      }
    ]
  },
  "deceasedBoolean": false,
  "address": [
    {
      "use": "home",
      "type": "both",
      "line": [
        "534 Erewhon St"
      ],
      "city": "PleasantVille",
      "district": "Rainbow",
      "state": "Vic",
      "postalCode": "3999",
      "period": {
        "start": "1974-12-25"
      }
    }
  ]
}
'''
}
