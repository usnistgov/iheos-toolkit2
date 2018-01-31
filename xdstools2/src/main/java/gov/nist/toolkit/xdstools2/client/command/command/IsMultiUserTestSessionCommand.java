package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetOdSupplyStateIndexRequest;

/**
 * Created by skb1 on 1/30/18.
 */
public abstract class IsMultiUserTestSessionCommand extends GenericCommand<SetOdSupplyStateIndexRequest,Boolean>{
    @Override
    public void run(SetOdSupplyStateIndexRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().setOdSupplyStateIndex(request, this);
    }
}
