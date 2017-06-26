package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.toolkitApi.DocumentRegRep
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.DocumentContent
import gov.nist.toolkit.toolkitServicesCommon.RefList
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Test Registry API for direct retrieval of DocumentEntry, Document, and evnet log
 */
class GetDocumentDetailsSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')  // start toolkit on port 8889

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def patientId = 'br14^^^&1.2.3&ISO'
    def userName = 'bill'
    def environmentName = 'test'

    def 'test'() {
        when: 'build reg/rep'
        spi.delete('reg', userName)
        DocumentRegRep regRep = spi.createDocumentRegRep('reg', userName, environmentName)
        SimConfig regrepSimConfig = spi.get(regRep)


        // disable checking of Patient Identity Feed
        regrepSimConfig.setProperty(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, false)
        regrepSimConfig = spi.update(regrepSimConfig)

        // load the reg/rep with two documents
        TestInstance testId = new TestInstance("12318")
        List<String> sections = null
        Map<String, String> qparams = new HashMap<>()
        qparams.put('$patientid$', patientId)

        List<Result> results = api.runTest(userName, regRep.getFullId(), testId, sections, qparams, true)

        then:
        results.get(0).passed()

        when: 'query to get just references'
        RefList objectRefList = regRep.findDocumentsForPatientID(patientId)
        println objectRefList.getRefs()

        then:
        objectRefList.getRefs().size() == 2

        when: 'get first DE details'
        String deString = regRep.getDocEntry(objectRefList.getRefs().get(0))
        println "Full Metadata:"
        println deString

        then:
        deString
        deString?.trim()?.startsWith('<')

        when: 'get event ids for the register transactions this simulator'
        RefList eventIds = regRep.getEventIds(regRep.getFullId(), TransactionType.REGISTER)
        println 'SimResource id: ' + eventIds.refs.get(0)

        then:
        eventIds.refs.size() == 1

        when: 'retrieve event'
        RefList events = regRep.getEvent(regRep.getFullId(), TransactionType.REGISTER, eventIds.refs.get(0))
        println "SimResource: " + events.refs.get(0)

        then:
        events.refs.size() == 1

        when: 'parse returned metadata'
        Metadata m = MetadataParser.parseNonSubmission(deString)

        then: 'verify it contains one DocumentEntry model'
        m.getExtrinsicObjects().size() == 1

        when: 'extract uniqueId for one DocumentEntry'
        String uniqueId = m.getUniqueIdValue(m.getExtrinsicObject(0))
        println "uniqueId is " + uniqueId

        then: 'verify has a value'
        uniqueId

        when: 'retrieve document'
        DocumentContent documentContent = regRep.getDocument(uniqueId)

        then:
        documentContent.uniqueId == uniqueId

        when: 'display document'
        println new String(documentContent.content)

        then:
        true
    }
}
