package gov.nist.toolkit.sdkTest
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.simulators.servlet.SimServlet
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import org.glassfish.grizzly.servlet.ServletRegistration
import org.glassfish.grizzly.servlet.WebappContext
import spock.lang.Shared
import spock.lang.Specification
/**
 *
 */
class XdsTest extends Specification {
    @Shared String port = '8889'
    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", port)
    SimulatorBuilder builder = new SimulatorBuilder(urlRoot)
    @Shared GrizzlyController server
    BasicSimParameters srcParams = new BasicSimParameters()
    BasicSimParameters recParams = new BasicSimParameters()

    def setupGrizzly() {
        server = new GrizzlyController()
        server.start(port);
    }

    def loadAxis2() {
        File axis2 = new File(getClass().getResource('/axis2.xml').file)
        System.getProperties().setProperty('axis2.xml', axis2.toString())
        System.getProperties().setProperty('axis2.repo', axis2.parentFile.toString())
    }

    def initializeSimServlet() {
        final WebappContext tools2 = new WebappContext("xdstools2","")
        final ServletRegistration sims = tools2.addServlet("xdstools2",new SimServlet());
        sims.addMapping('/xdstools2/sim/*')
        tools2.deploy(server.getHttpServer())
    }

    def setupSpec() {   // one time setup done when class launched
        UnitTestEnvironmentManager.setupLocalToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
        loadAxis2()
        initializeSimServlet()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
    }

    def setup() {  // run before each test method
        srcParams.id = 'source'
        srcParams.user = 'mike'
        srcParams.actorType = SimulatorActorType.DOCUMENT_SOURCE
        srcParams.environmentName = 'test'

        recParams.id = 'recipient'
        recParams.user = 'mike'
        recParams.actorType = SimulatorActorType.DOCUMENT_RECIPIENT
        recParams.environmentName = 'test'
    }

}
