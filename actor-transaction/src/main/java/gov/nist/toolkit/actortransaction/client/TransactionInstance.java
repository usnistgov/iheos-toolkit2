package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class TransactionInstance implements IsSerializable, Serializable {
    public String simId = null;
    public String messageId = null;   // message id
    public String labelInterpretedAsDate = null;
    public String trans = null;    // transaction type code
    public TransactionType nameInterpretedAsTransactionType = null;
    public ActorType actorType = null;
    public String ipAddress;
    public boolean isPif = false;

    public TransactionInstance() {}

    public String toString() {
        return labelInterpretedAsDate + " " + nameInterpretedAsTransactionType + " " + ipAddress;
    }

    public TransactionInstance chooseFromList(String label, List<TransactionInstance> instances) {
        String[] parts = label.split(" ");
        if (parts.length != 3) return null;
        String date = parts[0];
        String tt = parts[1];
        String ip = parts[2];

        for (TransactionInstance ti : instances) {
            if (date.equals(ti.labelInterpretedAsDate)) return ti;
        }

        // could select on the other parts - is there a need?
        return null;
    }

    public String getTransactionTypeName() { return trans; }
    public ActorType getActorType() { return actorType; }

    public void setActorType(ActorType actorType) {
        this.actorType = actorType;
    }

    public static TransactionInstance copy(TransactionInstance src) {
       TransactionInstance ti = new TransactionInstance();
       ti.simId = src.simId;
       ti.messageId = src.messageId;
       ti.labelInterpretedAsDate = src.labelInterpretedAsDate;
       ti.trans = src.trans;
       ti.nameInterpretedAsTransactionType = src.nameInterpretedAsTransactionType;
       ti.actorType = src.actorType;
       ti.ipAddress = src.ipAddress;
       return ti;
    }
}
