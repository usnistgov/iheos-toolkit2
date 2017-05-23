package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetTransactionErrorCodeRefsRequest extends CommandContext {
    private Severity severity;
    private String transactionName;

    public GetTransactionErrorCodeRefsRequest(){}
    public GetTransactionErrorCodeRefsRequest(CommandContext context, String transactionName, Severity severity){
        copyFrom(context);
        this.transactionName=transactionName;
        this.severity=severity;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }
}
