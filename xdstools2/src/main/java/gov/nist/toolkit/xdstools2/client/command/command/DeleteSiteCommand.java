package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSiteRequest;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class DeleteSiteCommand extends GenericCommand<DeleteSiteRequest,String>{
    @Override
    public void run(DeleteSiteRequest request) {
        FrameworkInitialization.data().getToolkitServices().deleteSite(request,this);
    }
}
