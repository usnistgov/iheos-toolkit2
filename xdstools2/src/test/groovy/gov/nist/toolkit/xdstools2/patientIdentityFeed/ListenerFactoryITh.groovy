package gov.nist.toolkit.xdstools2.patientIdentityFeed

import gov.nist.toolkit.actorfactory.PifHandler
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.adt.AdtSender
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.adt.ThreadPoolItem
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by bill on 9/2/15.
 */
class ListenerFactoryITh extends Specification {
    static Logger logger = Logger.getLogger(ListenerFactoryITh.class);
    def simId1 = new SimId('reg1')
    Pid pid1 = PidBuilder.createPid('A1^^^&1.2.3&ISO')
    Pid pid2 = PidBuilder.createPid('A2^^^&1.2.3&ISO')
    int firstPort = 5000
    int secondPort = 5001
    int lastPort = 5005
    SimDb db

    def setup() {
        UnitTestEnvironmentManager.setupLocalToolkit()
        ListenerFactory.init(firstPort, lastPort)
        SimDb.mkSim(simId1, ActorType.REGISTRY.shortName).delete()
        SimDb.mkSim(simId1, ActorType.REGISTRY.shortName)
        db = new SimDb(simId1)
    }

    def 'Start/stop a listener'() {
        when:
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1.id);
        ThreadPoolItem item = ListenerFactory.getItem(simId1.id)

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse

        logger.info("sleep for 5 seconds")
        sleep(5*1000);
        logger.info("sleep over - signal termination")
        ListenerFactory.terminate(simId1.id)
        logger.info("sleep for 5 seconds")
        sleep(5*1000);
        logger.info("exiting")

        then:  'Verify thread terminated and ThreadPoolItem no longer findable'
        !item.inUse
        !ListenerFactory.getItem(simId1.id)

    }

    def 'Listen and send'() {
        when: 'Start patientIdentityFeed'
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1.id, firstPort, new PifHandler());
        ThreadPoolItem item = ListenerFactory.getItem(simId1.id)

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse
        !db.patientIdExists(pid1)

        when: 'Send A01'
        def port = item.port
        String templateFile = getClass().getResource('/adt/A01.txt').file
        AdtSender sender = new AdtSender(templateFile, 'localhost', port)
        sender.send(pid1.asString())

        then:
        db.patientIdExists(pid1)
        !db.patientIdExists(PidBuilder.createPid("xxx^^^&1.2&ISO"))  // random name to check for

        when:
        sleep(1*1000)
        ListenerFactory.terminate(simId1.id)

        then:
        true
    }

    def 'Listen and send 2'() {
        setup:
        String templateFile = getClass().getResource('/adt/A01.txt').file

        when: 'Start patientIdentityFeed'
        logger.info("generate patientIdentityFeed")
        ListenerFactory.generateListener(simId1.id, firstPort, new PifHandler());
        ThreadPoolItem item = ListenerFactory.getItem(simId1.id)
        def port = item.port

        then: 'Valid ThreadPoolItem created'
        item
        item.inUse
        !db.patientIdExists(pid1)

        when: 'Send A01'
        new AdtSender(templateFile, 'localhost', port).send(pid1.asString())

        then:
        db.patientIdExists(pid1)
        !db.patientIdExists(PidBuilder.createPid("xxx^^^&1.2&ISO"))  // random name to check for

        when: 'Send another A01'
        new AdtSender(templateFile, 'localhost', port).send(pid2.asString())

        then:
        db.patientIdExists(pid1)
        db.patientIdExists(pid2)

        when:
        sleep(1*1000)
        ListenerFactory.terminate(simId1.id)

        then:
        true
    }
}
