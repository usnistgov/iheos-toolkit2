package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;

/**
 * Created by bill on 8/17/15.
 */
public class TransactionTransportFactory {

    static public TransactionTransport get(CallType callType) {
        if (callType == CallType.SOAP) return new SoapTransactionTransport();
        if (callType == CallType.DIRECT_CALL) return new DirectTransactionTransport();
        throw new ToolkitRuntimeException("TransactionTransportFactory:  No valid call type is configured.");
    }
}
