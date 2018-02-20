package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.fhir.simulators.support.StoredDocument
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import spock.lang.Shared

import java.nio.file.Path

/**
 * Test DsSimCommon methods for images
 * Created by rmoult01 on 3/13/17.
 */
class DsSimCommonSpec extends ToolkitSpecification {

    @Shared SimulatorBuilder spi
    @Shared String testSession = 'dssctest'
    @Shared String id = 'simulator_ids'
    @Shared SimId simId = new SimId(new TestSession(testSession), id)
    @Shared String actor = "ids"
    @Shared String transaction = "ids.ret"
    @Shared String imageCache

    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)
    /**
     * Run once at class initialization.
     * @return
     */
    def setupSpec() {
        // Opens a grizzly server for testing, in lieu of tomcat
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)

        imageCache = Installation.instance().imageCache("sim").absolutePath
    }

    // one time shutdown when everything is done
    def cleanupSpec() {
        api.deleteSimulatorIfItExists(simId)
//        server.stop()
//        ListenerFactory.terminateAll()
    }

    def setup() {
        api.createTestSession(testSession)
        createIdsSimulator()
    }

    def 'addImagingDocumentAttachments method test' () {
        println(testDesc)

        when: 'We create a DsSimCommon instance'

        SimDb db = Mock(SimDb)
        ValidationContext vc = Mock(ValidationContext)
        SimulatorConfig simConfig = api.getConfig(simId)
        SimCommon common = Mock(SimCommon, constructorArgs: [db, simConfig, false, vc, null, null, null])
        DsSimCommon dsSimCommon = new DsSimCommon(common, null, null, new MessageValidatorEngine())
        dsSimCommon.setSimulatorConfig(simConfig)
        ErrorRecorder er = Mock(ErrorRecorder)

        then: 'It exists'
        dsSimCommon

        when: 'Pass a list of composite Uids and transfer Syntax UIDs'
        dsSimCommon.addImagingDocumentAttachments(imagingDocumentUids, transferSyntaxUids, er)
        Collection<StoredDocument> docs = dsSimCommon.getAttachments()

        then: 'A document collection was returned'
        docs

        and: 'The count is what you expect'
        docs.size() == count

        and: 'All the images you were supposed to find you found'
        for (String img : found) {
            StoredDocument doc = null
            for(StoredDocument d : docs) {
                if (d.uid == img) {
                    doc = d
                    break
                }
            }
            assert doc : "Document with UID $img expected, not found"

            // validate file path
            String compositeUid = null
            for (String uid : imagingDocumentUids) {
                if (uid.endsWith(img)) {
                    compositeUid = uid
                    break
                }
            }
            assert compositeUid : "Internal test error. Image $img in found, not in imagineDocumentUids"
            String[] uids = compositeUid.split(":")
            Path path = java.nio.file.Paths.get(imageCache, simConfig.get(SimulatorProperties.idsImageCache).asString(), uids[0], uids[1], uids[2])
            String documentpfn = doc.pathToDocument.getAbsolutePath()
            String expectedpfn = path.toString()
            assert documentpfn.startsWith(expectedpfn) : "expected $expectedpfn, found $documentpfn"
            String pfn = doc.pathToDocument.getName()
            assert pfn in transferSyntaxUids : "Transfer Syntax UID $pfn not in Request"

            // Validate other Document fields
            assert doc.mimeType == 'application/dicom' : "img $img has invalid mime type [$doc.mimeType]"
            assert doc.charset == 'UTF-8' : "img $img has invalid character set [$doc.charset]"

        }
        for (String img : notFound) {
            StoredDocument doc = null
            for (StoredDocument d : docs) {
                if (d.uid == img) {
                    doc = d
                    break
                }
            }
            assert !doc: "Document with UID $img not expected, but found"
        }


        where: 'Tests to run'
        testDesc | imagingDocumentUids | transferSyntaxUids || count | found | notFound
        'Simple Test' |
        ['1.3.6.1.4.1.21367.201599.1.201604020954048:1.3.6.1.4.1.21367.201599.2.201604020954048:1.3.6.1.4.1.21367.201599.3.201604020954048.1'] |
        ['1.2.840.10008.1.2.1'] |
        1 | ['1.3.6.1.4.1.21367.201599.3.201604020954048.1'] |
        []

    }

    /**
     * Re/creates an IDS simulator with overrides for specific configuration
     * parameters needed for testing
     */
    void createIdsSimulator() {
        List<SimulatorConfigElement> elements = [
                new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.80.110"),
                new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-repository")]
        api.deleteSimulatorIfItExists(simId)
        SimulatorConfig conf = api.createSimulator(ActorType.IMAGING_DOC_SOURCE, simId).getConfig(0)
        for (SimulatorConfigElement chg : elements) {
            chg.setEditable(true);
            conf.replace(chg);
        }
        api.saveSimulator(conf);
    }

}
