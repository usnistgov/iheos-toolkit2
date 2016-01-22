package gov.nist.toolkit.services.shared

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.session.server.Session
import spock.lang.Specification

/**
 *
 */
class SimulatorServiceManagerTest extends Specification {
    Session session = new Session(Installation.installation().warHome(), Installation.defaultSessionName())
    SimulatorServiceManager mgr = new SimulatorServiceManager(session)

    def 'Create simulator with lower case name'() {
        when:
        SimId simId = new SimId('bill', 'aa')
        mgr.deleteConfig(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }

    def 'Create simulator with upper case name'() {
        when:
        SimId simId = new SimId('bill', 'AA')
        mgr.deleteConfig(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        sim.ids.size() == 1
        sim.ids.get(0).id == 'AA'
    }
}
