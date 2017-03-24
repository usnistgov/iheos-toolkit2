package gov.nist.toolkit.itTests.img

import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ParamType
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simulators.sim.ids.IdsActorSimulator
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static gov.nist.toolkit.utilities.xml.XmlUtil.strToOM
/**
 * Sample test class for IDS simulator RAD-69 receive/respond
 */
class IdsActorSimSpec extends ToolkitSpecification {

    @Shared SimulatorBuilder spi
    @Shared String testSession = 'idsitest'
    @Shared String id = 'simulator_ids'
    @Shared SimId simId = new SimId(testSession, id)
    @Shared String actor = "ids"
    @Shared String transaction = "ids.ret"
    @Shared TransactionType transactionType = TransactionType.RET_IMG_DOC_SET
    @Shared String envName = 'default'
    @Shared File simDbFile
    @Shared String testName = 'test name'
    @Shared OMElement requestElement
    @Shared OMElement responseElement = null

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

    def 'RAD-69 Response namespaces test' () {

        given: "A received message"

        String requestString = '''\
       <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
           <soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
              <wsa:To soapenv:mustUnderstand="true">http://localhost:9280/xdstools2/sim/test__simulator_ids/ids/ret.ids</wsa:To>
              <wsa:MessageID soapenv:mustUnderstand="true">urn:uuid:D3C5C1A713728C348B1488308738037</wsa:MessageID>
              <wsa:Action soapenv:mustUnderstand="true">urn:ihe:rad:2009:RetrieveImagingDocumentSet</wsa:Action>
           </soapenv:Header>
           <soapenv:Body>
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
              </xdsiB:RetrieveImagingDocumentSetRequest>
           </soapenv:Body>
        </soapenv:Envelope>'''

        requestElement = strToOM(requestString)

        SimDb db = Mock(SimDb)
        db.getRequestMessageBody() >> requestString

        when: 'Simulator instantiated'
        IdsActorSimulator sim = new IdsActorSimulator()

        then: 'It supports the $transactionType.name transaction'
        sim.supports transactionType

        when: 'Simulator invoked'

        MessageValidatorEngine mvc = new MessageValidatorEngine()
        ValidationContext vc = DefaultValidationContextFactory.validationContext()
        SimulatorConfig simConfig = api.getConfig(simId)
        SimCommon common = new MockedSimCommon(db, simConfig, false, vc, mvc, null, null)
        DsSimCommon dsSimCommon = new MockedDsSimCommon(common)
        sim = new IdsActorSimulator(common, dsSimCommon, db, simConfig)
        ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder()
        mvc.addErrorRecorder(testName, er)

        sim.run(transactionType, mvc, null)
        String responseString = XmlUtil.OMToStr(responseElement)

        /*
         * At this point
         * requestString and requestElement contains the SOAP request Envelope
         * in string and OMElement form
         * responseString and responseElement contains the SOAP request Envelope
         * in string and OMElement form
         */
        and: 'We collect the namespace URIs'
        Set<String> namespaces = new HashSet<>()
        for (OMElement e : XmlUtil.getDescendants(responseElement, true)) {
            namespaces.add(e.getNamespace().namespaceURI)
        }

        then: 'All required namespaces are present'
        namespaces.contains("urn:ihe:iti:xds-b:2007")
        namespaces.contains("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0")
        namespaces.contains("br.549")
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
