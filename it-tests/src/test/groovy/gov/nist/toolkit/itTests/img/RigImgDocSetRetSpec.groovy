package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.actorfactory.AbstractActorFactory
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymsg.repository.RetImgDocSetReqParser
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestModel
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.ExtendedPropertyManager
import gov.nist.toolkit.simulators.sim.ids.IdsActorSimulator
import gov.nist.toolkit.simulators.sim.rig.RigImgDocSetRet
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator
import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import gov.nist.toolkit.xdsexception.client.XdsException
import org.apache.axiom.om.OMElement
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Path

/**
 * Unit tests for RigImgDocSetRet which creates RAD-69s for each IDS in the Rig's RAD-75 request.
 *
 * Created by dmaffitt on 3/8/17.
 */
class RigImgDocSetRetSpec extends ToolkitSpecification {

    @Shared String testSession = 'rigitest'
    @Shared String id = 'simulator_ids'
    @Shared SimId simId = new SimId(testSession, id)
    @Shared SimulatorBuilder spi
    @Shared File simDbFile
    @Shared OMElement requestElement
    @Shared OMElement responseElement = null

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

        simDbFile = Installation.instance().simDbFile()
    }

    // one time shutdown when everything is done
    def cleanupSpec() {
        api.deleteSimulatorIfItExists(simId)
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {
        api.createTestSession(testSession)
        createIdsSimulator()
    }

    def 'Retrieve Image Document Set Request Processing Test - create RAD-69s for all IDS in request' () {
        println(testDesc)

        when: 'The RAD-75 message is parsed'

        OMElement requestElement = XmlUtil.strToOM(requestString)

        then: 'There is an Element'
        requestElement

        when: 'the RigImgDocSetRet is instantiated'
        MessageValidatorEngine mvc = new MessageValidatorEngine()
        ValidationContext vc = DefaultValidationContextFactory.validationContext()
        SimulatorConfig simConfig = api.getConfig(simId)
        SimCommon common = new MockedSimCommon(db, simConfig, false, vc, mvc, null, null)
        DsSimCommon dsSimCommon = new MockedDsSimCommon(common)
        ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder()
        mvc.addErrorRecorder(testName, er)

        RigImgDocSetRet rigImgDocSetRet = new RigImgDocSetRet( common, dsSimCommon, asc);

        then: 'An object is created'
        rigImgDocSetRet

        when: 'the RigImgDocSetSetRet is run'
        rigImgDocSetRet.run(er, mvc)
        String responseString = XmlUtil.OMToStr(responseElement)

        then: 'number of repos equals the number of rad-69s sent.'
        rigImgDocSetRet.getKnownIDSCount() == rigImgDocSetRet.getRad69ResponseCount()

//        when: 'the RetrieveImageRequestModel instance is retrieved'
//        RetrieveImageRequestModel requestModel = requestParser.getRequest();

//        // Steps used when testing ability to extract model for particular RIG or IDS
//        if (!forHome.equalsIgnoreCase('all'))
//            requestModel = requestModel.getModelForCommunity(forHome)
//        if (!forIds.equalsIgnoreCase('all'))
//            requestModel = requestModel.getModelForRepository(forIds)
//
//        then: 'A request model instance is generated'
//        requestModel
//
//        then: 'The Transfer Syntax UIDs are correct'
//        def xsul = requestModel.getTransferSyntaxUIDs()
//        xsul
//        xsul.size() == xferSyntaxUids.size()
//        Collections.sort(xsul)
//        xsul == xferSyntaxUids
//
//        then: 'Home Community IDs are correct'
//        def hcid = requestModel.getHomeCommunityIds()
//        hcid
//        hcid.size() == homeCommunityIds.size()
//        hcid == homeCommunityIds

//        then: "Repository UIDs are correct"
//        def rid = requestModel.getIDSRepositoryUniqueIds()
//        rid
//        rid.size() == repositoryIds.size()
//        rid == repositoryIds
//
//        then: "Included image data is correct"
//        def imgData = requestModel.getCompositeUids(true, true)
//        imgData
//        imgData.size() == compositeIds.size()
//        imgData == compositeIds

        where: 'Tests to run'

        testDesc | requestString | forHome | forIds || xferSyntaxUids | homeCommunityIds  | repositoryIds | compositeIds
        'Request 1 object from 1 study from 1 repository' |
                $/\
              <xdsiB:RetrieveImagingDocumentSetRequest xmlns:xdsiB="urn:ihe:rad:xdsi-b:2009">
                 <xdsiB:StudyRequest studyInstanceUID="1.3.6.1.4.1.21367.201599.1.201604020954048">
                    <xdsiB:SeriesRequest seriesInstanceUID="1.3.6.1.4.1.21367.201599.2.201604020954048">
                       <xdsb:DocumentRequest xmlns:xdsb="urn:ihe:iti:xds-b:2007">
                          <xdsb:HomeCommunityId>urn:oid:1.3.6.1.4.1.21367.13.70.200</xdsb:HomeCommunityId>
                          <xdsb:RepositoryUniqueId>1.3.6.1.4.1.21367.13.80.110</xdsb:RepositoryUniqueId>
                          <xdsb:DocumentUniqueId>1.3.6.1.4.1.21367.201599.3.201604020954048.1</xdsb:DocumentUniqueId>
                       </xdsb:DocumentRequest>
                    </xdsiB:SeriesRequest>
                 </xdsiB:StudyRequest>
                 <xdsiB:TransferSyntaxUIDList>
                    <xdsiB:TransferSyntaxUID>1.2.840.10008.1.2.1</xdsiB:TransferSyntaxUID>
                 </xdsiB:TransferSyntaxUIDList>
              </xdsiB:RetrieveImagingDocumentSetRequest>/$ |
                'all' | 'all' |
                ['1.2.840.10008.1.2.1'] |
                ['urn:oid:1.3.6.1.4.1.21367.13.70.200'] as Set |
                ['1.3.6.1.4.1.21367.13.80.110'] as Set |
                ['1.3.6.1.4.1.21367.201599.1.201604020954048:1.3.6.1.4.1.21367.201599.2.201604020954048:1.3.6.1.4.1.21367.201599.3.201604020954048.1:urn:oid:1.3.6.1.4.1.21367.13.70.200:1.3.6.1.4.1.21367.13.80.110']
        }

    private SimulatorConfig getConfig() {
        SimId id = new SimId("rigspec__simulator_rig")
        String actorType = "rig"
        Date expiration = new Date();
        SimulatorConfig config = new SimulatorConfig( id, actorType, expiration)
        List<SimulatorConfigElement> elements = new ArrayList<>();
        elements.add( new SimulatorConfigElement())
        String[] idsNames = ["ids_e"]
        config.add(
                [
                        new SimulatorConfigElement("Name", ParamType.TEXT, "ids_sim"),
                        new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.1"),
                        new SimulatorConfigElement(SimulatorProperties.idsrEndpoint, ParamType.TEXT, "ids_rendpoint"),
                        new SimulatorConfigElement(SimulatorProperties.idsrTlsEndpoint, ParamType.TEXT, "ids_tlsrendpoint"),
                        new SimulatorConfigElement(SimulatorProperties.wadoEndpoint, ParamType.TEXT, "ids_wadoendpoint"),
                        new SimulatorConfigElement(SimulatorProperties.wadoTlsEndpoint, ParamType.TEXT, "ids_wado_tls.endpoint"),
                        new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-e"),

//            new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.2"),
//            new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-f"),
//
//            new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT, "1.3.6.1.4.1.21367.13.71.201.3"),
//                 new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "xca-dataset-g"),
//            new SimulatorConfigElement(SimulatorProperties.imagingDocumentSources, ParamType.SELECTION, idsNames, true)
                ] )
        return config
    }

    private Simulator buildSims() {
        AbstractActorFactory af = AbstractActorFactory.getActorFactory(ActorType.IMAGING_DOC_SOURCE)
        SimManager simm = new SimManager(null)
        SimId simId = new SimId("ids_e")
        Simulator repoSim = af.buildNewSimulator( simm, ActorType.IMAGING_DOC_SOURCE,  simId,  false )

        Site site = af.getActorSite( getConfig(), null)
        return repoSim
    }

    private void loadExtendedProperties() {
        ExtendedPropertyManager.load( new File("../xdstools2/src/webapp"))
//        String currentPath = java.nio.file.Paths.get("").toAbsolutePath().toString()
//        File propFile = new File("../xdstools2/src/main/webapp/WEB-INF/extended.properties" );
//        Properties properties = new Properties();
//        try {
//            properties.load(new FileInputStream(propFile));
//        } catch (Exception e) {
////			logger.info("Cannot load extended.properties");
//        }
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

    /*
     * Partially mocked classes, mostly to turn off standard validations,
     * bypassing them to get to the 'meat' of ids validations
     */

    class MockedDsSimCommon extends DsSimCommon {

        MockedDsSimCommon (SimCommon common) {
            super(common)
        }

        // Turns off initial validation, which is a general purpose routine
        // from Bill's stuff. Just returns "OK"
        @Override
        boolean runInitialValidationsAndFaultIfNecessary() throws IOException {
            return true
        }

        // Does not actually send http response, but grabs the SOAP message
        // for testing.
        @Override
        public void sendHttpResponse(OMElement env, ErrorRecorder er) throws IOException {
            responseElement = env;
        }

    }

    class MockedSimCommon extends SimCommon {

        MockedSimCommon (SimDb db, SimulatorConfig simConfig, boolean tls,
                         ValidationContext vc, MessageValidatorEngine mvc,
                         HttpServletRequest request, HttpServletResponse response)
                throws IOException, XdsException {
            super(db, simConfig, tls, vc, mvc, request, response)
        }

        // Returns a testing version of the SOAPMessageValidator
        AbstractMessageValidator getMessageValidatorIfAvailable(Class cls) {
            return new SoapMessageValidator(requestElement)
        }
    }

}
