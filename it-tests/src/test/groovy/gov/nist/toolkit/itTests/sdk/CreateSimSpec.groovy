package gov.nist.toolkit.itTests.sdk
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.tookitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import spock.lang.Shared

import javax.ws.rs.core.Response
/**
 *
 */
class CreateSimSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    BasicSimParameters params = new BasicSimParameters();


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
    }

    def 'Create sim with invalid config'() {
        when:
        spi.delete(params)
        params.id = ''
        spi.create(params)

        then:
        ToolkitServiceException e = thrown()
        e.code == Response.Status.BAD_REQUEST.statusCode
    }

    def 'Create sim with bad environment'() {
        when:
        spi.delete(params)
        params.environmentName = 'foo'
        spi.create(params)

        then:
        ToolkitServiceException e = thrown()
        e.code == Response.Status.BAD_REQUEST.statusCode
        e.extendedCode == 40001
    }

    def 'Get Unknown SimId'() {
        when:
        spi.get(ToolkitFactory.newSimId('foo', 'bar', 'reg', 'default'))

        then:
        ToolkitServiceException e = thrown()
        e.code == Response.Status.NOT_FOUND.statusCode
    }

    def 'Delete unknown Sim'() {
        when:
        params.id = 'foofoo'
        spi.delete(params)

        then:
        ToolkitServiceException e = notThrown()
    }

    // Create a simulator and retrieve all its parameters and settings
    def 'Create/Retrieve Sim'() {
        when: 'Delete sim in case it exists'
        println 'STEP - DELETE SIM'
        spi.delete(params)

        and: 'Create new sim'
        println 'STEP - CREATE NEW SIM'
        SimId simId = spi.create(params)

        then: 'verify sim built'
        simId.getId() == 'reg'

        when: 'retrieve full configuration'
        println 'STEP - RETRIEVE FULL CONFIGURATION'
        SimConfig config = spi.get(simId)

        then: 'verify configuration'
        simId.getId() == config.getId()
        config.asString('Name') == 'bill__reg'
    }

    static final private parmName = "Validate_Codes"

    def 'Update sim config'() {
        when: 'Delete sim in case it exists'
        spi.delete(params)

        and: 'Create new sim'
        SimConfig config = (SimConfig) spi.create(params)
        println config.describe()

        then: 'verify sim built'
        config.getId() == 'reg'

        when: 'Update Validate_Codes to false'
        config.setProperty(parmName, false)
        SimConfig returnedConfig = spi.update(config)

        then: 'Update should be reflected in returned config'
        returnedConfig != null // null indicates no updates made
        !returnedConfig.asBoolean(parmName)

        and: 'Get fresh copy of resource'
        SimConfig updatedConfig = spi.get(config) // extends SimId

        then: 'Verify change was persisted'
        !updatedConfig.asBoolean(parmName)
    }

}
