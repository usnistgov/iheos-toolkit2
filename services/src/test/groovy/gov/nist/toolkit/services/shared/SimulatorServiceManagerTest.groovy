package gov.nist.toolkit.services.shared

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.BadSimIdException
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

    def setup() {
        mgr.deleteConfig(new SimId('bill', 'aa'))
        mgr.deleteConfig(new SimId('bill', 'AA'))
    }

    def 'user with __ in name is illegal'() {
        when:
        new SimId("foo__bar", "x")

        then:
        thrown BadSimIdException
    }

    def 'id with __ in name is ok'() {
        when:
        SimId simId = new SimId("xx", "xx__yy")

        then:
        notThrown BadSimIdException
        simId.user == 'xx'
        simId.id == 'xx__yy'
    }

    def 'id with __ in name is ok 2'() {
        when:
        SimId simId = new SimId("xx__yy__zz")

        then:
        notThrown BadSimIdException
        simId.user == 'xx'
        simId.id == 'yy__zz'
    }

    def 'user with / in name is illegal'() {
        when:
        SimId simId = new SimId("x/y__x")

        then:
        thrown BadSimIdException
    }

    def 'id with / in name is illegal'() {
        when:
        SimId simId = new SimId("xy__x/y")

        then:
        thrown BadSimIdException
    }

    def 'user with . in name is translated'() {
        when:
        SimId simId = new SimId("x.y", "z")

        then:
        notThrown BadSimIdException
        simId.user == 'x_y'
        simId.id == 'z'
    }

    def 'user with ._ is illegal'() {
        when:
        new SimId('x._y', 'y')

        then:
        thrown BadSimIdException
    }

    def 'id with . is translated'() {
        when:
        SimId simId = new SimId('x__y.z')

        then:
        simId.user == 'x'
        simId.id == 'y_z'
    }

    def 'user with upper case is translated'() {
        when:
        SimId simId = new SimId('HI__bill')

        then:
        simId.user == 'hi'
        simId.id == 'bill'
    }

    def 'id with upper case is translated'() {
        when:
        SimId simId = new SimId('HI__BIll')

        then:
        simId.user == 'hi'
        simId.id == 'bill'
    }

    def 'delete sim'() {
        setup:
        println "External Cache is " + Installation.installation().externalCache()

        when:
        SimId simId = new SimId('bill', 'aa')
        mgr.deleteConfig(simId)

        then:
        !new SimDb().exists(simId)
    }

    def 'Create simulator with lower case name'() {
        setup:
        println "External Cache is " + Installation.installation().externalCache()

        when:
        SimId simId = new SimId('bill', 'aa')
        mgr.deleteConfig(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        true
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }

    def 'Create simulator with upper case name'() {
        setup:
        println "External Cache is " + Installation.installation().externalCache()

        when:
        SimId simId = new SimId('bill', 'AA')
        mgr.deleteConfig(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }
}
