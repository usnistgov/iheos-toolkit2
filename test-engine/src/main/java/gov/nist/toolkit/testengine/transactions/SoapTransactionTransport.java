package gov.nist.toolkit.testengine.transactions;

import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 8/17/15.
 */
public class SoapTransactionTransport implements TransactionTransport {
    BasicTransaction basicTransaction;

    @Override
    public void attach(BasicTransaction basicTransaction) {
        this.basicTransaction = basicTransaction;
    }

    @Override
    public OMElement call(OMElement content) throws Exception {
        basicTransaction.soapCall(content);
        return basicTransaction.getSoapResult();
    }
}
