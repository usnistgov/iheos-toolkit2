package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.HttpEntity
import spock.lang.Shared
/**
 *
 */
class WriteReadTest extends FhirSpecification {
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
    },
    {
      "use": "usual",
      "given": [
        "Jim"
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

    @Shared SimId simId = new SimId('default', 'test')

    def setupSpec() {
        startGrizzly('8889')   // sets up Grizzly server on remoteToolkitPort

        new ResDb().mkSim(simId)
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
    }


    def 'write'() {
        when:
        HttpEntity results = post("http://localhost:${remoteToolkitPort}/xdstools2/fsim/${simId}/Patient", patient)
        InputStream is = results.getContent()
        String content = Io.getStringFromInputStream(is)
        println "Response is ${content}"

        then:
        true
    }

    String mkUrl() {
        "http://localhost:${remoteToolkitPort}/xdstools2/fsim/${simId}/Patient"
    }
}
