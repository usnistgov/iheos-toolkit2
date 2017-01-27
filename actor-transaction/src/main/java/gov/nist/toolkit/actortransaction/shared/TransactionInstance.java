package gov.nist.toolkit.actortransaction.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

/**
 *
 */
public class TransactionInstance implements IsSerializable {
    public String simId = null;
    public String messageId = null;   // message id
    public String labelInterpretedAsDate = null;
    public String trans = null;    // transaction type code
    public TransactionType nameInterpretedAsTransactionType = null;
    public ActorType actorType = null;
    public String ipAddress;

    public String toString() {
        return labelInterpretedAsDate + " " + nameInterpretedAsTransactionType + " " + ipAddress;

    }
}
