package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.orchestration.FhirSupportOrchestrationBuilder
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb

class FhirSupportOrchestrationBuilderSpec extends FhirSpecification  {
    def userName = 'fhirsupport'

    def setupSpec() {
        startGrizzlyWithFhir('8889')   // sets up Grizzly server on remoteToolkitPort
    }

    def 'test full build' () {
        setup:
        FhirSupportOrchestrationRequest request = new FhirSupportOrchestrationRequest()
        request.userName = userName
        request.environmentName = 'test'
        request.useExistingState = false

        FhirSupportOrchestrationBuilder builder = new FhirSupportOrchestrationBuilder(api, session, request)
        SimId simId = new SimId(builder.siteName)
        println "Simid is ${simId}"

        when:
        RawResponse rawResponse = builder.buildTestEnvironment()
        assert rawResponse instanceof FhirSupportOrchestrationResponse
        FhirSupportOrchestrationResponse response = rawResponse

        then:
        !response.hasError()

        when:
        response.patients.each { FhirSupportOrchestrationResponse.PatientDef pd ->
            println "${pd.pid}, ${pd.given}, ${pd.family}, ${pd.url}"
        }

        then:
        response.patients.size() == 1

        when:
        SimDb db = new SimDb(simId)
        File simDbFile = db.getSimDir()
        println "Sim Dir is ${simDbFile}"

        then:
        simDbFile.exists()
        simDbFile.isDirectory()

        when:
        File simIndex = new File(simDbFile, 'simindex')
        println "Looking for ${simIndex}"

        then:
        simIndex.exists()

    }
}
