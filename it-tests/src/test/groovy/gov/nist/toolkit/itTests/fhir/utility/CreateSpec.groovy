package gov.nist.toolkit.itTests.fhir.utility

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.engine.TransactionSettings
import gov.nist.toolkit.testengine.engine.Xdstest2
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared

/**
 *
 */
class CreateSpec extends FhirSpecification {
    @Shared SimulatorBuilder spi

    @Shared def testSession = 'bill'
    @Shared def simIdName = 'myfhirsys'
    @Shared def siteName = "${testSession}__${simIdName}"

    @Shared SimId simId = new SimId(testSession, simIdName).forFhir()
    @Shared FhirContext ourCtx = FhirContext.forDstu3()

    @Shared SimDb simDb

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
        Session session = new Session(Installation.instance().warHome())
        session.xt = new Xdstest2(Installation.instance().toolkitxFile(), null, session)
        session.transactionSettings = new TransactionSettings()


        then:
        true
    }

}
