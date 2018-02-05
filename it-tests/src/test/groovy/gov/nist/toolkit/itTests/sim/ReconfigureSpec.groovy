package gov.nist.toolkit.itTests.sim

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.simulators.servlet.ReconfigureSimulators
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import org.apache.log4j.BasicConfigurator
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test updating a simulator with new host:port
 */
class ReconfigureSpec extends Specification {
    @Shared ToolkitApi api = new ToolkitApi(UnitTestEnvironmentManager.setupLocalToolkit())
    @Shared String retrieveEndpoint
    SimId simId
    SimulatorConfig config

    def setupSpec() {
        BasicConfigurator.configure()
    }

    def setup() {
        simId = new SimId(new TestSession('bill'), 'mysim', ActorType.REPOSITORY.name)
        api.deleteSimulatorIfItExists(simId)
        Simulator sim = api.createSimulator(simId)
        config = sim.getConfig(0)
        retrieveEndpoint = config.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()
    }

    def cleanup() {
        api.deleteSimulatorIfItExists(simId)
    }

    def 'confirm original config'() {
        when:
        EndpointParser ep = new EndpointParser(retrieveEndpoint)
        boolean validation = ep.validate()
        println ep.getError()

        then:
        validation

        when:
        String host = ep.getHost()
        String port = ep.getPort()

        then:
        host == 'localhost'
        port == '8889'
    }

    def 'update endpoint and confirm' () {
        when:
        ReconfigureSimulators rs = new ReconfigureSimulators()
        rs.setOverrideHost('home')
        rs.setOverridePort('42')
        rs.init(null)
        rs.reconfigure(simId)

        and:
        SimulatorConfig config2 = api.getConfig(simId)
        String retrieveEndpoint2 = config2.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()
        EndpointParser ep = new EndpointParser(retrieveEndpoint2)
        String host = ep.getHost()
        String port = ep.getPort()

        then:
        host == 'home'
        port == '42'
    }


    def 'update endpoint and service and confirm' () {
        when:
        ReconfigureSimulators rs = new ReconfigureSimulators()
        rs.setOverrideHost('home')
        rs.setOverridePort('42')
        Installation.instance().setServletContextName('toolkit45')
        rs.init(null)
        rs.reconfigure(simId)

        and:
        SimulatorConfig config2 = api.getConfig(simId)
        String retrieveEndpoint2 = config2.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()
        EndpointParser ep = new EndpointParser(retrieveEndpoint2)
        println "Updated endpoint is ${ep.endpoint}"
        String host = ep.getHost()
        String port = ep.getPort()
        String context = ep.context

        then:
        host == 'home'
        port == '42'
        context == 'toolkit45'
    }

    def 'empty service' () {
        when:
        ReconfigureSimulators rs = new ReconfigureSimulators()
        rs.setOverrideHost('home')
        rs.setOverridePort('42')
        Installation.instance().setServletContextName('')
        rs.init(null)
        rs.reconfigure(simId)

        and:
        SimulatorConfig config2 = api.getConfig(simId)
        String retrieveEndpoint2 = config2.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()
        EndpointParser ep = new EndpointParser(retrieveEndpoint2)
        String host = ep.getHost()
        String port = ep.getPort()
        String context = ep.context

        then:
        host == 'home'
        port == '42'
        context == ''
        retrieveEndpoint2 == 'http://home:42/sim/bill__mysim/rep/ret'
    }
}
