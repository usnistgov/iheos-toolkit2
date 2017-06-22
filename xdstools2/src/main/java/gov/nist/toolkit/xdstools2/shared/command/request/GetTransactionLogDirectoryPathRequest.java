package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.Date;

/**
 * Created by skb1 on 3/13/17.
 */
public class GetTransactionLogDirectoryPathRequest extends CommandContext{
    SimId simId;
    String transactionCode;
    String pid;
    Date hl7timeOfSectionRun;
    CommandContext commandContext;

    public GetTransactionLogDirectoryPathRequest() {
    }

    public GetTransactionLogDirectoryPathRequest(CommandContext context, SimId simId, String transactionCode, String pid, Date hl7timeOfSectionRun) {
        copyFrom(context);
        this.simId = simId;
        this.transactionCode = transactionCode;
        this.pid = pid;
        this.hl7timeOfSectionRun = hl7timeOfSectionRun;
    }

    public SimId getSimId() {
        return simId;
    }

    public void setSimId(SimId simId) {
        this.simId = simId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getHl7timeOfSectionRun() {
        return hl7timeOfSectionRun;
    }

    public void setHl7timeOfSectionRun(Date hl7timeOfSectionRun) {
        this.hl7timeOfSectionRun = hl7timeOfSectionRun;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    public void setCommandContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }
}
