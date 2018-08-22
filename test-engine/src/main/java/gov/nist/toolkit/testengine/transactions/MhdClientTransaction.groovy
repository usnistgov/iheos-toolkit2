package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.testengine.assertionEngine.Assertion
import gov.nist.toolkit.testengine.assertionEngine.AssertionEngine
import gov.nist.toolkit.testengine.engine.*
import gov.nist.toolkit.testengine.engine.validations.ProcessValidations
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.fhir.*
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement


/**
 * Runs assertions that rely on SimReference and FhirSimulatorTransaction
 */
class MhdClientTransaction extends BasicTransaction {
    ILogReporting logReport
    private List <String> errs;

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

    @Override
    protected void run(OMElement request) throws Exception {
    }


    @Override
    void processAssertion(AssertionEngine engine, Assertion a, OMElement assertion_output) throws XdsInternalException {
        errs = new ArrayList <>();
        try {
            SimReference simReference = getSimReference(a)
            if (a.hasValidations()) {
                // the collection of FHIR transactions to search against
                List<FhirSimulatorTransaction> transactions = new FhirSimulatorTransaction(simReference.simId,simReference.transactionType).getAll()
                List<FhirSimulatorTransaction> passing = new ProcessValidations(this, logReport).run(new SimDbTransactionInstanceBuilder<FhirSimulatorTransaction>(new SimDb(simReference.simId)), simReference, a, assertion_output, transactions)
                if (passing.isEmpty())
                    errs.add("No Transactions match requirements")
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


}
