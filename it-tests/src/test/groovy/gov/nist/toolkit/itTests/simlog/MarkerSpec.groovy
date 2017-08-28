package gov.nist.toolkit.itTests.simlog

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimDbEvents
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.testengine.scripts.BuildCollections
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import spock.lang.Shared

/**
 * Test SimDb markers - a tool to isolate events that happen since a certain time
 */
class MarkerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi


    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared String patientId = 'BR14^^^&1.2.360&ISO'
    @Shared String patientId2 = 'BR15^^^&1.2.360&ISO'
    @Shared String envName = 'test'
    @Shared String testSession = 'bill';
    @Shared String id = 'rec'
    @Shared String id2 = 'rec2'
    @Shared String rec = "${testSession}__${id}"
    @Shared String rec2 = "${testSession}__${id2}"
    @Shared SimId simId = new SimId(rec)  // ultimate destination
    @Shared SimId simId2 = new SimId(rec2)

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        api.deleteSimulatorIfItExists(simId)
        api.deleteSimulatorIfItExists(simId2)

        // local customization

        new BuildCollections().init(null)

        api.createTestSession(testSession)

        spi.delete(id, testSession)
        spi.delete(id2, testSession)

        Installation.instance().defaultEnvironmentName()

        spi.create(
                id,
                testSession,
                SimulatorActorType.DOCUMENT_RECIPIENT,
                envName)

        spi.create(
                id2,
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

    def 'marker test - single sim'() {
        setup:
        SiteSpec siteSpec = new SiteSpec(rec)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");
        SimDb db = new SimDb(simId)
        SimDbEvents events = new SimDbEvents([simId])

        when: 'one event before marker'
        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        when: 'set marker'
        events.createMarker()

        then:
        true

        when: 'one event after marker'
        testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        when: 'one event after marker'
        testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        then: 'must be two events after marker'
        events.getEventsSinceMarker().size() == 2

    }

    def 'marker test - two sim'() {
        setup:
        SiteSpec siteSpec = new SiteSpec(rec)
        TestInstance testInstance = new TestInstance('12360')
        List<String> sections = ['submit']
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', "P20160803215512.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO");

        // Build SimDbEvents - managing two sims
        SimDbEvents events = new SimDbEvents([simId, simId2])

        when: 'one event before marker'
        TestOverviewDTO testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        when: 'set marker'
        events.createMarker()

        then:
        true

        when: 'one event after marker same Sim'
        testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        when: 'one event after marker - other Sim'
        testOverviewDTO = session.xdsTestServiceManager().runTest(envName, testSession, siteSpec, testInstance, sections, params, null, true)

        then:
        testOverviewDTO.sections.get('submit').pass

        then: 'must be two events after marker'
        events.getEventsSinceMarker().size() == 2

    }

}
