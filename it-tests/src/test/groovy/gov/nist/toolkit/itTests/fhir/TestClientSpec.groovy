package gov.nist.toolkit.itTests.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 *
 */
class TestClientSpec extends FhirSpecification {
    @Shared SimulatorBuilder spi

    @Shared def testSession = 'bill'
    @Shared def simIdName = 'myfhirsys'
    @Shared def siteName = "${testSession}__${simIdName}"

    @Shared SimId simId = new SimId(testSession, simIdName).forFhir()
    @Shared FhirContext ourCtx = FhirContext.forDstu3()

    @Shared SimDb simDb

    @Shared TestInstance testInstance = new TestInstance('FhirTestClientCreate')


    def setupSpec() {
        startGrizzlyWithFhir('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        // local customization

        new BuildCollections().init(null)

        // SimId must be translated into SPI variety
        spi.delete(spiSimId(simId))   // if you use the form spi.delete(simIdName, testSession) it will look in the SimDb instead of ResDb

        spi.createFhirServer(simId.id, simId.user, 'default')

    }

    def 'do create'() {
        when:
        def sections = ['create']
        def params = [ :]
        List<Result> results = api.runTest(testSession, siteName, testInstance, sections, params, true)

        then:
        results.size() == 1
        results.get(0).passed()
    }

    def 'do read'() {
        when:
        def sections = ['read']
        def params = [ :]
        List<Result> results = api.runTest(testSession, siteName, testInstance, sections, params, true)

        then:
        results.size() == 1
        results.get(0).passed()
    }
}
