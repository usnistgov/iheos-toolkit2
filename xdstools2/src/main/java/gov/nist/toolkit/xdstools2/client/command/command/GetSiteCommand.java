package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteRequest;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetSiteCommand extends GenericCommand<GetSiteRequest,Site> {
    @Override
    public void run(GetSiteRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getSite(request,this);
    }
}
