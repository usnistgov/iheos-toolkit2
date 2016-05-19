package gov.nist.toolkit.itTests.sim

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.EndpointParser
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.simulators.servlet.ReconfigureSimulators
import spock.lang.Shared
import spock.lang.Specification
/**
 * Test updating a simulator with new host:port
 */
class ReconfigureSpec extends Specification {
    @Shared ToolkitApi api = ToolkitApi.forInternalUse()
    @Shared String retrieveEndpoint
    SimId simId
    SimulatorConfig config

    def setup() {
        simId = new SimId('bill', 'mysim', ActorType.REPOSITORY.name)
        api.deleteSimulatorIfItExists(simId)
        Simulator sim = api.createSimulator(simId)
        config = sim.getConfig(0)
        retrieveEndpoint = config.getConfigEle(SimulatorProperties.retrieveEndpoint).asString()
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

    def 'update endpoint and confirm'() {
        when:
        ReconfigureSimulators rs = new ReconfigureSimulators()
        rs.init(null)
        rs.setOverrideHost('home')
        rs.setOverridePort('42')
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
}
