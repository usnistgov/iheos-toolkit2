package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.actortransaction.shared.ActorType
//import org.apache.log4j.BasicConfigurator
import spock.lang.Specification
/**
 *
 */
class ActorFactoryLoadTest extends Specification {

    def 'load test'() {
        setup:
//        BasicConfigurator.configure();

        when:
        GenericSimulatorFactory fact = new GenericSimulatorFactory(null)

        then:
        new GenericSimulatorFactory().getActorFactory(ActorType.REPOSITORY)

    }
}
