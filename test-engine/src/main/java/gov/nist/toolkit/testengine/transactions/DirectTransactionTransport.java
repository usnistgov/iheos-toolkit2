package gov.nist.toolkit.testengine.transactions;

import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 8/17/15.
 */
public class DirectTransactionTransport implements TransactionTransport {
    @Override
    public void attach(BasicTransaction basicTransaction) {
        // not needed
    }

    @Override
    public OMElement call(OMElement content) throws Exception {
        return null;
    }
}
