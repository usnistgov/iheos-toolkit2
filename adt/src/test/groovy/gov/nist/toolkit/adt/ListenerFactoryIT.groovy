package gov.nist.toolkit.adt

import gov.nist.toolkit.session.server.TestSession
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by bill on 9/2/15.
 */
class ListenerFactoryIT extends Specification {
    static Logger logger = Logger.getLogger(ListenerFactoryIT.class);
    def simId1 = 'reg1'

    def setup() {
        TestSession.setupToolkit()
    }

    def 'Start/stop a listener'() {
        when:
        logger.info("generate listener")
        ListenerFactory.generateListener(simId1);
        def item = ListenerFactory.getItem(simId1)

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse

        logger.info("sleep for 5 seconds")
        sleep(5*1000);
        logger.info("sleep over - signal termination")
        ListenerFactory.terminate(simId1)
        logger.info("sleep for 5 seconds")
        sleep(5*1000);
        logger.info("exiting")

        then:  'Verify thread terminated and ThreadPoolItem no longer findable'
        !item.inUse
        !ListenerFactory.getItem(simId1)

    }
}
