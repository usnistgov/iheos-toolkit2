package gov.nist.toolkit.xdstools2.patientIdentityFeed

import gov.nist.toolkit.actorfactory.PatientIdentityFeedServlet
import gov.nist.toolkit.actorfactory.PifHandler
import gov.nist.toolkit.simcommon.server.factories.RegistryActorFactory
import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.adt.AdtSender
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.adt.ThreadPoolItem
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimManager
import org.apache.log4j.Logger
import spock.lang.Specification

/**
 * Created by bill on 9/9/15.
 */
class PifITh extends Specification {
    static Logger logger = Logger.getLogger(PifITh.class);
    def simId1 = 'reg1'
    def pid1 = 'A1^^^&1.2.3&ISO'
    def pid2 = 'A2^^^&1.2.3&ISO'
    PifHandler pifHandler = new PifHandler()
    // these reflect toolkit.properties configuration (must)
    int firstPort = 5000
    int secondPort = 5001
    int lastPort = 5005
    Session session
    SimManager simManager
    String templateFile = getClass().getResource('/adt/A01.txt').file

    def setup() {
        session = UnitTestEnvironmentManager.setupLocalToolkit()
        simManager = new SimManager(session.getId())
        SimDb.deleteAllSims()
    }

    def 'Listener startup on Registry Sim creation'() {
        setup:
        PatientIdentityFeedServlet servlet = new PatientIdentityFeedServlet()

        when: 'starts no listeners - no Registry sims defined'
        servlet.initPatientIdentityFeed()

        then:
        ListenerFactory.getAllRunningListeners().isEmpty()
        !pifHandler.hasPatientId(simId1, pid1)

        when: 'Build new Registry simulator'
        RegistryActorFactory registryActorFactory = new RegistryActorFactory()
        Simulator sim = new RegistryActorFactory().buildNewSimulator(simManager, ActorType.REGISTRY, simId1, true)
//        Simulator sim = registryActorFactory.buildNew(simManager, simId1, true)
        ListenerFactory.getAllRunningListeners().each { ThreadPoolItem tpi ->
            println "Port ${tpi.port} running"
        }

        then: 'Should now have one listener'
        ListenerFactory.getAllRunningListeners().size() == 1

        when: 'Send A01'
        ThreadPoolItem item = ListenerFactory.getItem(simId1)
        def port = item.port
        AdtSender sender = new AdtSender(templateFile, 'localhost', port)
        sender.send(pid1)

        then: 'Verify patient id received and recorded'
        pifHandler.hasPatientId(simId1, pid1)
    }

}
