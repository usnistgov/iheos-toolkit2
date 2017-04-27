package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetSutInitiatedTransactionInstanceRequest;

import java.util.List;

/**
 * Created by skb1 on 3/15/17.
 */
public abstract class SetSutInitiatedTransactionInstanceCommand extends GenericCommand<SetSutInitiatedTransactionInstanceRequest,List<InteractingEntity>>{
    @Override
    public void run(SetSutInitiatedTransactionInstanceRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().setSutInitiatedTransactionInstance(request,this);
    }
}
