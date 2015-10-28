import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfigBean
import gov.nist.toolkit.toolkitServicesCommon.SimId
import spock.lang.Specification



/**
 *
 */
class CreateSimTest extends Specification {
    def host='localhost'
    def port = '8888'
    SimulatorBuilder builder = new SimulatorBuilder(host, port);

    def 'Put SimId'() {
        given: 'This builds sim with default parameters'

        when:
        SimId simId = builder.create('reg', 'mike', 'reg', 'NA2015')

        then:
        simId.getId() == 'reg'
    }

    def 'Delete SimId'() {
        when:
        builder.delete('reg', 'mike')

        then: true
    }

    // Create a simulator and retrieve all its parameters and settings
    def 'Get SimBean'() {
        when:
        builder.delete('reg', 'mike')
        SimId simId = builder.create('reg', 'mike', 'reg', 'NA2015')

        then:
        simId.getId() == 'reg'

        when:
        SimConfigBean config = builder.getSimConfig(simId)
        println config

        then:
        simId.getId() == config.getId()
    }
}
