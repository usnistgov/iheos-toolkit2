package gov.nist.toolkit.sdkTest
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.EngineSpi
import gov.nist.toolkit.tookitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class UpdateSimNegativeTest extends Specification {
    def host='localhost'
    @Shared def port = '8889'
    EngineSpi builder = new EngineSpi(String.format('http://localhost:%s/xdstools2', port));
    @Shared GrizzlyController server
    BasicSimParameters params = new BasicSimParameters();
    SimConfig config
    def parmName = "Validate_Codes"

    def setupGrizzly() {
        server = new GrizzlyController()
        server.start(port);
    }

    def setupSpec() {   // one time setup done when class launched
        UnitTestEnvironmentManager.setupLocalToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = 'mike'
        params.actorType = SimulatorActorType.REGISTRY
        params.environmentName = 'test'

        // establish test environment - newly created sim
        builder.delete(params)
        config = (SimConfig) builder.create(params)
    }

    def 'Update deleted sim'() {
        when:
        builder.delete(params)
        config.setProperty(parmName, false)
        builder.update(config)

        then:
        ToolkitServiceException e = thrown()
        e.extendedCode == 40401
    }

    def 'Update property undefined property'() {
        when:
        config.setProperty("MyParam", false)
        SimConfig returnedConfig = builder.update(config)

        then: 'No update made'
        !returnedConfig
    }

    def 'Update property with wrong type'() {
        when:
        config.setProperty(parmName, 'foo')
        builder.update(config)

        then:
        ToolkitServiceException e = thrown()
        e.code == 409
        e.reason.contains 'wrong type'
    }
}
