package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.client.PatientDef
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.orchestration.FhirSupportOrchestrationBuilder
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimDb

class FhirSupportOrchestrationBuilderSpec extends FhirSpecification  {
    def userName = 'fhirsupport'

    def setupSpec() {
        startGrizzlyWithFhir('8889')   // sets up Grizzly server on remoteToolkitPort
        Installation.instance().testSessions.each { TestSession testSession ->
            new SimDb().deleteAllSims(testSession)
        }
    }

    def 'test full build' () {
        setup:
        FhirSupportOrchestrationRequest request = new FhirSupportOrchestrationRequest()
        request.testSession = new TestSession(userName)
        request.environmentName = 'test'
        request.useExistingState = false

        FhirSupportOrchestrationBuilder builder = new FhirSupportOrchestrationBuilder(api, session, request)
        SimId simId = SimIdFactory.simIdBuilder(builder.siteName)
        println "Simid is ${simId}"

        when:
        RawResponse rawResponse = builder.buildTestEnvironment()
        assert rawResponse instanceof FhirSupportOrchestrationResponse
        FhirSupportOrchestrationResponse response = rawResponse

        then:
        !response.hasError()

        when:
        response.patients.each { PatientDef pd ->
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
