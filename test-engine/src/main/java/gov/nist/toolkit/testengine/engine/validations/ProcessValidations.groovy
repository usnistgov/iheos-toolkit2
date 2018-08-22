package gov.nist.toolkit.testengine.engine.validations

import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.testengine.assertionEngine.Assertion
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.ILogReporting
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.TransactionRecordGetter
import gov.nist.toolkit.testengine.engine.validations.fhir.AbstractFhirValidater
import gov.nist.toolkit.testengine.transactions.BasicTransaction
import gov.nist.toolkit.testengine.transactions.TransactionInstanceBuilder
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement

class ProcessValidations<T extends TransactionRecordGetter<T>> {
    final BasicTransaction basicTransaction;
    ILogReporting logReport

    ProcessValidations(BasicTransaction bt, ILogReporting logReport) {
        this.basicTransaction = bt
        this.logReport = logReport
    }

    // return list of passing transactions
    List<T> run(TransactionInstanceBuilder transactionInstanceBuilder, SimReference simReference, Assertion a, OMElement assertion_output, List<T> transactions) {
        String trans = simReference.transactionType.code

        if (transactions.size() == 0)
            throw new XdsInternalException("No ${simReference.transactionType.name} transactions found in simlog for ${simReference.simId}")

        logReport.addDetail("#Validations run against all ${simReference.transactionType.name} transactions", '')

        a.validations.validaters.each { Assertion.Validations.ValidaterInstance v ->
            logReport.addDetail(v.validater.class.simpleName, v.validater.filterDescription)
        }


        List<ValidaterResult> inProgress = []
        List<ValidaterResult> passing = []
        List<ValidaterResult> failing = []
        transactions.each { T transaction ->
//            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            boolean hasError = false

            // Run all validators on this transaction
            a.getAllValidaters().collect { Assertion.Validations.ValidaterInstance validater1 ->
                if (!(validater1.validater instanceof AbstractValidater))
                    throw new ToolkitRuntimeException("oops")
                AbstractValidater validater = (AbstractValidater) validater1.validater
                ValidaterResult result = validater.validate(transaction)
                result
            }.each {ValidaterResult result ->
                if (result.match) {
                    inProgress << result
                } else {
                    failing << result
                    hasError = true
                }
            }

            if (!hasError && !inProgress.isEmpty()) {
                consolidateLogs(inProgress)
                passing << inProgress[0]
            }
        }

        logReport.addDetailHeader('Validating Messages')
        passing.each { ValidaterResult result ->
            T transaction = result.transaction
            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            logReport.addDetailLink(transaction.url, transaction.placeToken, label, result.filter.filterDescription)
            String log = result.log
            if (log)
                log.eachLine { String line ->
                    logReport.addDetail('', line)
                }
        }

        logReport.addDetailHeader('Non-Validating Messages', 'Failed Validations')
        failing.each { ValidaterResult result ->
            T transaction = result.transaction
            TransactionInstance ti = transactionInstanceBuilder.build(transaction.simDbEvent.actor, transaction.simDbEvent.eventId, trans)
            String label = ti.toString()
            logReport.addDetailLink(transaction.url, transaction.placeToken, label, result.filter.filterDescription)
            result.filter.log.eachLine { String line ->
                logReport.addDetail('', line)
            }
        }

        return passing
    }


    private def consolidateLogs(List<ValidaterResult> results) {
        ValidaterResult first = null
        results.each { ValidaterResult result ->
            if (first == null) {
                first = result
                return
            }
            String x = result.filter.getLog()
            first.log(x)
        }
    }

}
