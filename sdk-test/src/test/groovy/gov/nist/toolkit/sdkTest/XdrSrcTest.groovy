package gov.nist.toolkit.sdkTest

import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actortransaction.SimulatorActorType
import gov.nist.toolkit.registrymsg.registry.RegistryError
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListParser
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.TestSession
import gov.nist.toolkit.simulators.servlet.SimServlet
import gov.nist.toolkit.tookitApi.*
import gov.nist.toolkit.toolkitServicesCommon.*
import gov.nist.toolkit.transactionNotificationService.TransactionLog
import gov.nist.toolkit.transactionNotificationService.TransactionNotification
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
class XdrSrcTest extends Specification implements TransactionNotification {
    def host='localhost'
    @Shared String port = '8889'
    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", port)
//    EngineSpi engine = new EngineSpi(host, port);
    SimulatorBuilder builder = new SimulatorBuilder(urlRoot)
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
        srcParams.actorType = SimulatorActorType.DOCUMENT_SOURCE
        srcParams.environmentName = 'test'

        recParams.id = 'recipient'
        recParams.user = 'mike'
        recParams.actorType = SimulatorActorType.DOCUMENT_RECIPIENT
        recParams.environmentName = 'test'
    }

    def 'Create DocSrc'() {
        when:
        println 'STEP - DELETE DOCSRC SIM'
        builder.delete(srcParams.id, srcParams.user)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        SimId simId = builder.create(
                srcParams.id,
                srcParams.user,
                srcParams.actorType,
                srcParams.environmentName
        )

        then: 'verify sim built'
        simId.getId() == srcParams.id
    }

    def 'Send XDR'() {
        when:
        println 'STEP - DELETE DOCREC SIM'
        builder.delete(recParams.id, recParams.user)

        and:
        println 'STEP - CREATE DOCREC SIM'
        DocumentRecipient documentRecipient = builder.createDocumentRecipient(
                recParams.id,
                recParams.user,
                recParams.environmentName
        )

        and:  'This is un-verifiable since notifications are handled through the servlet filter chain which is not configured here'
        println 'STEP - UPDATE - REGISTER NOTIFICATION'
        documentRecipient.setProperty(SimulatorProperties.TRANSACTION_NOTIFICATION_URI, urlRoot + '/rest/toolkitcallback')
        documentRecipient.setProperty(SimulatorProperties.TRANSACTION_NOTIFICATION_CLASS, 'gov.nist.toolkit.sdkTest.XdrSrcTest')
        SimConfig withRegistration = documentRecipient.update(documentRecipient.getConfig())
        println "Updated Src Sim config is ${withRegistration.describe()}"

        then: 'verify sim built'
        documentRecipient.getId() == recParams.id

        when:
        println 'STEP - DELETE DOCSRC SIM'
        builder.delete(srcParams.id, srcParams.user)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        DocumentSource documentSource = builder.createDocumentSource(
                srcParams.id,
                srcParams.user,
                srcParams.environmentName
        )

        then: 'verify sim built'
        documentSource.getId() == srcParams.id

        when:
        println 'STEP - UPDATE - SET DOC REC ENDPOINTS INTO DOC SRC'
        documentSource.setProperty(SimulatorProperties.pnrEndpoint, documentRecipient.asString(SimulatorProperties.pnrEndpoint))
        SimConfig updatedVersion = documentSource.update(documentSource.getConfig())
        println "Updated Src Sim config is ${updatedVersion.describe()}"

        then:
        updatedVersion

        when:
        println 'STEP - SEND XDR'
        RawSendRequest req = documentSource.newRawSendRequest()

        req.metadata = this.getClass().getResource('/testdata/PnR1Doc.xml').text
        req.addDocument('Document01', new Document('text/plain', 'Hello World!'.bytes))

        RawSendResponse response = documentSource.sendProvideAndRegister(req)

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



    def 'Try to reate sim with bad id'() {
        when:
        SimConfig recSimConfig = builder.create(
                'has spaces',
                recParams.user,
                recParams.actorType,
                recParams.environmentName
        )

        then:
        ToolkitServiceException e = thrown()
        e.code == 500
    }

    @Override
    void notify(TransactionLog log) {
        println "NOTIFICATION RECEIVED"
    }
}
