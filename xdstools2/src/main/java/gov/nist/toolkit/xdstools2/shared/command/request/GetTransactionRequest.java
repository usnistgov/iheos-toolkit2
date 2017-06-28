package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Request context to be used for all transaction related calls to the server.
 * Created by onh2 on 10/20/16.
 */
public class GetTransactionRequest extends CommandContext{
    private SimId simid;
    private String actor;
    private String trans;
    private String messageId;

    public GetTransactionRequest(){}
    public GetTransactionRequest(CommandContext commandContext, SimId simid){
        copyFrom(commandContext);
        this.simid=simid;
    }
    public GetTransactionRequest(CommandContext context,SimId simid,String actor,String transaction){
        this(context,simid);
        this.actor=actor;
        this.trans=transaction;
    }
    public GetTransactionRequest(CommandContext commandContext, SimId simid, String actor, String trans, String messageId) {
        this(commandContext,simid,actor,trans);
        this.messageId=messageId;
    }

    // ****** Getters and Setters ****** //

    public SimId getSimid() {
        return simid;
    }

    public void setSimid(SimId simid) {
        this.simid = simid;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
