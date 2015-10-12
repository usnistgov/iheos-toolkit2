import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimId
import spock.lang.Specification
/**
 *
 */
class SimIdTest extends Specification {

    def 'Put SimId 8080'() {
        given:
        SimulatorBuilder builder = new SimulatorBuilder('localhost', '8080');

        when:
        SimId simId = builder.create('reg', 'mike', 'reg', 'NA2015')

        then:
        simId.getId() == 'reg'
    }

    def 'Put SimId 8888'() {
        given:
        SimulatorBuilder builder = new SimulatorBuilder('localhost', '8888');

        when:
        SimId simId = builder.create('reg', 'mike', 'reg', 'NA2015')

        then:
        simId.getId() == 'reg'
    }

    def 'Delete SimId 8888'() {
        given:
        SimulatorBuilder builder = new SimulatorBuilder('localhost', '8888');

        when:
        builder.delete('reg', 'mike')

        then: true
    }
}
