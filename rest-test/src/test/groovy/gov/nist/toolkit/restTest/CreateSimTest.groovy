package gov.nist.toolkit.restTest

import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.tookitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfigBean
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import org.glassfish.grizzly.http.server.HttpServer
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 *
 */
class CreateSimTest extends Specification {
    def host='localhost'
    def port = '8888'
    SimulatorBuilder builder = new SimulatorBuilder(host, port);
    private HttpServer server
    private WebTarget target
    BasicSimParameters params = new BasicSimParameters();

    def setupGrizzly() {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    def setup() {
        TestSession.setupToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
        params.id = 'reg'
        params.user = 'mike'
        params.actorType = 'reg'
        params.environmentName = 'NA2015'
    }

    def 'Get Unknown SimId'() {
        when:
       builder.getSimConfig(ToolkitFactory.newSimId('foo', 'bar', 'reg', 'default'))

        then:
        ToolkitServiceException e = thrown()
        e.code == 404

    }

    // Create a simulator and retrieve all its parameters and settings
    def 'Get SimBean'() {
        when: 'Delete sim in case it exists'
        println 'STEP - DELETE SIM'
        builder.delete(params)

        and: 'Create new sim'
        println 'STEP - CREATE NEW SIM'
        SimId simId = builder.create(params)

        then: 'verify sim built'
        simId.getId() == 'reg'

        when: 'retrieve full configuration'
        println 'STEP - RETRIEVE FULL CONFIGURATION'
        SimConfigBean config = builder.getSimConfig(simId)
        println "Received " + config

        then: 'verify configuration'
        simId.getId() == config.getId()
        config.asString('Name') == 'mike__reg'
    }

    def 'Put SimId'() {
        given: 'This builds sim with default parameters'

        when:
        SimId simId = builder.create(params)

        then:
        simId.getId() == 'reg'
    }

    def 'Delete SimId'() {
        when:
        builder.delete('reg', 'mike')

        then: true
    }

}
