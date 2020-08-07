package gov.nist.toolkit.itTests.xdr

import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymsg.registry.RegistryError
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListParser
import gov.nist.toolkit.toolkitApi.*
import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.SimId
import gov.nist.toolkit.toolkitServicesCommon.resource.DocumentResource
import gov.nist.toolkit.transactionNotificationService.TransactionLog
import gov.nist.toolkit.transactionNotificationService.TransactionNotification
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import spock.lang.Shared

import java.nio.file.Paths

/**
 *
 */
class XdrSrcSpec extends ToolkitSpecification implements TransactionNotification {
    @Shared SimulatorBuilder spi



    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared BasicSimParameters srcParams = new BasicSimParameters()
    @Shared BasicSimParameters recParams = new BasicSimParameters()

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        spi.delete(srcParams.id, srcParams.user)
        spi.delete(recParams.id, recParams.user)
//        server.stop()
//        ListenerFactory.terminateAll()
    }

    def setup() {  // run before each test method
        srcParams.id = 'source'
        srcParams.user = 'bill'
        srcParams.actorType = SimulatorActorType.DOCUMENT_SOURCE
        srcParams.environmentName = 'test'

        recParams.id = 'recipient'
        recParams.user = 'bill'
        recParams.actorType = SimulatorActorType.DOCUMENT_RECIPIENT
        recParams.environmentName = 'test'
    }

    def 'Create DocSrc'() {
        when:
        println 'STEP - DELETE DOCSRC SIM'
        spi.delete(srcParams)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        SimId simId = spi.create(srcParams)

        then: 'verify sim built'
        simId.getId() == srcParams.id
    }

    def 'Send XDR'() {
        when:
        println 'STEP - DELETE DOCREC SIM'
        spi.delete(recParams)

        and:
        println 'STEP - CREATE DOCREC SIM'
        DocumentRecipient documentRecipient = spi.createDocumentRecipient(
                recParams.id,
                recParams.user,
                recParams.environmentName
        )

        and:  'This is un-verifiable since notifications are handled through the servlet validate chain which is not configured here'
        println 'STEP - UPDATE - REGISTER NOTIFICATION'
        documentRecipient.setProperty(SimulatorProperties.TRANSACTION_NOTIFICATION_URI, urlRoot + '/rest/toolkitcallback')
        documentRecipient.setProperty(SimulatorProperties.TRANSACTION_NOTIFICATION_CLASS, 'gov.nist.toolkit.itTests.xdr.XdrSrcTest')
        SimConfig withRegistration = documentRecipient.update(documentRecipient.getConfig())
        println "Updated Src Sim config is ${withRegistration.describe()}"

        then: 'verify sim built'
        documentRecipient.getId() == recParams.id

        when:
        println 'STEP - DELETE DOCSRC SIM'
        spi.delete(srcParams.id, srcParams.user)

        and:
        println 'STEP - CREATE DOCSRC SIM'
        DocumentSource documentSource = spi.createDocumentSource(
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

        req.metadata = Paths.get(this.getClass().getResource('/').toURI()).resolve('testdata/PnR1Doc.xml').toFile().text
        req.addDocument('Document01', new DocumentResource('text/plain', 'Hello World!'.bytes))

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
        SimConfig recSimConfig = spi.create(
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
