package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.search.DocumentReferenceSearch
import gov.nist.toolkit.fhir.server.search.FhirBase
import gov.nist.toolkit.fhir.shared.searchModels.IdentifierSM
import gov.nist.toolkit.fhir.shared.searchModels.ResourceType
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.instance.model.api.IBaseResource
import spock.lang.Specification

/**
 * Excercise the ability to lookup Patient references in support of DocumentReference searches
 */
class DocumentReferenceSearchTest extends Specification {
    // Two mock fhir servers
    def fhirBase1 = new MyFhirBase('http://example.com/fhir')
    def fhirBase2 = new MyFhirBase('http://target.com/fhir')
    // searches go to server 1
    DocumentReferenceSearch search

    Map<String, Patient> theWorld = [:]

    def setup() {
        // search goes to server 1
        search = new DocumentReferenceSearch(fhirBase1, null)
        // server 2 is searched if server 1 doesn't have the content - a simple search path
        // to demonstrate that the DR and the Patient can be in different servers
        search.addSupportFhirServer(fhirBase2)

        // load one patient resource into each server
        theWorld['http://example.com/fhir/Patient/1'] = patientResource(patient1)
        theWorld['http://target.com/fhir/Patient/1'] = patientResource(patient2)

//        search.addSupportFhirServer(new MyFhirBase('http://target.com/fhir'))
    }

    // DR in server 1 and Patient in server 1
    def 'dr search - patient in same server'() {
        when:
        search.patientIdentifier = new IdentifierSM('urn:oid:1.2.36.146.595.217.0.1', '12345')
        def qstring = search.asQueryString()

        then:
        qstring == 'http://example.com/fhir/DocumentReference?subject=http://example.com/fhir/Patient/1'
    }

    // DR in server 1 and Patient in server 2
    def 'dr search - patient in different server'() {
        when:
        search.patientIdentifier = new IdentifierSM('urn:oid:1.2.36.146.595.217.0.1', '123456')
        def qstring = search.asQueryString()

        then:
        qstring == 'http://example.com/fhir/DocumentReference?subject=http://target.com/fhir/Patient/1'
    }

    // Mock fhir server - only responds to patient searches by patient id
    class MyFhirBase extends FhirBase {

        MyFhirBase(String baseUrl) {
            super(baseUrl)
        }

        // This is the focus of the mock - simple search function
        @Override
        Map<URI, IBaseResource> search(ResourceType resourceType, List<String> params) {
            def map = [:]

            def entry =
            theWorld.find { String uri, Patient patient ->
                if (resourceType != ResourceType.Patient) return false
                def pidValue = patient.identifierFirstRep.value
                def requestedPidValue = params[0].split('\\|', 2)[1]
                requestedPidValue == pidValue
            }
            if (entry)
                map[new URI(entry.key)] = entry.value

            return map
        }
    }

    IBaseResource patientResource(def patient) {
        ToolkitFhirContext.get().newJsonParser().parseResource(patient)
    }

    def patient1 = '''
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
  ]
}
'''

    def patient2 = '''
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
      "value": "123456",
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
  "gender": "male",
  "_gender": {
    "fhir_comments": [
      "   use FHIR code system for male / female   "
    ]
  }
}
'''
}
