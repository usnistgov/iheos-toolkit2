package gov.nist.toolkit.itTests.simlog

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 *
 */
class MarkerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String id = 'rec'
    @Shared String rec = "${testSession}__${id}"
    @Shared SimId simId = new SimId(rec)  // ultimate destination
    @Shared String proxyId = "simproxy"
    @Shared SimConfig recSimConfig

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        api.deleteSimulatorIfItExists(simId)

        // local customization

        new BuildCollections().init(null)

        api.createTestSession(testSession)

        spi.delete(id, testSession)

        Installation.instance().defaultEnvironmentName()

        recSimConfig = spi.create(
                id,
                testSession,
                SimulatorActorType.DOCUMENT_RECIPIENT,
                envName)

    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
    }

    def 'send to REC'() {
        when:
        SiteSpec siteSpec = new SiteSpec(rec)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass
    }

}
