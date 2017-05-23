package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.server.shared.command.CommandContext;


/**
 * Created by onh2 on 11/4/16.
 */
public class GetNewSimulatorRequest extends CommandContext {
    private SimId simId;
    private String actorTypeName;

    public GetNewSimulatorRequest(){}
    public GetNewSimulatorRequest(CommandContext context, String actorTypeName, SimId simId){
        copyFrom(context);
        this.actorTypeName=actorTypeName;
        this.simId=simId;
    }

    public SimId getSimId() {
        return simId;
    }

    public void setActorTypeName(String actorTypeName) {
        this.actorTypeName = actorTypeName;
    }

    public String getActorTypeName() {
        return actorTypeName;
    }

    public void setSimId(SimId simId) {
        this.simId = simId;
    }
}
