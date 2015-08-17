package gov.nist.toolkit.testengine.transactions;

import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 8/17/15.
 */

/**
 * This class is injected into the transaction processing to allow control of
 * the use of either a SOAP interface or a Direct Call interface.  The Direct
 * Call interface is used for unit testing and maybe later some forms of simulator
 * integration.
 */
public interface TransactionTransport {
    void attach(BasicTransaction basicTransaction);
    OMElement call(OMElement content) throws Exception;

}
