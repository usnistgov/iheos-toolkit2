package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class QDTransaction extends XCQTransaction {


    public QDTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
    }

    @Override
    public OMElement getSecurityEl(String assertionStr) throws XdsInternalException {
        try {
            return SequoiaHeaderBuilder.buildSequoiaSecurityHeader(endpoint, assertionStr, transactionSettings);
        } catch (Exception e) {
            throw new XdsInternalException("Cannot build Sequoia security header: " + e.getMessage(), e);
        }
    }

    @Override
    protected String getBasicTransactionName() {
        return TransactionType.QD.getShortName();
    }

}
