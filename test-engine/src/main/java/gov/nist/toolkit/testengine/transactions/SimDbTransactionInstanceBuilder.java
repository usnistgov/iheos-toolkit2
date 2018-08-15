package gov.nist.toolkit.testengine.transactions;


import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.engine.TransactionRecordGetter;

import java.util.List;

// Ties collecting of TransactionInstance to SimDb
// Test harness for IT testing offers alternate
public class SimDbTransactionInstanceBuilder<U extends TransactionRecordGetter<U>> implements TransactionInstanceBuilder {
    SimDb simDb;
    List<String> errs;

    public SimDbTransactionInstanceBuilder(SimDb simDb, List<String> errs) {
        this.simDb = simDb;
        this.errs = errs;
    }

    @Override
    public TransactionInstance build(String actor, String eventId, String trans) {
        return simDb.buildTransactionInstance(actor, eventId, trans);
    }

    /*
    @Override
    public List<U> getSimulatorTransactions(SimReference simReference) throws XdsInternalException {
        try {
            return .getAll(simReference.getSimId(), simReference.getTransactionType());
        } catch (XdsInternalException ie) {
            errs.add(String.format("Error loading reference to simulator transaction from logs for %s\n%s\n", simReference.toString(), ie.getMessage()));
            throw ie;
        }
    }
    */

}

