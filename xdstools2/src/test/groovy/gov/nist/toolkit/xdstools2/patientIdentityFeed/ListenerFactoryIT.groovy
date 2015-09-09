package gov.nist.toolkit.xdstools2.patientIdentityFeed

import gov.nist.toolkit.actorfactory.PifHandler
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actortransaction.client.ATFactory
import gov.nist.toolkit.adt.AdtSender
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.adt.ThreadPoolItem
import gov.nist.toolkit.session.server.TestSession
import org.apache.log4j.Logger
import spock.lang.Specification
/**
 * Created by bill on 9/2/15.
 */
class ListenerFactoryIT extends Specification {
    static Logger logger = Logger.getLogger(ListenerFactoryIT.class);
    def simId1 = 'reg1'
    def pid1 = 'A1^^^&1.2.3&ISO'
    def pid2 = 'A2^^^&1.2.3&ISO'
    PifHandler pifHandler = new PifHandler()
    int firstPort = 5000
    int secondPort = 5001
    int lastPort = 5005

    def setup() {
        TestSession.setupToolkit()
        ListenerFactory.init(firstPort, lastPort)
        SimDb.mkSim(simId1, ATFactory.ActorType.REGISTRY.shortName).delete()
        SimDb.mkSim(simId1, ATFactory.ActorType.REGISTRY.shortName)
    }

    def 'Start/stop a listener'() {
        when:
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1);
        ThreadPoolItem item = ListenerFactory.getItem(simId1)

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

    def 'Listen and send'() {
        when: 'Start patientIdentityFeed'
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1, firstPort, new PifHandler());
        ThreadPoolItem item = ListenerFactory.getItem(simId1)

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse
        !pifHandler.hasPatientId(simId1, pid1)

        when: 'Send A01'
        def port = item.port
        String templateFile = getClass().getResource('/adt/A01.txt').file
        AdtSender sender = new AdtSender(templateFile, 'localhost', port)
        sender.send(pid1)

        then:
        pifHandler.hasPatientId(simId1, pid1)
        !pifHandler.hasPatientId(simId1, "xxx")  // random name to check for

        when:
        sleep(1*1000)
        ListenerFactory.terminate(simId1)

        then:
        true
    }

    def 'Listen and send 2'() {
        setup:
        String templateFile = getClass().getResource('/adt/A01.txt').file

        when: 'Start patientIdentityFeed'
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1, firstPort, new PifHandler());
        ThreadPoolItem item = ListenerFactory.getItem(simId1)
        def port = item.port

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse
        !pifHandler.hasPatientId(simId1, pid1)

        when: 'Send A01'
        new AdtSender(templateFile, 'localhost', port).send(pid1)

        then:
        pifHandler.hasPatientId(simId1, pid1)
        !pifHandler.hasPatientId(simId1, "xxx")  // random name to check for

        when: 'Send another A01'
        new AdtSender(templateFile, 'localhost', port).send(pid2)

        then:
        pifHandler.hasPatientId(simId1, pid1)
        pifHandler.hasPatientId(simId1, pid2)

        when:
        sleep(1*1000)
        ListenerFactory.terminate(simId1)

        then:
        true
    }
}
