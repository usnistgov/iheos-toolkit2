package gov.nist.toolkit.sdkTest
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.tookitApi.ToolkitServiceException
import gov.nist.toolkit.toolkitServicesCommon.SimConfigResource
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.ToolkitFactory
import org.glassfish.grizzly.http.server.HttpServer
import spock.lang.Specification

import javax.ws.rs.core.Response
/**
 *
 */
class CreateSimTest extends Specification {
    def host='localhost'
    def port = '8888'
    SimulatorBuilder builder = new SimulatorBuilder(host, port);
    HttpServer server
    BasicSimParameters params = new BasicSimParameters();

    def setupGrizzly() {
        server = Main.startServer();
    }

    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
    }

    def setup() {  // run before each test method
        params.id = 'reg'
        params.user = 'mike'
        params.actorType = 'reg'
        params.environmentName = 'NA2015'
    }

    def 'Create sim with invalid config'() {
        when:
        params.id = ''
        builder.create(params)

        then:
        ToolkitServiceException e = thrown()
        e.code == Response.Status.BAD_REQUEST.statusCode
    }

    def 'Get Unknown SimId'() {
        when:
       builder.get(ToolkitFactory.newSimId('foo', 'bar', 'reg', 'default'))

        then:
        ToolkitServiceException e = thrown()
        e.code == Response.Status.NOT_FOUND.statusCode
    }

    def 'Delete unknown Sim'() {
        when:
        params.id = 'foofoo'
        builder.delete(params)

        then:
        ToolkitServiceException e = thrown()
        println e
        e.code == Response.Status.NOT_FOUND.statusCode
    }

    // Create a simulator and retrieve all its parameters and settings
    def 'Create/Retrieve Sim'() {
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
        SimConfigResource config = builder.get(simId)

        then: 'verify configuration'
        simId.getId() == config.getId()
        config.asString('Name') == 'mike__reg'
    }

    static final private parmName = "Validate_Codes"
//    def 'Update sim config'() {
//        when: 'Delete sim in case it exists'
//        println 'STEP - DELETE SIM'
//        builder.delete(params)
//
//        and: 'Create new sim'
//        println 'STEP - CREATE NEW SIM'
//        SimConfigResource config = builder.create(params)
//        println config.describe()
//
//        then: 'verify sim built'
//        config.getId() == 'reg'
//
//        when: 'Update Validate_Codes to false'
//        config.setProperty(parmName, 'false')
//        builder.update(config)
//
//        and: 'Get fresh copy of resource'
//        SimConfigResource updatedConfig = builder.get(config) // extends SimId
//
//        then: 'Verify value updated'
//        updatedConfig.asString(parmName) == 'false'
//    }

}
