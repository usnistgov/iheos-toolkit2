package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetInteractionFromModelRequest;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class GetInteractionFromModelCommand extends GenericCommand<GetInteractionFromModelRequest,InteractingEntity>{
    @Override
    public void run(GetInteractionFromModelRequest var1) {
        FrameworkInitialization.data().getToolkitServices().getInteractionFromModel(var1,this);
    }
}
