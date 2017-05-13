package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSiteRequest;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class DeleteSiteCommand extends GenericCommand<DeleteSiteRequest,String>{
    @Override
    public void run(DeleteSiteRequest request) {
        XdsTools2Presenter.data().getToolkitServices().deleteSite(request,this);
    }
}
