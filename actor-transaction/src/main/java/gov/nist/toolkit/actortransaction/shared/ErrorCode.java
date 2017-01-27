package gov.nist.toolkit.actortransaction.shared;

import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.io.Serializable;

/**
 *
 */
public class ErrorCode implements Serializable {
    TransactionType transaction;
    String code;
    Severity severity;
    String text;

    public TransactionType getTransaction() {
        return transaction;
    }


    public void setTransaction(TransactionType transaction) {
        this.transaction = transaction;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
