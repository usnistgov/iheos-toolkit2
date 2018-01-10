package gov.nist.toolkit.services.shared

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.installation.ExternalCacheManager
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Specification

/**
 *
 */
class SimulatorServiceManagerTest extends Specification {
    Session session = new Session(Installation.instance().warHome(), Installation.defaultSessionName())
    SimulatorServiceManager mgr = new SimulatorServiceManager(session)

    def setupSpec() {
        ExternalCacheManager.initializeFromMarkerFile(new File(this.getClass().getResource('/external_cache/external_cache.txt').file))
    }

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
        simId.testSession == 'xx'
        simId.id == 'xx__yy'
    }

    def 'id with __ in name is ok 2'() {
        when:
        SimId simId = new SimId("xx__yy__zz")

        then:
        notThrown BadSimIdException
        simId.testSession == 'xx'
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
        simId.testSession == 'x_y'
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
        simId.testSession == 'x'
        simId.id == 'y_z'
    }

    def 'user with upper case is translated'() {
        when:
        SimId simId = new SimId('HI__bill')

        then:
        simId.testSession == 'hi'
        simId.id == 'bill'
    }

    def 'id with upper case is translated'() {
        when:
        SimId simId = new SimId('HI__BIll')

        then:
        simId.testSession == 'hi'
        simId.id == 'bill'
    }

    def 'delete sim'() {
        setup:
        println "External Cache is " + Installation.instance().externalCache()

        when:
        SimId simId = new SimId('bill', 'aa')
        mgr.deleteConfig(simId)

        then:
        !SimDb.exists(simId)
    }

    def 'Create simulator with lower case name'() {
        setup:
        println "External Cache is " + Installation.instance().externalCache()

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
        println "External Cache is " + Installation.instance().externalCache()

        when:
        SimId simId = new SimId('bill', 'AA')
        mgr.deleteConfig(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId)

        then:
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }
}
