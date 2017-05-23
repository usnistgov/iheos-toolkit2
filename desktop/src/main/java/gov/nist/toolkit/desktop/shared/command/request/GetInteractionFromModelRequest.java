package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;

/**
 * Created by onh2 on 11/14/16.
 */
public class GetInteractionFromModelRequest extends CommandContext {
    private InteractingEntity model;

    public GetInteractionFromModelRequest(){}
    public GetInteractionFromModelRequest(CommandContext context, InteractingEntity model){
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
