package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.assertionEngine.Assertion
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine
import gov.nist.toolkit.testengine.engine.*
import gov.nist.toolkit.testengine.engine.fhirValidations.*
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement


/**
 * Runs assertions that rely on SimReference and FhirSimulatorTransaction
 */
class MhdClientTransaction extends BasicTransaction {
    ILogReporting logReport


    @Override
    protected void run(OMElement request) throws Exception {
    }

    private List <String> errs;

    @Override
    void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
        errs = new ArrayList <>();
        try {
            SimReference simReference = getSimReference(a)
            if (a.hasValidations()) {
                processValidations(new SimDbTransactionInstanceBuilder(new SimDb(simReference.simId)), simReference, a, assertion_output)
            } else
                throw new XdsInternalException("MhdClientTransaction: Unknown Assertion clause with not Assert statements");
        } catch (XdsInternalException ie) {
            errs.add(ie.getMessage());
        }
        if (!errs.isEmpty()) {
            ILogger testLogger = new TestLogFactory().getLogger();
            testLogger.add_name_value_with_id(assertion_output, "AssertionStatus", a.id, "fail");
            for (String err : errs)
                logReport.fail(err);
        }
    }

    // Ties collecting of TransactionInstance to SimDb
    // Test harness for IT testing offers alternate
    class SimDbTransactionInstanceBuilder implements TransactionInstanceBuilder {
        SimDb simDb

        SimDbTransactionInstanceBuilder(SimDb simDb) {
            this.simDb = simDb
        }

        @Override
        TransactionInstance build(String actor, String eventId, String trans) {
            return simDb.buildTransactionInstance(actor, eventId, trans)
        }

        @Override
        List<FhirSimulatorTransaction> getSimulatorTransactions(SimReference simReference) throws XdsInternalException {
            try {
                return FhirSimulatorTransaction.getAll(simReference.simId, simReference.transactionType)
            } catch (XdsInternalException ie) {
                errs.add("Error loading reference to simulator transaction from logs for ${simReference.toString()}\n" + ie.getMessage());
                throw ie;
            }
        }

    }

    List<FhirSimulatorTransaction> processValidations(TransactionInstanceBuilder transactionInstanceBuilder, SimReference simReference, Assertion a, OMElement assertion_output) {
        String trans = simReference.transactionType.code

        // the collection of FHIR transactions to search against
        List<FhirSimulatorTransaction> transactions = transactionInstanceBuilder.getSimulatorTransactions(simReference)

        if (transactions.size() == 0)
            throw new XdsInternalException("No ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")

        logReport.addDetail("#Validations run against all ${simReference.transactionType.name} transactions", '')

        a.validations.validaters.each { Assertion.Validations.ValidaterInstance v ->
            logReport.addDetail(v.validater.filterDescription, '')

        }

        boolean goodMessageFound = false
        List<FhirSimulatorTransaction> passing = []
        List<ValidaterResult> failing = []
        transactions.each { FhirSimulatorTransaction transaction ->
            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            String thisUrl = transaction.url + " (${label})"
            boolean hasError = false
            a.getAllValidaters().collect { Assertion.Validations.ValidaterInstance validater1 ->
                if (!(validater1.validater instanceof AbstractFhirValidater))
                    throw new ToolkitRuntimeException("oops")
                AbstractFhirValidater validater = (AbstractFhirValidater) validater1.validater
                validater.validate(transaction)
            }.each {ValidaterResult result ->
                if (!result.match) {
                    failing << result
                    hasError = true
                }
            }
            if (!hasError) {
                passing << transaction
                goodMessageFound = true
            }
        }
        logReport.addDetailHeader('Validating Messages')
        passing.each { FhirSimulatorTransaction transaction ->
            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            logReport.addDetailLink(transaction.url, transaction.placeToken, label, '')
        }
        logReport.addDetailHeader('Non-Validating Messages', 'Failed Validations')
        failing.each { ValidaterResult result ->
            FhirSimulatorTransaction transaction = result.transaction
            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            logReport.addDetailLink(transaction.url, transaction.placeToken, label, result.filter.filterDescription)
        }

        return passing
    }

//    @Deprecated
//    def verifyFindSingleDRSubmit(SimReference simReference) {
//        List<AbstractFhirValidater> validaters = [
//                new PostFhirValidater(simReference),
//                new StatusFhirValidater(simReference, '200'),
//                new SingleDocSubmissionFhirValidater(simReference)
//        ]
//        SimDb simDb = new SimDb(simReference.simId)
//        String trans = simReference.transactionType.code
//        List<FhirSimulatorTransaction> transactions = getSimulatorTransactions(simReference)
//        if (transactions.size() == 0)
//            throw new XdsInternalException("No ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")
//
//        logReport.addDetail("#Validations run against all ${simReference.transactionType.name} transactions", '')
//        validaters.each { AbstractFhirValidater val ->
//            logReport.addDetail(val.filterDescription, '')
//        }
//
//        boolean goodMessageFound = false
//        List<FhirSimulatorTransaction> passing = []
//        List<ValidaterResult> failing = []
//        transactions.each { FhirSimulatorTransaction transaction ->
//            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
//            String label = ti.toString()
//            String thisUrl = transaction.url + " (${label})"
//            boolean hasError = false
//            validaters.collect { AbstractFhirValidater validater ->
//                validater.validate(transaction)
//            }.each {ValidaterResult result ->
//                if (result.match) {
//                } else {
//                    failing << result
//                    hasError = true
//                }
//            }
//            if (!hasError) {
//                passing << transaction
//                goodMessageFound = true
//            }
//        }
//        logReport.addDetailHeader('Validating Messages')
//        passing.each { FhirSimulatorTransaction transaction ->
//            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
//            String label = ti.toString()
//            logReport.addDetailLink(transaction.url, transaction.placeToken, label, '')
//        }
//        logReport.addDetailHeader('Non-Validating Messages', 'Failed Validations')
//        failing.each { ValidaterResult result ->
//            FhirSimulatorTransaction transaction = result.transaction
//            TransactionInstance ti = simDb.buildTransactionInstance(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
//            String label = ti.toString()
//            logReport.addDetailLink(transaction.url, transaction.placeToken, label, result.filter.filterDescription)
//        }
//
//        if (!goodMessageFound)
//            throw new XdsInternalException("No acceptable ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")
//    }

    SimReference getSimReference(Assertion a) {
        try {
            OMElement simTransactionElement = XmlUtil.firstChildWithLocalName(a.assertElement, "SimReference");
            return a.getSimReference(simTransactionElement)
        } catch (ToolkitRuntimeException ie) {
            errs.add("Error decoding reference to simulator transaction from testplan assertion: " + ie.getMessage());
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
        logReport = s_ctx
        defaultEndpointProcessing = false
        parse_metadata = false
        noMetadataProcessing = true
    }

    // for IT testing only
    // where ILogReporting is adequate
    MhdClientTransaction(ILogReporting logReport, OMElement instruction, OMElement instruction_output) {
        super(null, instruction, instruction_output)
        this.logReport = logReport
        defaultEndpointProcessing = false
        parse_metadata = false
        noMetadataProcessing = true
    }
}
