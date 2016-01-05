package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actorfactory.SimCache
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.tookitApi.DocumentRegRep
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServices.ToolkitFactory
import gov.nist.toolkit.toolkitServicesCommon.ObjectRefList
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Test Registry API for direct retrieval of DocumentEntry references and details.
 */
class GetDocumentDetailsSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

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

        // this expects full server version of simulator config
        // this call makes the configuration available as a site for the test client
        SimCache.addToSession(Installation.defaultSessionName(),  ToolkitFactory.asSimulatorConfig(regrepSimConfig))

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
        ObjectRefList objectRefList = regRep.findDocumentsForPatientID(patientId)
        println objectRefList.getObjectRefs()

        then:
        objectRefList.getObjectRefs().size() == 2

        when: 'get first DE details'
        String deString = regRep.getDocEntry(objectRefList.getObjectRefs().get(0))
        println "Full Metadata:"
        println deString

        then:
        deString
        deString?.trim()?.startsWith('<')
    }
}
