package gov.nist.toolkit.actortransaction.client;

/**
 * Created by bill on 9/30/15.
 */
public class TransactionInstance {
    String label;
    String labelInterpretedAsDate;
    String name;
    TransactionType nameInterpretedAsTransactionType;

    public TransactionInstance(String label, String name) {
        this.label = label;
        this.name = name;


    }
}
