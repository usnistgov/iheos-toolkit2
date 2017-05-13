package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteNamesByTranTypeRequest;

import java.util.List;

/**
 * Created by onh2 on 10/19/16.
 */
public abstract class GetSiteNamesByTranTypeCommand extends GenericCommand<GetSiteNamesByTranTypeRequest,List<String>>{
    @Override
    public void run(GetSiteNamesByTranTypeRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getSiteNamesByTranType(request,this);
    }
}
