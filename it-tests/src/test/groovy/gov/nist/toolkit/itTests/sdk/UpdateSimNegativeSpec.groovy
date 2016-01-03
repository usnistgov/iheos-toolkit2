package gov.nist.toolkit.itTests.sdk
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.grizzlySupport.GrizzlyController
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.TestSupport
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.tookitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class UpdateSimNegativeSpec extends Specification {
    @Shared ToolkitApi api
    @Shared Session session
    @Shared def remoteToolkitPort = '8889'
    @Shared SimulatorBuilder spi
    @Shared server



    BasicSimParameters params = new BasicSimParameters();
    SimConfig config
    def parmName = "Validate_Codes"

    def setupSpec() {   // one time setup done when class launched
        (session, api) = TestSupport.INIT()

        // Start up a full copy of toolkit, running on top of Grizzly instead of Tomcat
        // on port remoteToolkitPort
        server = new GrizzlyController()
        server.start(remoteToolkitPort);
        server.withToolkit()

        // Is this still needed?
        Installation.installation().overrideToolkitPort(remoteToolkitPort)  // ignore toolkit.properties

        // Initialze remote api for talking to toolkit on Grizzly
        String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
        spi = new SimulatorBuilder(urlRoot)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = 'mike'
        params.actorType = SimulatorActorType.REGISTRY
        params.environmentName = 'test'

        // establish test environment - newly created sim
        spi.delete(params)
        config = (SimConfig) spi.create(params)
    }

    def 'Update deleted sim'() {
        when:
        spi.delete(params)
        config.setProperty(parmName, false)
        spi.update(config)

        then:
        ToolkitServiceException e = thrown()
        e.extendedCode == 40401
    }

    def 'Update property undefined property'() {
        when:
        config.setProperty("MyParam", false)
        SimConfig returnedConfig = spi.update(config)

        then: 'No update made'
        !returnedConfig
    }

    def 'Update property with wrong type'() {
        when:
        config.setProperty(parmName, 'foo')
        spi.update(config)

        then:
        ToolkitServiceException e = thrown()
        e.code == 409
        e.reason.contains 'wrong type'
    }
}
