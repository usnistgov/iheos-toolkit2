package gov.nist.toolkit.services.server

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import spock.lang.Specification

import java.nio.file.Paths

/**
 *
 */
class SimulatorServiceManagerTest extends Specification {
    Session session = new Session(Installation.instance().warHome(), Installation.defaultSessionName())
    SimulatorServiceManager mgr = new SimulatorServiceManager(session)
    def environment = 'default'

    def setupSpec() {
        ExternalCacheManager.initializeFromMarkerFile(Paths.get(this.getClass().getResource('/').toURI()).resolve('external_cache/external_cache.txt').toFile())
    }

    def setup() {
        mgr.delete(SimIdFactory.simIdBuilder('bill__aa'))
        mgr.delete(SimIdFactory.simIdBuilder('bill__AA'))
    }

    def 'user with __ in name is illegal'() {
        when:
        SimIdFactory.simIdBuilder("foo__bar__x")

        then:
        thrown ToolkitRuntimeException
    }

    def 'no test session is illegal'() {
        when:
        SimIdFactory.simIdBuilder("x")

        then:
        thrown ToolkitRuntimeException
    }

    def 'id with __ in name is not ok'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder("xx__xx__yy")

        then:
        thrown ToolkitRuntimeException
    }


    def 'user with / in name is illegal'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder("x/y__x")

        then:
        thrown BadSimIdException
    }

    def 'id with / in name is illegal'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder("xy__x/y")

        then:
        thrown BadSimIdException
    }

    def 'user with . in name is translated'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder("x.y__z")

        then:
        simId.testSession.value == 'x_y'
        simId.id == 'z'
    }

    def 'user with ._ is illegal'() {
        when:
        SimIdFactory.simIdBuilder('x._y__y')

        then:
        thrown BadSimIdException
    }

    def 'id with . is translated'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder('x__y.z')

        then:
        simId.testSession.value == 'x'
        simId.id == 'y_z'
    }

    def 'user with upper case is translated'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder('HI__bill')

        then:
        simId.testSession.value == 'hi'
        simId.id == 'bill'
    }

    def 'id with upper case is translated'() {
        when:
        SimId simId = SimIdFactory.simIdBuilder('HI__BIll')

        then:
        simId.testSession.value == 'hi'
        simId.id == 'bill'
    }

    def 'delete sim'() {
        setup:
        println "External Cache is " + Installation.instance().externalCache()

        when:
        SimId simId = SimIdFactory.simIdBuilder('bill__aa')
        mgr.delete(simId)

        then:
        !SimDb.exists(simId)
    }

    def 'Create simulator with lower case name'() {
        setup:
        println "External Cache is " + Installation.instance().externalCache()

        when:
        SimId simId = SimIdFactory.simIdBuilder('bill__aa')
        mgr.delete(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId, environment)

        then:
        true
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }

    def 'Create simulator with upper case name'() {
        setup:
        println "External Cache is " + Installation.instance().externalCache()

        when:
        SimId simId = SimIdFactory.simIdBuilder('bill__AA')
        mgr.delete(simId)
        Simulator sim = mgr.getNewSimulator(ActorType.REPOSITORY.name, simId, environment)

        then:
        sim.ids.size() == 1
        sim.ids.get(0).id == 'aa'
    }
}
