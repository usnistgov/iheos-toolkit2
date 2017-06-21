package gov.nist.toolkit.actorfactory.client

import gov.nist.toolkit.actorfactory.factories.IGActorFactory
import gov.nist.toolkit.actorfactory.loader.ActorFactoryLoader
import spock.lang.Specification
/**
 *
 */
class ActorFactoryLoaderTest extends Specification {

    def 'load test'() {
        when:
        def factories = ActorFactoryLoader.getActorFactories()

        then:
        factories.contains(IGActorFactory)
    }

}
