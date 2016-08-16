package gov.nist.toolkit.itTests.sdk
import gov.nist.toolkit.configDatatypes.SimulatorActorType
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.toolkitApi.BasicSimParameters
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 *
 */
class UpdateSimNegativeSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    BasicSimParameters params = new BasicSimParameters();
    SimConfig config
    def parmName = "Validate_Codes"

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = 'bill'
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

    def 'Set undefined property - should be ignored'() {
        when:
        config.setProperty("MyParam", false)
        SimConfig returnedConfig = spi.update(config)

        then: 'No update made'
        !returnedConfig.propertyNames.contains("MyParam")
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
