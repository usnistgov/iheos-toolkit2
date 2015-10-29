package gov.nist.toolkit.sdkTest

import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.registrymsg.registry.RegistryError
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListParser
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.simulators.servlet.SimServlet
import gov.nist.toolkit.tookitApi.BasicSimParameters
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.*
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.servlet.ServletRegistration
import org.glassfish.grizzly.servlet.WebappContext
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 */
class XdrSrcTest extends Specification {
    def host='localhost'
    @Shared String port = '8889'
    SimulatorBuilder builder = new SimulatorBuilder(host, port);
    @Shared HttpServer server
    BasicSimParameters srcParams = new BasicSimParameters()
    BasicSimParameters recParams = new BasicSimParameters()

    def setupGrizzly() {
        server = Main.startServer(port);
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
        tools2.deploy(server)
    }

    def setupSpec() {   // one time setup done when class launched
        TestSession.setupToolkit()
        ToolkitApi.forServiceUse()
        setupGrizzly()
        loadAxis2()
        initializeSimServlet()
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.shutdownNow()
    }

    def setup() {  // run before each test method
        srcParams.id = 'source'
        srcParams.user = 'mike'
        srcParams.actorType = 'xdrsrc'
        srcParams.environmentName = 'test'

        recParams.id = 'recipient'
        recParams.user = 'mike'
        recParams.actorType = 'rec'
        recParams.environmentName = 'test'
    }

    def 'Create DocSrc'() {
        when:
        println 'STEP - DELETE DOCSRC SIM'
        builder.delete(srcParams)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        SimId simId = builder.create(srcParams)

        then: 'verify sim built'
        simId.getId() == srcParams.id
    }

    def 'Send XDR'() {
        when:
        println 'STEP - DELETE DOCREC SIM'
        builder.delete(recParams)

        and:
        println 'STEP - CREATE DOCREC SIM'
        SimConfig recSimConfig = builder.create(recParams)

        then: 'verify sim built'
        recSimConfig.getId() == recParams.id

        when:
        println 'STEP - DELETE DOCSRC SIM'
        builder.delete(srcParams)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        SimConfig srcSimConfig = builder.create(srcParams)

        then: 'verify sim built'
        srcSimConfig.getId() == srcParams.id

        when:
        println 'STEP - UPDATE SET DOC REC ENDPOINTS INTO DOC SRC'
        srcSimConfig.setProperty(SimulatorProperties.pnrEndpoint, recSimConfig.asString(SimulatorProperties.pnrEndpoint))
        SimConfig updatedVersion = builder.update(srcSimConfig)
        println "Updated Src Sim config is ${updatedVersion.describe()}"

        then:
        updatedVersion

        when:
        println 'STEP - SEND XDR'
        SendRequestResource req = new SendRequestResource()
        req.id = recParams.id
        req.user = recParams.user
        req.transactionName = 'xdrpr'
        req.metadata = this.getClass().getResource('/testdata/PnR1Doc.xml').text
        req.addDocument('Document01', new Document('text/plain', 'Hello World!'.bytes))

        SendResponseResource response = builder.sendXdr(req)

        String responseSoapBody = response.responseSoapBody;
        OMElement responseEle = Util.parse_xml(responseSoapBody)
        RegistryErrorListParser rel = new RegistryErrorListParser(responseEle)
        List<RegistryError> errors = rel.registryErrorList
        errors.each { RegistryError err ->
            println err.codeContext
        }

        then:
        errors.size() == 0
    }

}
