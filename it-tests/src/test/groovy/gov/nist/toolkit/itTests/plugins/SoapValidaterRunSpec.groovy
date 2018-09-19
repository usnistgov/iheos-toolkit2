package gov.nist.toolkit.itTests.plugins

import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.server.SimDbEvent
import gov.nist.toolkit.testengine.assertionEngine.Assertion
import gov.nist.toolkit.testengine.engine.*
import gov.nist.toolkit.testengine.engine.validations.ValidationPluginRunner
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.transactions.NullTransaction
import gov.nist.toolkit.testengine.transactions.TransactionInstanceBuilder
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.axis2.AxisFault
import spock.lang.Shared
import spock.lang.Specification

class SoapValidaterRunSpec extends Specification {
    @Shared Session session
    @Shared TestKitSearchPath path
    @Shared SimId simId
    @Shared TransactionType tType
    @Shared SimReference simReference
    @Shared ToolkitEnvironment toolkitEnvironment
    @Shared ActorType actorType

    def setupSpec() {
        Installation.instance().setServletContextName("");
        session = UnitTestEnvironmentManager.setupLocalToolkit()
        path = new TestKitSearchPath('default', TestSession.DEFAULT_TEST_SESSION)
        simId = SimIdFactory.simIdBuilder('default__foo')
        tType = TransactionType.ANY
        simReference = new SimReference(simId, tType)
        actorType = ActorType.DOC_SOURCE
        toolkitEnvironment = new ToolkitEnvironment() {
            @Override
            String getEnvironment() {
                return 'default'
            }

            @Override
            TestSession getTestSession() {
                return TestSession.DEFAULT_TEST_SESSION
            }
        }
    }

    def 'run mock validater'() {
        setup:
        def assertionText = '''
<Assert id='test1'>
   <SimReference id="reg" actorType="reg" transaction="rb"/>
   <Validations type="SOAP">
      <DummyValidater testAttribute="testValue"/>  <!-- Any attributes must match instance variables in the class specified here. -->
   </Validations>
</Assert>
'''
        when:
        OMElement assertionEle = Util.parse_xml(assertionText)
        TestConfig testConfig = new TestConfig()
        String date = 'Today'
        Assertion a = new Assertion(toolkitEnvironment, assertionEle, testConfig, date)
        NullTransaction nt = new NullTransaction(new LogReport(), null, null)
        TransactionInstanceBuilder transactionInstanceBuilder = new MyTransactionInstanceBuilder()
//        List<SimulatorTransaction> transactions = new SimulatorTransaction(simReference.simId,simReference.transactionType).getAll()
        List<SoapSimulatorTransaction> transactions = transactionInstanceBuilder.getSimulatorTransactions(simReference)
        List<ValidaterResult> passing = new ValidationPluginRunner<SoapSimulatorTransaction>(nt.getLogReport()).run(transactionInstanceBuilder, simReference, a, null, transactions)

        then:
        passing.size() == 1
    }

    def messageId = 1
    class MyTransactionInstanceBuilder implements TransactionInstanceBuilder {

        @Override
        TransactionInstance build(String actor, String eventId, String trans) {
            TransactionInstance ti = new TransactionInstance()
            ti.simId = simId
            ti.messageId = messageId++ as String
            ti.actorType = ActorType.findActor(actor)
            ti.trans = trans
            ti.labelInterpretedAsDate = 'now'
            ti.nameInterpretedAsTransactionType = TransactionType.ANY
            return ti
        }

        List<SoapSimulatorTransaction> getSimulatorTransactions(SimReference simReference) throws XdsInternalException {
            SoapSimulatorTransaction sst = new SoapSimulatorTransaction(simReference)
            SimDbEvent e = new SimDbEvent(new SimId(new TestSession("bogusTestSession"),"bogusSimId"), "reg", "rb", 'event0')
            sst.simDbEvent = e
            [sst]
        }
    }

    // an interface used by StepContext
    class LogReport implements ILogReporting {

        @Override
        void addDetail(String name, String value) {

        }

        @Override
        void addDetailHeader(String headerText) {

        }

        @Override
        void addDetailHeader(String headerText, String value) {

        }

        @Override
        void addDetailLink(String externalLink, String internalPlaceToken, String linkText, String content) {

        }

        @Override
        void set_error(String msg) throws XdsInternalException {

        }

        @Override
        void fail(OMElement ele) throws XdsInternalException {

        }

        @Override
        void set_error(List<String> msgs) throws XdsInternalException {

        }

        @Override
        void set_fault(String code, String msg) throws XdsInternalException {

        }

        @Override
        void set_fault(AxisFault e) throws XdsInternalException {

        }

        @Override
        void fail(String message) throws XdsInternalException {

        }
    }

}
