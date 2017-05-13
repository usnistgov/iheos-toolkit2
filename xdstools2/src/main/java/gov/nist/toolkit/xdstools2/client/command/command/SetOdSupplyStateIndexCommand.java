package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.SetOdSupplyStateIndexRequest;

/**
 * Created by skb1 on 2/16/17.
 */
public abstract class SetOdSupplyStateIndexCommand extends GenericCommand<SetOdSupplyStateIndexRequest,Boolean>{
    @Override
    public void run(SetOdSupplyStateIndexRequest request) {
        XdsTools2Presenter.data().getToolkitServices().setOdSupplyStateIndex(request, this);
    }
}
