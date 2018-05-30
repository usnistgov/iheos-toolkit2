package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.assertionEngine.Assertion
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.ILogger
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.TestLogFactory
import gov.nist.toolkit.testengine.engine.fhirValidations.*
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement

import javax.xml.namespace.QName
/**
 *
 */
class MhdClientTransaction extends BasicTransaction {


    @Override
    protected void run(OMElement request) throws Exception {
    }

    private List <String> errs;

    @Override
    void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
        XdsInternalException xdsInternalException = null;
        errs = new ArrayList <>();
        try {
            SimReference simReference = getSimReference(a)
            switch (a.process) {
                case "FindSingleDRSubmit":
                    verifyFindSingleDRSubmit(simReference)
                    break
                default:
                    throw new XdsInternalException("MhdClientTransaction: Unknown assertion.process: ${a.process}");
            }
        } catch (XdsInternalException ie) {
            xdsInternalException = ie;
            errs.add(ie.getMessage());
        }
        if (!errs.isEmpty()) {
            ILogger testLogger = new TestLogFactory().getLogger();
            testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
            for (String err : errs)
                s_ctx.fail(err);
        }
    }

    def verifyFindSingleDRSubmit(SimReference simReference) {
        List<AbstractFhirValidater> validaters = [
                new PostFhirValidater(simReference),
                new StatusFhirValidater(simReference, '200'),
                new SingleDocSubmissionFhirValidater(simReference)
        ]
        SimDb simDb = new SimDb(simReference.simId)
        String trans = simReference.transactionType.code
        List<FhirSimulatorTransaction> transactions = getSimulatorTransactions(simReference)
        if (transactions.size() == 0)
            throw new XdsInternalException("No ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")

        s_ctx.addDetail("#Validations run against all ${simReference.transactionType.name} transactions", '')
        validaters.each { AbstractFhirValidater val ->
            s_ctx.addDetail(val.filterDescription, '')
        }

        boolean goodMessageFound = false
        List<FhirSimulatorTransaction> passing = []
        List<ValidaterResult> failing = []
        transactions.each { FhirSimulatorTransaction transaction ->
            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            String thisUrl = transaction.url + " (${label})"
            boolean hasError = false
            validaters.collect { AbstractFhirValidater validater ->
                validater.validate(transaction)
            }.each {ValidaterResult result ->
                if (result.match) {
                } else {
                    failing << result
                    hasError = true
                }
            }
            if (!hasError) {
                passing << transaction
                goodMessageFound = true
            }
        }
        s_ctx.addDetailHeader('Validating Messages')
        passing.each { FhirSimulatorTransaction transaction ->
            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
//            String internalUrl = ''
            String label = ti.toString()
//            String externalUrl = "${transaction.url} [${transaction.placeToken}] (${label})"
//            s_ctx.addDetail(externalUrl, '')
            s_ctx.addDetailLink(transaction.url, transaction.placeToken, label, '')
        }
        s_ctx.addDetailHeader('Non-Validating Messages', 'Failed Validations')
        failing.each { ValidaterResult result ->
            FhirSimulatorTransaction transaction = result.transaction
            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
//            String internalUrl = ''
//            String externalUrl = "${transaction.url} [${transaction.placeToken}] (${label})"
//            s_ctx.addDetail(externalUrl, result.filter.filterDescription)
            s_ctx.addDetailLink(transaction.url, transaction.placeToken, label, result.filter.filterDescription)
        }

        if (!goodMessageFound)
            throw new XdsInternalException("No acceptable ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")
    }

    SimReference getSimReference(Assertion a) {
        try {
            OMElement simTransactionElement = XmlUtil.firstChildWithLocalName(a.assertElement, "SimReference");
            if (simTransactionElement == null)
                throw new XdsInternalException(a.toString() + " has no SimReference element");
            String id = simTransactionElement.getAttributeValue(new QName("id"));
            String trans = simTransactionElement.getAttributeValue(new QName("transaction"));
            TransactionType tType = TransactionType.find(trans);
            if (tType == null) throw new XdsInternalException(a.toString() + " invalid transaction");
            ActorType aType = ActorType.getActorType(tType);
            TestInstance ti = testConfig.testInstance;
            SimId simId = new SimId(ti.testSession, id, aType.getShortName());
            return new SimReference(simId, tType)
        } catch (XdsInternalException ie) {
            errs.add("Error decoding reference to simulator transaction from testplan assertion: " + ie.getMessage());
            throw ie;
        }
    }

    private List<FhirSimulatorTransaction> getSimulatorTransactions(SimReference simReference) throws XdsInternalException {
        try {
            return FhirSimulatorTransaction.getAll(simReference.simId, simReference.transactionType)
        } catch (XdsInternalException ie) {
            errs.add("Error loading reference to simulator transaction from logs for ${simReference.toString()}\n" + ie.getMessage());
            throw ie;
        }
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        parseBasicInstruction(part);
    }

    @Override
    protected String getRequestAction() {
        return null
    }

    @Override
    protected String getBasicTransactionName() {
        return "pdb"
    }

    MhdClientTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
        defaultEndpointProcessing = false
        parse_metadata = false
        noMetadataProcessing = true
    }
}
