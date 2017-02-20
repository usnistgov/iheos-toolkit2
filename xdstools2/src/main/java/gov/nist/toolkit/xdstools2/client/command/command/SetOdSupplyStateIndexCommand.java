package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetOdSupplyStateIndexRequest;

/**
 * Created by skb1 on 2/16/17.
 */
public abstract class SetOdSupplyStateIndexCommand extends GenericCommand<SetOdSupplyStateIndexRequest,Boolean>{
    @Override
    public void run(SetOdSupplyStateIndexRequest request) {
        ClientUtils.INSTANCE.getToolkitServices().setOdSupplyStateIndex(request, this);
    }
}
