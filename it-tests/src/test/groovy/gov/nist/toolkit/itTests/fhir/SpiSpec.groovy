package gov.nist.toolkit.itTests.fhir

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared
/**
 * Test FHIR accessibility through the toolkit spi
 */
class SpiSpec extends FhirSpecification {
    @Shared SimulatorBuilder spi

    @Shared def testSession = 'default'
    @Shared def simIdName = 'test'

    @Shared SimId simId = new SimId(new TestSession(testSession), simIdName).forFhir()
    @Shared FhirContext ourCtx = FhirContext.forDstu3()

    def setupSpec() {
        startGrizzlyWithFhir('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def 'ensure clean environment'() {
        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)
    }

    def 'create sim and verify without SPI'() {
        when:
        SimDb.fdelete(simId)
        SimDb.mkfSim(simId)

        then:
        SimDb.fexists(simId)
    }

    def 'create sim with SPI shortcut and verify'() {
        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)

        when:
        spi.create(    // shortcut
                simIdName,
                testSession,
                SimulatorActorType.FHIR_SERVER,
                'test'
        )

        then:
        SimDb.fexists(simId)

    }

    def 'create sim with SPI and verify'() {
        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)

        when:
        spi.createFhirServer(simId.id, simId.testSession.value, 'default')

        then:
        SimDb.fexists(simId)

    }

    def 'delete sim with SPI and verify'() {
        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)

        when:
        spi.createFhirServer(simId.id, simId.testSession.value, 'default')

        then:
        SimDb.fexists(simId)

        when:
        spi.delete(spiSimId(simId))

        then:
        !SimDb.fexists(simId)


    }



}
