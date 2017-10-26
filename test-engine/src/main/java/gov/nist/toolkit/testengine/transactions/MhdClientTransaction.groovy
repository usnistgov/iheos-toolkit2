package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.simcommon.client.SimId
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
    public void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
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
        //if (xdsInternalException != null) throw xdsInternalException;
    }

    def verifyFindSingleDRSubmit(SimReference simReference) {
        List<AbstractValidater> validaters = [
                new PostValidater(simReference),
                new StatusValidater(simReference, '201'),
                new SingleDocSubmissionValidater(simReference)
        ]
        List<FhirSimulatorTransaction> transactions = getSimulatorTransactions(simReference)
        if (transactions.size() == 0)
            throw new XdsInternalException("No ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")

        s_ctx.addDetail("#Validations run against all ${simReference.transactionType.name} transactions", '')
        validaters.each {AbstractValidater val ->
            s_ctx.addDetail(val.filterDescription, '')
        }

        boolean goodMessageFound = false
        s_ctx.addDetail("#${simReference.transactionType.name} Messages", "Failed Validation")
        transactions.each { FhirSimulatorTransaction transaction ->
            String thisUrl = transaction.url
            boolean hasError = false
            validaters.collect { AbstractValidater validater ->
                validater.validate(transaction)
            }.each {ValidaterResult result ->
                if (result.match) {
                    hasError = true
                    s_ctx.addDetail(result.transaction.url, result.filter.filterDescription)
                    //goodMessageFound = true
                } else {
                    s_ctx.addDetail(result.transaction.url, result.filter.filterDescription)
                    hasError = true
                }
            }
            if (!hasError) {
                s_ctx.addDetail(thisUrl, '')
                goodMessageFound = true
            }
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
            SimId simId = new SimId(ti.getUser(), id, aType.getShortName());
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
