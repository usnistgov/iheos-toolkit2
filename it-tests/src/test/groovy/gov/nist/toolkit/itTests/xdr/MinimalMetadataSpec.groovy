package gov.nist.toolkit.itTests.xdr

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymsg.registry.RegistryError
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListParser
import gov.nist.toolkit.toolkitApi.BasicSimParameters
import gov.nist.toolkit.toolkitApi.DocumentRecipient
import gov.nist.toolkit.toolkitApi.DocumentSource
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.RawSendRequest
import gov.nist.toolkit.toolkitServicesCommon.RawSendResponse
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import gov.nist.toolkit.toolkitServicesCommon.resource.DocumentResource
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement
import spock.lang.Shared
/**
 *
 */
class MinimalMetadataSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi
    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    @Shared BasicSimParameters srcParams = new BasicSimParameters()
    @Shared BasicSimParameters recParams = new BasicSimParameters()

    DocumentSource documentSource
    DocumentRecipient documentRecipient

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        spi.delete(srcParams.id, srcParams.user)
        spi.delete(recParams.id, recParams.user)
        server.stop()
        ListenerFactory.terminateAll()
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

        // Delete and re-create document source
        spi.delete(srcParams.id, srcParams.user)
        documentSource = spi.createDocumentSource(
                srcParams.id,
                srcParams.user,
                srcParams.environmentName
        )

        // Delete and re-create document recipient
        spi.delete(recParams.id, recParams.user)
        documentRecipient = spi.createDocumentRecipient(
                recParams.id,
                recParams.user,
                recParams.environmentName
        )

        // set DOC REC endpoint into DOC SRC
        String endpoint = documentRecipient.asString(SimulatorProperties.pnrEndpoint)
//        endpoint = endpoint.replace('8889', '7777')
        documentSource.setProperty(SimulatorProperties.pnrEndpoint, endpoint)
        SimConfig updatedVersion = documentSource.update(documentSource.getConfig())
    }

    def 'send XDR with minimal metadata' () {
        RawSendRequest req = documentSource.newRawSendRequest()
        String minimalMetadataHeader = '<direct:metadata-level xmlns:direct="urn:direct:addressing">minimal</direct:metadata-level>'

        req.addExtraHeader(minimalMetadataHeader)
        req.metadata = this.getClass().getResource('/xdr/XdrMinimal.xml').text
        req.addDocument('Document01', new DocumentResource('text/plain', 'Hello World!'.bytes))

        when:
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
    // missing mimeType
    def 'send XDR with bad minimal metadata' () {
        RawSendRequest req = documentSource.newRawSendRequest()
        String minimalMetadataHeader = '<direct:metadata-level xmlns:direct="urn:direct:addressing">minimal</direct:metadata-level>'

        req.addExtraHeader(minimalMetadataHeader)
        req.metadata = this.getClass().getResource('/xdr/XdrMinimalBad.xml').text
        req.addDocument('Document01', new DocumentResource('text/plain', 'Hello World!'.bytes))

        when:
        RawSendResponse response = documentSource.sendProvideAndRegister(req)

        String responseSoapBody = response.responseSoapBody;
        OMElement responseEle = Util.parse_xml(responseSoapBody)
        RegistryErrorListParser rel = new RegistryErrorListParser(responseEle)
        List<RegistryError> errors = rel.registryErrorList
        errors.each { RegistryError err ->
            println err.codeContext
        }

        then:
        errors.size() == 1
        errors.get(0).codeContext.contains('mimeType attribute missing')
    }

}
