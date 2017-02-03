package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class GetInteractionFromModelRequest extends CommandContext{
    private InteractingEntity model;

    public GetInteractionFromModelRequest(){}
    public GetInteractionFromModelRequest(CommandContext context,InteractingEntity model){
        copyFrom(context);
        this.model=model;
    }

    public InteractingEntity getModel() {
        return model;
    }

    public void setModel(InteractingEntity model) {
        this.model = model;
    }
}
